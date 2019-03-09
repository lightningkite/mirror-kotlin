package com.lightningkite.mirror.representation

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.File

class ReadClassInfo(
        val imports: List<String> = listOf(),
        val modifiers: List<Modifier> = listOf(),
        val implements: List<ReadType> = listOf(),
        val packageName: String = "",
        val owner: String? = null,
        val name: String = "Type",
        val typeParameters: List<ReadTypeParameter> = listOf(),
        val enumValues: List<String>? = null,
        val annotations: List<AnnotationInfo> = listOf(),
        val fields: List<ReadFieldInfo> = listOf(),
        val hasCompanion: Boolean = false
) {
    val mirrorAnnotations get() = annotations.filter { it.name != "Suppress" }

    var fromFile: File? = null

    companion object {
        const val GENERATED_NOTICE = "AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT"
        val defaults:Map<String, String> = mapOf(
                "Unit" to "Unit",
                "Boolean" to "false",
                "Char" to "' '",
                "String" to "\"\"",
                "UByte" to "0.toUnsignedByte()",
                "UShort" to "0.toUnsignedShort()",
                "UInt" to "0U",
                "ULong" to "0UL",
                "Byte" to "0.toByte()",
                "Short" to "0.toShort()",
                "Int" to "0",
                "Long" to "0L",
                "Float" to "0f",
                "Double" to "0.0",
                "List" to "listOf()",
                "Map" to "mapOf()"
        )
    }

    enum class Modifier {
        Sealed,
        Abstract,
        Data,
        Open,
        Interface,
        Inline,
        Annotation,
        Object;

        companion object {
            val map = values().associate { it.name.toLowerCase() to it }
        }
    }

    val reflectionPackage: String @JsonIgnore get(){
        return if(packageName.startsWith("kotlin.")){
            "mirror.$packageName"
        } else packageName
    }
    val reflectionQualifiedName: String @JsonIgnore get(){
        return "$reflectionPackage.$reflectionName"
    }
    val reflectionQualifiedNameMin: String
        @JsonIgnore get() {
            return if (typeParameters.isEmpty()) reflectionQualifiedName
            else "$reflectionQualifiedName.minimal"
        }
    val reflectionName: String @JsonIgnore get() = "${owner ?: ""}${name}Mirror"
    val accessName: String @JsonIgnore get() = (if (owner == null) "" else owner + ".") + name

    val accessNameWithStars: String
        @JsonIgnore get() = (accessName + if (typeParameters.isNotEmpty())
            typeParameters.joinToString(",", "<", ">") { "*" }
        else
            "")
    val accessNameWithBound: String
        @JsonIgnore get() = ReadType(
                accessName,
                typeParameters.map { ReadTypeProjection(ReadType(it.name), ReadTypeProjection.Variance.INVARIANT) },
                false
        ).useMinimumBound(this)
    val accessNameWithArguments: String
        @JsonIgnore get() = (accessName + if (typeParameters.isNotEmpty())
            typeParameters.joinToString(",", "<", ">") { it.name }
        else
            "")

    @JsonIgnore val requiredFields = fields.filter { !it.optional }
    @JsonIgnore val optionalFields = fields.filter { it.optional }
    val qualifiedName: String
        @JsonIgnore get() = "$packageName.$accessName"

    @JsonIgnore val fullImports = (imports + "com.lightningkite.mirror.info.*" + "kotlin.reflect.KClass" + "kotlinx.serialization.*").distinct()
}
