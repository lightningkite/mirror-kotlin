//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package com.lightningkite.mirror.test

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*
import mirror.kotlin.*

object TestEnumMirror : MirrorEnum<TestEnum>() {
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<TestEnum> get() = TestEnum::class as KClass<TestEnum>
    override val modifiers: Array<Modifier> get() = arrayOf()
    override val packageName: String get() = "com.lightningkite.mirror.test"
    override val localName: String get() = "TestEnum"
    override val enumValues: Array<TestEnum> get() = arrayOf(TestEnum.ValueA,TestEnum.ValueB,TestEnum.ValueC)
}
