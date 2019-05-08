package com.lightningkite.mirror.output

import com.lightningkite.mirror.PolymorphicMirrorName
import com.lightningkite.mirror.TabWriter
import com.lightningkite.mirror.representation.*
import com.lightningkite.mirror.representation.ReadClassInfo.Companion.GENERATED_NOTICE

fun TabWriter.writeAnnotation(classInfo: ReadClassInfo) {
    line("//Generated by Lightning Kite's Mirror plugin")
    line("//$GENERATED_NOTICE")
    line("package ${classInfo.reflectionPackage}")
    line()
    for (import in classInfo.fullImports) {
        line("import $import")
    }
    line()
    line {
        if (classInfo.fields.isNotEmpty()) {
            append("data class ")
        } else {
            append("class ")
        }
        append(classInfo.accessName)
        append("Mirror(")
    }
    tab {
        classInfo.fields.forEachIndexed { index, field ->
            line {
                append("val ")
                append(field.name)
                append(": ")
                append(field.type.use)
                if (field.optional) {
                    if (field.default != null) {
                        append(" = ")
                        append(field.default)
                    } else if (field.type.nullable) {
                        append(" = null")
                    } else when (field.type.kclass) {
                        "Unit", "kotlin.Unit" -> append(" = Unit")
                        "Boolean", "kotlin.Boolean" -> append(" = false")
                        "Byte", "kotlin.Byte" -> append(" = 0")
                        "Short", "kotlin.Short" -> append(" = 0")
                        "Int", "kotlin.Int" -> append(" = 0")
                        "Long", "kotlin.Long" -> append(" = 0L")
                        "Float", "kotlin.Float" -> append(" = 0f")
                        "Double", "kotlin.Double" -> append(" = 0L")
                        "Char", "kotlin.Char" -> append(" = ' '")
                        "String", "kotlin.String" -> append(" = \"\"")
                        "KClass", "kotlin.reflect.KClass" -> when (classInfo.qualifiedName) {
                            "kotlinx.serialization.Serializable" -> append(" = KSerializer::class")
                        }
                    }
                }
                if (index != classInfo.fields.lastIndex) {
                    append(",")
                }
            }
        }
    }
    line("): MirrorAnnotation {")
    tab {
        line("override val annotationType: KClass<out Annotation> get() = ${classInfo.accessName}::class")
        line("override fun asMap(): Map<String, Any?> = mapOf(")
        tab {
            classInfo.fields.forEachIndexed { index, field ->
                line {
                    append("\"")
                    append(field.name)
                    append("\" to ")
                    append(field.name)
                    if (index != classInfo.fields.lastIndex) {
                        append(",")
                    }
                }
            }
        }
        line(")")
    }
    line("}")
}

fun ReadFieldInfo.toReadString(index: Int): String {
    return if (!this.type.nullable) {
        when (val kclass = this.type.kclass) {
            "Unit", "kotlin.Unit",
            "Boolean", "kotlin.Boolean",
            "Byte", "kotlin.Byte",
            "Short", "kotlin.Short",
            "Int", "kotlin.Int",
            "Long", "kotlin.Long",
            "Float", "kotlin.Float",
            "Double", "kotlin.Double",
            "Char", "kotlin.Char",
            "String", "kotlin.String" -> buildString {
                append(fieldName)
                append(" = decoderStructure.decode${kclass.removePrefix("kotlin.")}Element(this, ")
                append(index.toString())
                append(")")
            }
            else -> buildString {
                append(fieldName)
                append(" = decoderStructure.decodeSerializableElement(this, ")
                append(index.toString())
                append(", ")
                append(type.toString())
                append(")")
            }
        }
    } else {
        buildString {
            append(fieldName)
            append(" = decoderStructure.decodeSerializableElement(this, ")
            append(index.toString())
            append(", ")
            append(type.toString())
            append(")")
        }
    }
}

fun ReadFieldInfo.toWriteString(index: Int): String {
    // encodeSerializableElement(this@PairClassInfo, 0, typeA, obj.first)
    return if (!this.type.nullable) {
        when (val kclass = this.type.kclass) {
            "Unit", "kotlin.Unit" -> buildString {
                append("encoderStructure.encodeUnitElement(this, ")
                append(index.toString())
                append(")")
            }
            "Boolean", "kotlin.Boolean",
            "Byte", "kotlin.Byte",
            "Short", "kotlin.Short",
            "Int", "kotlin.Int",
            "Long", "kotlin.Long",
            "Float", "kotlin.Float",
            "Double", "kotlin.Double",
            "Char", "kotlin.Char",
            "String", "kotlin.String" -> buildString {
                append("encoderStructure.encode${kclass.removePrefix("kotlin.")}Element(this, ")
                append(index.toString())
                append(", obj.")
                append(name)
                append(")")
            }
            else -> buildString {
                append("encoderStructure.encodeSerializableElement(this, ")
                append(index.toString())
                append(", ")
                append(type.toString())
                append(", obj.")
                append(name)
                append(")")
            }
        }
    } else {
        buildString {
            append("encoderStructure.encodeSerializableElement(this, ")
            append(index.toString())
            append(", ")
            append(type.toString())
            append(", obj.")
            append(name)
            append(")")
        }
    }
}

fun ReadClassInfo.fullImportsWithMirrors(): List<String> {
    val i = ArrayList(fullImports)

    val mirrors = HashSet<String>()
    fun forType(type: ReadType) {
        mirrors.add(type.kclass)
        for (sub in type.typeArguments) {
            forType(sub.type)
        }
    }
    implements.forEach { forType(it) }
    annotations.forEach { mirrors += it.name }
    fields.forEach {
        forType(it.type)
        it.annotations.forEach { mirrors += it.name }
    }

    for (mirror in mirrors) {
        if (mirror[0].isLowerCase()) {
            if (!mirror.substringAfter("kotlin.").contains('.')) {
                //Skip, already imported
            } else if (mirror.startsWith("kotlin.")) {
                i.add("mirror." + mirror + "Mirror")
                continue
            } else {
                i.add(mirror + "Mirror")
                continue
            }
        }
        val search = "." + mirror.substringBefore('.', mirror)
        val postOwner = mirror.substringAfter('.', "")
        val append = if(postOwner.isEmpty()) "" else postOwner.filter { it.isLetterOrDigit() }

        val matching = i.find { it.endsWith(search) }
        if (matching != null) {
            println("Found match for $mirror - $matching;")
            if (matching.startsWith("kotlin.")) {
                i.add("mirror." + matching + append + "Mirror")
                continue
            } else {
                i.add(matching + append + "Mirror")
                continue
            }
        }

        println("Couldn't find anything for $mirror;")
    }
    return i.distinct()
}

fun TabWriter.writeMirror(classInfo: ReadClassInfo) {
    println("Writing ${classInfo.qualifiedName}")
    return when {
        ReadClassInfo.Modifier.Object in classInfo.modifiers -> writeObjectMirror(classInfo)
        ReadClassInfo.Modifier.Annotation in classInfo.modifiers -> writeAnnotation(classInfo)
        ReadClassInfo.Modifier.Abstract in classInfo.modifiers ||
                ReadClassInfo.Modifier.Sealed in classInfo.modifiers ||
                ReadClassInfo.Modifier.Interface in classInfo.modifiers -> {
            writeInterfaceMirror(classInfo)
        }
        classInfo.enumValues != null -> writeEnumMirror(classInfo)
        else -> writeNormalMirror(classInfo)
    }
}

fun TabWriter.writeMirrorCompanionObject(classInfo: ReadClassInfo){
    line("override val mirrorClassCompanion: MirrorClassCompanion? get() = Companion")
    line("companion object : MirrorClassCompanion {")
    tab {
        for(typeParam in classInfo.typeParameters){
            line("val ${typeParam.name}MirrorMinimal get() = ${typeParam.minimumBound(classInfo, maxResolutions = 2)}")
        }
        line()
        line {
            append("override val minimal = ")
            append(classInfo.reflectionName)
            append("(")
            append(classInfo.typeParameters.joinToString {
                buildString {
                    append("""TypeArgumentMirrorType("${it.name}", Variance.${it.projection.variance.name}, ${it.name}MirrorMinimal)""")
                }
            })
            append(")")
        }
        line {
            append("override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = ")
            append(classInfo.reflectionName)
            append("(")
            var index = 0
            append(classInfo.typeParameters.joinToString {
                buildString {
                    append("typeArguments[${index++}] as MirrorType<")
                    append(it.useMinimumBound(classInfo, maxResolutions = 3))
                    append(">")
                }

            })
            append(")")
        }
    }
    line("}")
}

fun TabWriter.writeInterfaceMirror(classInfo: ReadClassInfo) = with(classInfo) {

    line("//Generated by Lightning Kite's Mirror plugin")
    line("//$GENERATED_NOTICE")
    line("package ${classInfo.reflectionPackage}")
    line()
    for (import in classInfo.fullImportsWithMirrors()) {
        line("import $import")
    }
    line()
    if (typeParameters.isNotEmpty()) {
        line {
            append("data class ")
            append(reflectionName)
            append(typeParameters.joinToString(", ", "<", ">") { it.name + ": " + it.projection.type.use })
            append("(")
        }
        tab {
            typeParameters.forEachIndexed { index, readTypeParameter ->
                line {
                    append("val ")
                    append(readTypeParameter.name)
                    append("Mirror: MirrorType<")
                    append(readTypeParameter.name)
                    append(">")
                    if (index != typeParameters.lastIndex) {
                        append(",")
                    }
                }
            }
        }
        line {
            append(") : $PolymorphicMirrorName<")
            append(classInfo.accessNameWithArguments)
            append(">() {")
        }
        tab {
            line()
            writeMirrorCompanionObject(classInfo)
            line()
            line {
                append("override val typeParameters: Array<MirrorType<*>> get() = arrayOf(")
                append(typeParameters.joinToString { it.name + "Mirror" })
                append(")")
            }
        }
    } else {
        line {
            append("object ")
            append(reflectionName)
            append(" : $PolymorphicMirrorName<")
            append(classInfo.accessName)
            append(">() {")
        }
    }

    tab {

        line("""@Suppress("UNCHECKED_CAST")""")
        line {
            append("override val kClass: KClass<")
            append(classInfo.accessNameWithArguments)
            append("> get() = ")
            append(classInfo.accessName)
            append("::class as KClass<")
            append(classInfo.accessNameWithArguments)
            append(">")
        }

        line {
            append("override val modifiers: Array<Modifier> get() = arrayOf(")
            append(modifiers.joinToString { "Modifier." + it.name })
            append(")")
        }

        line("override val implements: Array<MirrorClass<*>> get() = arrayOf(${classInfo.implements.joinToString()})")
        line("override val packageName: String get() = \"${classInfo.packageName}\"")
        line("override val localName: String get() = \"${classInfo.accessName}\"")
        if (classInfo.owner != null) {
            line("override val owningClass: KClass<*>? get() = ${classInfo.owner}::class")
        }
        if (classInfo.hasCompanion) {
            line("override val companion: Any? get() = ${classInfo.accessName}.Companion")
        }
        if (classInfo.mirrorAnnotations.isNotEmpty()) {
            line {
                append("override val annotations: List<Annotation> = listOf(")
                append(classInfo.mirrorAnnotations.joinToString { it.name + "Mirror" + "(" + it.arguments.joinToString() + ")" })
                append(")")
            }
        }

    }

    line("}")
}

fun TabWriter.writeEnumMirror(classInfo: ReadClassInfo) = with(classInfo) {

    line("//Generated by Lightning Kite's Mirror plugin")
    line("//$GENERATED_NOTICE")
    line("package ${classInfo.reflectionPackage}")
    line()
    for (import in classInfo.fullImportsWithMirrors()) {
        line("import $import")
    }
    line()
    if (typeParameters.isNotEmpty()) {
        line {
            append("data class ")
            append(reflectionName)
            append(typeParameters.joinToString(", ", "<", ">") { it.name + ": " + it.projection.type.use })
            append("(")
        }
        tab {
            typeParameters.forEachIndexed { index, readTypeParameter ->
                line {
                    append("val ")
                    append(readTypeParameter.name)
                    append("Mirror: MirrorType<")
                    append(readTypeParameter.name)
                    append(">")
                    if (index != typeParameters.lastIndex) {
                        append(",")
                    }
                }
            }
        }
        line {
            append(") : MirrorEnum<")
            append(classInfo.accessNameWithArguments)
            append(">() {")
        }
        tab {
            line()
            writeMirrorCompanionObject(classInfo)
            line()
            line {
                append("override val typeParameters: Array<MirrorType<*>> get() = arrayOf(")
                append(typeParameters.joinToString { it.name + "Mirror" })
                append(")")
            }
        }
    } else {
        line {
            append("object ")
            append(reflectionName)
            append(" : MirrorEnum<")
            append(classInfo.accessName)
            append(">() {")
        }
    }

    tab {

        line("""@Suppress("UNCHECKED_CAST")""")
        line {
            append("override val kClass: KClass<")
            append(classInfo.accessNameWithArguments)
            append("> get() = ")
            append(classInfo.accessName)
            append("::class as KClass<")
            append(classInfo.accessNameWithArguments)
            append(">")
        }

        line {
            append("override val modifiers: Array<Modifier> get() = arrayOf(")
            append(modifiers.joinToString { "Modifier." + it.name })
            append(")")
        }

        line("override val packageName: String get() = \"${classInfo.packageName}\"")
        line("override val localName: String get() = \"${classInfo.accessName}\"")
        line {
            append("override val enumValues: Array<")
            append(classInfo.accessNameWithArguments)
            append("> get() = arrayOf(")
            for ((index, value) in classInfo.enumValues!!.withIndex()) {
                append(accessName)
                append(".")
                append(value)
                if (index != classInfo.enumValues.lastIndex) {
                    append(",")
                }
            }
            append(")")
        }
        if (classInfo.owner != null) {
            line("override val owningClass: KClass<*>? get() = ${classInfo.owner}::class")
        }
        if (classInfo.hasCompanion) {
            line("override val companion: Any? get() = ${classInfo.accessName}.Companion")
        }
        if (classInfo.mirrorAnnotations.isNotEmpty()) {
            line {
                append("override val annotations: List<Annotation> = listOf(")
                append(classInfo.mirrorAnnotations.joinToString { it.name + "Mirror" + "(" + it.arguments.joinToString() + ")" })
                append(")")
            }
        }

    }

    line("}")
}

fun TabWriter.writeNormalMirror(classInfo: ReadClassInfo) = with(classInfo) {

    line("//Generated by Lightning Kite's Mirror plugin")
    line("//$GENERATED_NOTICE")
    line("package ${classInfo.reflectionPackage}")
    line()
    for (import in classInfo.fullImportsWithMirrors()) {
        line("import $import")
    }
    //Additional mirrors loaded here

    line()
    if (typeParameters.isNotEmpty()) {
        line {
            append("data class ")
            append(reflectionName)
            append(typeParameters.joinToString(", ", "<", ">") { it.name + ": " + it.projection.type.use })
            append("(")
        }
        tab {
            typeParameters.forEachIndexed { index, readTypeParameter ->
                line {
                    append("val ")
                    append(readTypeParameter.name)
                    append("Mirror: MirrorType<")
                    append(readTypeParameter.name)
                    append(">")
                    if (index != typeParameters.lastIndex) {
                        append(",")
                    }
                }
            }
        }
        line {
            append(") : MirrorClass<")
            append(classInfo.accessNameWithArguments)
            append(">() {")
        }
        tab {
            line()
            writeMirrorCompanionObject(classInfo)
            line()
            line {
                append("override val typeParameters: Array<MirrorType<*>> get() = arrayOf(")
                append(typeParameters.joinToString { it.name + "Mirror" })
                append(")")
            }
        }
    } else {
        line {
            append("object ")
            append(reflectionName)
            append(" : MirrorClass<")
            append(classInfo.accessName)
            append(">() {")
        }
    }

    tab {

        line("""@Suppress("UNCHECKED_CAST")""")
        line {
            append("override val kClass: KClass<")
            append(classInfo.accessNameWithArguments)
            append("> get() = ")
            append(classInfo.accessName)
            append("::class as KClass<")
            append(classInfo.accessNameWithArguments)
            append(">")
        }

        line {
            append("override val modifiers: Array<Modifier> get() = arrayOf(")
            append(modifiers.joinToString { "Modifier." + it.name })
            append(")")
        }

        line("override val packageName: String get() = \"${classInfo.packageName}\"")
        line("override val localName: String get() = \"${classInfo.accessName}\"")
        line("override val implements: Array<MirrorClass<*>> get() = arrayOf(${classInfo.implements.joinToString()})")
        if (classInfo.owner != null) {
            line("override val owningClass: KClass<*>? get() = ${classInfo.owner}::class")
        }
        if (classInfo.hasCompanion) {
            line("override val companion: Any? get() = ${classInfo.accessName}.Companion")
        }
        if (classInfo.mirrorAnnotations.isNotEmpty()) {
            line {
                append("override val annotations: List<Annotation> = listOf(")
                append(classInfo.mirrorAnnotations.joinToString { it.name + "Mirror" + "(" + it.arguments.joinToString() + ")" })
                append(")")
            }
        }

        line()

        classInfo.fields.forEachIndexed { index, field ->
            line {
                append("val ")
                append(field.fieldName)
                append(": Field<")
                append(classInfo.accessNameWithArguments)
                append(",")
                append(field.type.use)
                append("> = Field(")
            }
            tab {
                line("owner = this,")
                line("index = $index,")
                line("name = \"${field.name}\",")
                line("type = ${field.type},")
                line("optional = ${field.optional},")
                line("get = { it.${field.name} },")
                if (field.mutable) {
                    line("set = { it, value -> it.${field.name} = value },")
                }
                line {
                    append("annotations = listOf<Annotation>(")
                    append(field.mirrorAnnotations.joinToString { it.name + "Mirror" + "(" + it.arguments.joinToString() + ")" })
                    append(")")
                }
            }
            line(")")
            line()
        }

        line {
            append("override val fields: Array<Field<")
            append(classInfo.accessNameWithArguments)
            append(", *>> = arrayOf(")
            append(fields.joinToString { it.fieldName })
            append(")")
        }

        line()

        line {
            append("override fun deserialize(decoder: Decoder): ")
            append(classInfo.accessNameWithArguments)
            append(" {")
        }
        tab {
            //Make a place to enter the values
            for (field in fields) {
                line("var ${field.name}Set = false")
                line("var ${field.fieldName}: ${field.type.copy(nullable = true).use} = null")
            }

            //Retrieve the values
            line {
                append("val decoderStructure = decoder.beginStructure(this")
                if (typeParameters.isNotEmpty()) {
                    for (t in typeParameters) {
                        append(", ")
                        append(t.name + "Mirror")
                    }
                }
                append(")")
            }
            line("loop@ while (true) {")
            tab {
                line("when (decoderStructure.decodeElementIndex(this)) {")
                tab {
                    line("CompositeDecoder.READ_ALL -> {")
                    tab {
                        //all reads in order
                        for ((index, field) in classInfo.fields.withIndex()) {
                            line(field.toReadString(index))
                            line("${field.name}Set = true")
                        }
                        line("break@loop")
                    }
                    line("}")
                    line("CompositeDecoder.READ_DONE -> break@loop")
                    //all reads prefixed with # ->
                    for ((index, field) in classInfo.fields.withIndex()) {
                        line("$index -> {")
                        tab {
                            line(field.toReadString(index))
                            line("${field.name}Set = true")
                        }
                        line("}")
                    }
                    line("else -> {}")
                }
                line("}")
            }
            line("}")
            line("decoderStructure.endStructure(this)")

            //Handle defaults
            for ((index, field) in classInfo.fields.withIndex()) {
                if (field.default != null) {
                    //We have a default calculation?  Awesome!
                    line("if(!${field.name}Set) {")
                    tab {
                        line("${field.fieldName} = ${field.default}")
                    }
                    line("}")
                } else if (field.type.nullable) {
                    //Oh good, let's just use null if it's not there
                } else if (field.optional) {
                    //Well... I guess we'll retrieve it by calling the constructor an extra time.
                    line("if(!${field.name}Set) {")
                    tab {
                        line {
                            append(field.fieldName)
                            append(" = ")
                            append(accessNameWithArguments)
                            append("(")
                        }
                        tab {
                            val fieldsSoFar = requiredFields + optionalFields.subList(0, index)
                            for ((subindex, subfield) in fieldsSoFar.withIndex()) {
                                line {
                                    append(subfield.name)
                                    append(" = ")
                                    append(subfield.fieldName)
                                    append(" as ")
                                    append(subfield.type.use)
                                    if (subindex != fieldsSoFar.lastIndex) {
                                        append(",")
                                    }
                                }
                            }
                        }
                        line {
                            append(").")
                            append(field.name)
                        }
                    }
                    line("}")
                } else {
                    line("if(!${field.name}Set) {")
                    tab {
                        line("throw MissingFieldException(\"${field.name}\")")
                    }
                    line("}")
                }
            }

            //Construct the object
            line {
                append("return ")
                append(accessNameWithArguments)
                append("(")
            }
            tab {
                for ((index, field) in fields.withIndex()) {
                    line {
                        append(field.name)
                        append(" = ")
                        append(field.fieldName)
                        append(" as ")
                        append(field.type.use)
                        if (index != fields.lastIndex) {
                            append(",")
                        }
                    }
                }
            }
            line(")")

        }
        line("}")

        line()

        line {
            append("override fun serialize(encoder: Encoder, obj: ")
            append(classInfo.accessNameWithArguments)
            append(") {")
        }
        tab {
            line {
                append("val encoderStructure = encoder.beginStructure(this")
                if (typeParameters.isNotEmpty()) {
                    for (t in typeParameters) {
                        append(", ")
                        append(t.name + "Mirror")
                    }
                }
                append(")")
            }
            for ((index, field) in classInfo.fields.withIndex()) {
                line(field.toWriteString(index))
            }
            line("encoderStructure.endStructure(this)")
        }
        line("}")
    }

    line("}")
}

fun TabWriter.writeObjectMirror(classInfo: ReadClassInfo) = with(classInfo) {

    line("//Generated by Lightning Kite's Mirror plugin")
    line("//$GENERATED_NOTICE")
    line("package ${classInfo.reflectionPackage}")
    line()
    for (import in classInfo.fullImportsWithMirrors()) {
        line("import $import")
    }
    //Additional mirrors loaded here

    line()
    line {
        append("object ")
        append(reflectionName)
        append(" : MirrorObject<")
        append(classInfo.accessName)
        append(">(")
        append(classInfo.accessName)
        append(") {")
    }

    tab {

        line("""@Suppress("UNCHECKED_CAST")""")
        line {
            append("override val kClass: KClass<")
            append(classInfo.accessNameWithArguments)
            append("> get() = ")
            append(classInfo.accessName)
            append("::class as KClass<")
            append(classInfo.accessNameWithArguments)
            append(">")
        }

        line {
            append("override val modifiers: Array<Modifier> get() = arrayOf(")
            append(modifiers.joinToString { "Modifier." + it.name })
            append(")")
        }

        line("override val packageName: String get() = \"${classInfo.packageName}\"")
        line("override val localName: String get() = \"${classInfo.accessName}\"")
        line("override val implements: Array<MirrorClass<*>> get() = arrayOf(${classInfo.implements.joinToString()})")
        if (classInfo.owner != null) {
            line("override val owningClass: KClass<*>? get() = ${classInfo.owner}::class")
        }
        if (classInfo.mirrorAnnotations.isNotEmpty()) {
            line {
                append("override val annotations: List<Annotation> = listOf(")
                append(classInfo.mirrorAnnotations.joinToString { it.name + "Mirror" + "(" + it.arguments.joinToString() + ")" })
                append(")")
            }
        }

        line()

    }

    line("}")
}