//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.info

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*
import mirror.kotlin.*

data class SubtypeMirror(
    val detail: String
): MirrorAnnotation {
    override val annotationType: KClass<out Annotation> get() = Subtype::class
    override fun asMap(): Map<String, Any?> = mapOf(
        "detail" to detail
    )
}
