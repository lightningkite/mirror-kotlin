//package com.lightningkite.mirror.archive.model
//
//import com.lightningkite.mirror.info.MirrorType
//
//
//val <T: Any> MirrorType<T>.modification get() = Type<Operation<T>>(Operation::class, listOf(TypeProjection(this), TypeProjection.STAR))
//val <T: Any> MirrorType<T>.condition get() = Type<Condition<T>>(Condition::class, listOf(TypeProjection(this)))
//val <T: Any> MirrorType<T>.sort get() = Type<Sort<T>>(Sort::class, listOf(TypeProjection(this), TypeProjection.STAR))
//val <T: Any> MirrorType<T>.queryResult get() = Type<QueryResult<T>>(QueryResult::class, listOf(TypeProjection(this)))