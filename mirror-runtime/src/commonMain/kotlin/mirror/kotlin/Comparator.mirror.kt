//Generated by Lightning Kite's Mirror plugin
//AUTOMATICALLY GENERATED AND WILL BE OVERRIDDEN IF THIS MESSAGE IS PRESENT
package mirror.kotlin

import com.lightningkite.mirror.info.*
import kotlin.reflect.KClass
import kotlinx.serialization.*
import mirror.kotlin.*

data class ComparatorMirror<T: Any?>(
    val TMirror: MirrorType<T>
) : PolymorphicMirror<Comparator<T>>() {
    
    override val mirrorClassCompanion: MirrorClassCompanion? get() = Companion
    companion object : MirrorClassCompanion {
        val TMirrorMinimal get() = AnyMirror.nullable
        
        override val minimal = ComparatorMirror(TypeArgumentMirrorType("T", Variance.INVARIANT, TMirrorMinimal))
        @Suppress("UNCHECKED_CAST")
        override fun make(typeArguments: List<MirrorType<*>>): MirrorClass<*> = ComparatorMirror(typeArguments[0] as MirrorType<Any?>)
        
        @Suppress("UNCHECKED_CAST")
        fun make(
            TMirror: MirrorType<*>? = null
        ) = ComparatorMirror<Any?>(
            TMirror = (TMirror ?: TMirrorMinimal) as MirrorType<Any?>
        )
    }
    
    override val typeParameters: Array<MirrorType<*>> get() = arrayOf(TMirror)
    @Suppress("UNCHECKED_CAST")
    override val kClass: KClass<Comparator<T>> get() = Comparator::class as KClass<Comparator<T>>
    override val modifiers: Array<Modifier> get() = arrayOf(Modifier.Interface, Modifier.Abstract)
    override val implements: Array<MirrorClass<*>> get() = arrayOf(AnyMirror)
    override val packageName: String get() = "kotlin"
    override val localName: String get() = "Comparator"
}
