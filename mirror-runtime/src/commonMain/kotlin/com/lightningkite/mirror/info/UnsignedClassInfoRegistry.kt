@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.lightningkite.mirror.info

import com.lightningkite.kommon.native.SharedImmutable

@SharedImmutable
val UnsignedClassInfoRegistry : ClassInfoRegistry = ClassInfoRegistry(
        UByteClassInfo,
        UShortClassInfo,
        UIntClassInfo,
        ULongClassInfo
)

object UByteClassInfo : EmptyClassInfo<UByte>(UByte::class, "kotlin", "UByte")
object UShortClassInfo : EmptyClassInfo<UShort>(UShort::class, "kotlin", "UShort")
object UIntClassInfo : EmptyClassInfo<UInt>(UInt::class, "kotlin", "UInt")
object ULongClassInfo : EmptyClassInfo<ULong>(ULong::class, "kotlin", "ULong")