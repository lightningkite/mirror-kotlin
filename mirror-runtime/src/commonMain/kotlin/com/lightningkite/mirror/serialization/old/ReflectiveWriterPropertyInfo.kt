//package com.lightningkite.mirror.serialization
//
//import com.lightningkite.kotlinx.reflection.KxField
//import com.lightningkite.kotlinx.reflection.Type
//import com.lightningkite.kotlinx.reflection.kxReflect
//import com.lightningkite.kotlinx.reflection.untyped
//import kotlin.reflect.KClass
//
//data class ReflectiveWriterPropertyInfo<OUT, RESULT>(
//        val key: String,
//        val valueType: Type,
//        val getter: (Any) -> Any?,
//        val writerObtainer: () -> TypeWriter<OUT, RESULT>
//) {
//    val writer by lazy(writerObtainer)
//    @Suppress("NOTHING_TO_INLINE")
//    fun writeValue(out: OUT, on: Any): RESULT {
//        return writer.invoke(out, getter(on), valueType)
//    }
//}
//
//fun <OUT, RESULT> KClass<*>.reflectiveWriterData(
//        typeWriterRepository: TypeWriterRepository<OUT, RESULT>,
//        fields: List<KxField<*, *>> = kxReflect.values.values + kxReflect.variables.values
//): List<ReflectiveWriterPropertyInfo<OUT, RESULT>> = fields.mapNotNull {
//    if(it.artificial) null
//    else ReflectiveWriterPropertyInfo(
//            key = it.name,
//            valueType = it.type,
//            getter = it.get.untyped,
//            writerObtainer = { typeWriterRepository.writer(it.type.base.kClass) }
//    )
//}