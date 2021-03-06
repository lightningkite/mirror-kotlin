package com.lightningkite.mirror.source

import com.lightningkite.mirror.representation.*
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener

class SourceListener : KotlinParserBaseListener() {
    val classes = ArrayList<ReadClassInfo>()
    var currentPackage = ""
    val imports = ArrayList<String>()
    val multiImports = ArrayList<String>()
    val ownerChain = ArrayList<String>()

    override fun enterPackageHeader(ctx: KotlinParser.PackageHeaderContext) {
        currentPackage = ctx.identifier()?.text ?: ""
    }

    override fun enterKotlinFile(ctx: KotlinParser.KotlinFileContext?) {
        currentPackage = ""
        imports.clear()
    }

    override fun enterImportHeader(ctx: KotlinParser.ImportHeaderContext) {
        if (ctx.MULT() != null) {
            multiImports.add(ctx.identifier().text)
        } else {
            imports.add(ctx.identifier().text)
        }
    }

    fun KotlinParser.TypeContext.convert(): ReadType {
        nullableType()?.let {
            return (it.typeReference()?.convert() ?: it.parenthesizedType().type().convert()).copy(nullable = true)
        }
        typeReference()?.let {
            return it.convert()
        }
        parenthesizedType()?.let {
            return it.type().convert()
        }
        functionType()?.let {
            //We can parse this maybe
        }
        return ReadType("Any", nullable = true)
    }

    fun KotlinParser.TypeReferenceContext.convert(): ReadType {
        return userType()?.convert() ?: typeReference()!!.convert()
    }

    fun KotlinParser.UserTypeContext.convert(typeArgumentsOverride: KotlinParser.TypeArgumentsContext? = null): ReadType {
        val last = this.simpleUserType().last() ?: return ReadType("ERROR")
        val prefix = this.simpleUserType().dropLast(1).joinToString(".") { it.simpleIdentifier().text }.let {
            if (it.isBlank()) it
            else "$it."
        }
        return ReadType(
                kclass = prefix + last.simpleIdentifier().text,
                typeArguments = (typeArgumentsOverride ?: last.typeArguments())?.typeProjection()?.map { it.convert() }
                        ?: listOf(),
                nullable = false
        )
    }

    fun KotlinParser.TypeProjectionContext.convert(): ReadTypeProjection {
        if (MULT() != null) {
            return ReadTypeProjection(type = ReadType("Any", nullable = true), variance = ReadTypeProjection.Variance.STAR)
        }
        return ReadTypeProjection(
                type = type().convert(),
                variance = this.typeProjectionModifierList()?.varianceAnnotation()?.first()?.let {
                    when {
                        it.IN() != null -> ReadTypeProjection.Variance.IN
                        it.OUT() != null -> ReadTypeProjection.Variance.OUT
                        else -> ReadTypeProjection.Variance.INVARIANT
                    }
                } ?: ReadTypeProjection.Variance.INVARIANT
        )
    }

    fun KotlinParser.TypeParameterContext.convert(): ReadTypeParameter {
        var variance = ReadTypeProjection.Variance.INVARIANT
        this.modifierList()?.modifier()?.forEach {
            it.varianceAnnotation()?.let {
                if(it.IN() != null) variance = ReadTypeProjection.Variance.IN
                if(it.OUT() != null) variance = ReadTypeProjection.Variance.OUT
            }
        }
        return ReadTypeParameter(
                name = this.simpleIdentifier().text,
                projection = this.type()?.let { ReadTypeProjection(type = it.convert(), variance = variance) }
                        ?: ReadTypeProjection(ReadType("Any", nullable = true), variance)
        )
    }

    fun KotlinParser.AnnotationContext.convert(): AnnotationInfo {
        this.LabelReference()?.let {
            return AnnotationInfo(
                    name = it.text.trimStart('@'),
                    arguments = this.valueArguments()?.valueArgument()?.map { it.text } ?: listOf(),
                    useSiteTarget = null
            )
        }
        val unescaped = this.unescapedAnnotation()
        return AnnotationInfo(
                useSiteTarget = this.annotationUseSiteTarget()?.text,
                name = unescaped.identifier().text,
                arguments = unescaped.valueArguments()?.valueArgument()?.map { it.text } ?: listOf()
        )
    }

    fun KotlinParser.ClassParameterContext.convert(): ReadFieldInfo {
        return ReadFieldInfo(
                name = this.simpleIdentifier().text,
                type = this.type().convert(),
                optional = this.expression() != null,
                mutable = this.VAR() != null,
                annotations = this.modifierList()?.annotations()?.mapNotNull { it.annotation()?.convert() } ?: listOf(),
                default = this.expression()?.text
        )
    }

    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        val info = ReadClassInfo(
                packageName = currentPackage,
                imports = imports + multiImports.map { it + ".*" },
                modifiers = (ctx.modifierList()?.modifier()?.mapNotNull {
                    it.classModifier()?.let {
                        when {
                            it.DATA() != null -> ReadClassInfo.Modifier.Data
                            it.SEALED() != null -> ReadClassInfo.Modifier.Sealed
                            it.ANNOTATION() != null -> ReadClassInfo.Modifier.Annotation
                            else -> null
                        }
                    } ?: it.inheritanceModifier()?.let {
                        when {
                            it.ABSTRACT() != null -> ReadClassInfo.Modifier.Abstract
                            it.OPEN() != null -> ReadClassInfo.Modifier.Open
                            else -> null
                        }
                    } ?: it.functionModifier()?.let {
                        when {
                            it.INLINE() != null -> ReadClassInfo.Modifier.Inline
                            else -> null
                        }
                    }
                }
                        ?: listOf()).plus(if (ctx.INTERFACE() != null) listOf(ReadClassInfo.Modifier.Interface) else listOf()),
                implements = ctx.delegationSpecifiers()?.delegationSpecifier()?.mapNotNull {
                    it.userType()?.convert() ?: it.constructorInvocation()?.userType()?.convert(
                            typeArgumentsOverride = it.constructorInvocation()?.callSuffixLambdaless()?.typeArguments()
                    )
                } ?: listOf(),
                owner = if (ownerChain.isEmpty()) null else ownerChain.joinToString("."),
                name = ctx.simpleIdentifier().text,
                typeParameters = ctx.typeParameters()?.typeParameter()?.map { it.convert() } ?: listOf(),
                enumValues = ctx.enumClassBody()?.enumEntries()?.enumEntry()?.map { it.simpleIdentifier().text },
                annotations = ctx.modifierList()?.annotations()?.map { it.annotation().convert() } ?: listOf(),
                fields = ctx.primaryConstructor()?.classParameters()?.classParameter()?.mapNotNull {
                    if (it.VAL() != null || it.VAR() != null) {
                        it.convert()
                    } else null
                } ?: listOf(),
                hasCompanion = ctx.classBody()?.classMemberDeclaration()?.any { it.companionObject() != null }
                        ?: ctx.enumClassBody()?.classMemberDeclaration()?.any { it.companionObject() != null } ?: false
        )
        if(info.fields.any { it.type.kclass.isBlank() }) {
            throw IllegalStateException()
        }
        classes.add(info)
        println("Read: ${classes.last().name} -  ${classes.last().owner} - ${classes.last().packageName} - ${classes.last().qualifiedName}")
        ownerChain.add(ctx.simpleIdentifier().text)
    }

    override fun exitClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) {
        ownerChain.removeAt(ownerChain.lastIndex)
    }

    override fun enterObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext) {
        classes.add(ReadClassInfo(
                packageName = currentPackage,
                imports = imports + multiImports.map { it + ".*" },
                modifiers = (ctx.modifierList()?.modifier()?.mapNotNull {
                    it.classModifier()?.let {
                        when {
                            it.DATA() != null -> ReadClassInfo.Modifier.Data
                            it.SEALED() != null -> ReadClassInfo.Modifier.Sealed
                            else -> null
                        }
                    } ?: it.inheritanceModifier()?.let {
                        when {
                            it.ABSTRACT() != null -> ReadClassInfo.Modifier.Abstract
                            it.OPEN() != null -> ReadClassInfo.Modifier.Open
                            else -> null
                        }
                    } ?: it.functionModifier()?.let {
                        when {
                            it.INLINE() != null -> ReadClassInfo.Modifier.Inline
                            else -> null
                        }
                    }
                } ?: listOf()).plus(listOf(ReadClassInfo.Modifier.Object)),
                implements = ctx.delegationSpecifiers()?.delegationSpecifier()?.mapNotNull {
                    it.userType()?.convert() ?: it.constructorInvocation()?.userType()?.convert(
                            typeArgumentsOverride = it.constructorInvocation()?.callSuffixLambdaless()?.typeArguments()
                    )
                } ?: listOf(),
                owner = if (ownerChain.isEmpty()) null else ownerChain.joinToString("."),
                name = ctx.simpleIdentifier().text,
                typeParameters = listOf(),
                enumValues = null,
                annotations = ctx.modifierList()?.annotations()?.map { it.annotation().convert() } ?: listOf(),
                fields = ctx.primaryConstructor()?.classParameters()?.classParameter()?.mapNotNull {
                    if (it.VAL() != null || it.VAR() != null) {
                        it.convert()
                    } else null
                } ?: listOf()
        ))
        println("Read: ${classes.last().name} -  ${classes.last().owner} - ${classes.last().packageName} - ${classes.last().qualifiedName}")
        ownerChain.add(ctx.simpleIdentifier().text)
    }

    override fun exitObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext?) {
        ownerChain.removeAt(ownerChain.lastIndex)
    }
}