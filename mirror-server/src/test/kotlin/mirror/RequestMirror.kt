//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.request

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*

class RequestMirror<T : Any?>(
        val TMirror: MirrorType<T>
) : PolymorphicMirror<Request<T>>() {

    companion object {
        val minimal = RequestMirror(AnyMirror.nullable)
    }

    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(TMirror)
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<Request<T>>
        get() = Request::class as KClass<Request<T>>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Interface)
    override val implements: Array<MirrorClass<*>> get() = arrayOf()
    override val packageName: String get() = "com.lightningkite.mirror.request"
    override val localName: String get() = "Request"
}
