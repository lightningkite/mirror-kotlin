//package com.lightningkite.mirror.info
//
//import kotlinx.serialization.internal.EnumDescriptor
//import kotlin.reflect.KClass
//
//interface Encoder {
//    fun <T : Any> encoder(type: Type<T>): Encoder.(value: T?) -> Unit
//    fun <T : Any> encodePolymorphic(typeLimit: Type<T>, value: T?)
//    //Maybe raw types?
//
//
//    fun <T : Any> encode(type: Type<T>, value: T?) = encoder(type).invoke(this, value)
//}
//
//interface Decoder {
//    fun <T : Any> decoder(type: Type<T>): Decoder.() -> T?
//    fun <T : Any> decodePolymorphic(typeLimit: Type<T>): T?
//
//    fun <T : Any> decode(type: Type<T>): T? = decoder(type).invoke(this)
//}
//
//class Type<T : Any>(
//    val kClass: KClass<T>,
//    val typeArguments: List<TypeProjection> = listOf(),
//    val isNullable: Boolean = false
//)
//
//class TypeProjection(
//    val bound: Type<*>,
//    val variance: Variance
//) {
//    enum class Variance {
//        IN, OUT, EXACT, STAR
//    }
//}
//
//interface ClassInfo<T : Any> {
//    val kClass: KClass<T>
//    val typeModifiers: List<Modifier>
//    val parentTypes: List<Type<*>>
//    val packageName: String
//    val owner: KClass<*>?
//    val name: String
//    val typeParameters: List<TypeProjection>
//    val enumValues: List<T>?
//    val annotations: List<AnnotationInfo>
//    val serializedFields: List<SerializedFieldInfo>
//
//    fun construct(parameters: Map<String, Any?>): T
//
//    enum class Modifier {
//        Sealed,
//        Abstract,
//        Data,
//        Open,
//        Interface
//    }
//}
//
//class SerializedFieldInfo(
//    val name: String,
//    val type: Type<*>,
//    val isOptional: Boolean,
//    val annotations: List<AnnotationInfo>
//)
//
//class AnnotationInfo(
//    val name: String,
//    val arguments: List<Any?>
//)