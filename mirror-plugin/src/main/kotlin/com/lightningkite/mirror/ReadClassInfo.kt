package com.lightningkite.mirror

class ReadClassInfo(
        val imports: List<String> = listOf(),
        val modifiers: List<ReadClassInfo.Modifier> = listOf(),
        val implements: List<ReadType> = listOf(),
        val packageName: String = "",
        val owner: String? = null,
        val name: String = "Type",
        val typeParameters: List<ReadTypeParameter> = listOf(),
        val enumValues: List<String>? = null,
        val annotations: List<AnnotationInfo> = listOf(),
        val fields: List<ReadSerializedFieldInfo> = listOf()
) {

    enum class Modifier {
        Sealed,
        Abstract,
        Data,
        Open,
        Interface;

        companion object {
            val map = values().associate { it.name.toLowerCase() to it }
        }
    }

    val reflectionName: String get() = "${owner ?: ""}${name}ClassInfo"
    val accessName: String get() = (if (owner == null) "" else owner + ".") + name

    val accessNameWithStars: String
        get() = (accessName + if (typeParameters.isNotEmpty())
            typeParameters.joinToString(",", "<", ">") { "*" }
        else
            "")
    val accessNameWithBound: String
        get() = ReadType(
                accessName,
                typeParameters.map { ReadTypeProjection(ReadType(it.name), ReadTypeProjection.Variance.EXACT) },
                false
        ).useMinimumBound(this)

    val requiredFields = fields.filter { !it.isOptional }
    val optionalFields = fields.filter { it.isOptional }
    val qualifiedName: String
        get() = "$packageName.$accessName"

    fun generateConstructor(): String {
        if (modifiers.contains(Modifier.Interface) || modifiers.contains(Modifier.Abstract) || modifiers.contains(Modifier.Sealed))
            return """
            |   override fun construct(map: Map<String, Any?>): $accessNameWithBound = throw NotImplementedError()
            """.trimMargin()
        return """
            |   override fun construct(map: Map<String, Any?>): $accessNameWithBound {
            |       //Gather variables
            |       ${
        requiredFields
                .joinToString("\n        ") {
                    "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as ${it.type.useMinimumBound(this)}"
                }
        }
        |           //Handle the optionals
            |       ${
        optionalFields
                .withIndex()
                .joinToString("\n        ") { (index, it) ->
                    if (it.default != null) {
                        //We have a default calculation?  Awesome!
                        "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as? ${it.type.useMinimumBound(this)} ?: ${it.default}"
                    } else if (it.type.isNullable) {
                        //Oh good, let's just use null if it's not there
                        "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as ${it.type.useMinimumBound(this)}"
                    } else {
                        //Well... I guess we'll retrieve it by calling the constructor an extra time.
                        val args = requiredFields.asSequence().map { it.name + " = " + it.name } + optionalFields.subList(0, index).asSequence().map { it.name + " = " + it.name }
                        "val ${it.name}:${it.type.useMinimumBound(this)} = map[\"${it.name}\"] as? ${it.type.useMinimumBound(this)} ?: (Fields.${it.name}.get($accessNameWithBound(${args.joinToString()})) as ${it.type.useMinimumBound(this)})"
                    }
                }
        }
            |       //Finally do the call
            |       return $accessNameWithBound(
            |           ${fields.joinToString(",\n            ") { it.name + " = " + it.name }}
            |       )
            |   }
            """.trimMargin()
    }

    fun filePart(): String = """
        |@Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST")
        |object $reflectionName: ClassInfo<$accessNameWithStars> {
        |
        |   override val kClass: KClass<$accessNameWithStars> = $accessName::class
        |   override val modifiers: List<ClassInfo.Modifier> = listOf(${modifiers.joinToString { "ClassInfo.Modifier." + it.name }})
        |
        |   override val implements: List<Type<*>> = ${implements.joinToString(", ", "listOf(", ")")}
        |
        |   override val packageName: String = "$packageName"
        |   override val owner: KClass<*>? = ${if (owner == null) null else "$owner::class"}
        |   override val ownerName: String? = ${if (owner == null) null else "\"$owner\""}
        |
        |   override val name: String = "$name"
        |   override val annotations: List<AnnotationInfo> = listOf(${annotations.joinToString()})
        |   override val enumValues: List<$accessNameWithStars>? = ${if (enumValues == null) "null" else enumValues.joinToString(", ", "listOf(", ")")}
        |
        |   object Fields {
        |       ${
    fields.joinToString("\n        ") { it.toString(this) }
    }
        |   }
        |
        |   override val fields:List<SerializedFieldInfo<$accessNameWithStars, *>> = listOf(${fields.joinToString { "Fields.${it.name}" }})
        |
        |${generateConstructor()}
        |
        |}
        |
    """.trimMargin()

    override fun toString(): String = """
        |//Generated by Lightning Kite's Mirror plugin
        |package $packageName
        |
        |${imports.joinToString { "import $it" }}
        |import com.lightningkite.mirror.info.*
        |import kotlin.reflect.KClass
        |
        |${filePart()}""".trimMargin()
}