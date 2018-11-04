//package com.lightningkite.mirror.old
//
///**
// * Changes type parameters to minimal implementations.
// * This is really freaking complicated.
// */
//fun ReadClassInfo.anyfy(){
//
//    //This section takes the type parameters and ensures that self-referencing type parameters work
//    //For example, this makes TestClass<T: Comparable<T>> work beautifully.
//    //This works by making typeless implementations TestClass<Comparable<Comparable<*>>.
//    //Why Kotlin accepts this I do not fully understand, but it works.
//    val vanillafiedTypeParameters = typeParameters.map {
//        it.copy(minimum = it.minimum.deepCopy())
//    }
//    vanillafiedTypeParameters.forEach { typeParam ->
//        typeParam.minimum.recursiveSubs().forEach { sub ->
//            if(sub is ReadTypeProjection){
//                val tp = typeParameters.find { it.name == sub.type.base }
//                if(tp != null){
//                    sub.type = ReadTypeProjection.STAR.type
//                    sub.isStar = ReadTypeProjection.STAR.isStar
//                    sub.variance = ReadTypeProjection.STAR.variance
//                }
//            }
//        }
//    }
//    typeParameters.forEach { typeParam ->
//        typeParam.minimum.recursiveSubs().forEach { sub ->
//            if(sub is ReadTypeProjection){
//                val tp = vanillafiedTypeParameters.find { it.name == sub.type.base }
//                if(tp != null){
//                    sub.type = tp.minimum
//                }
//            }
//        }
//    }
//
//
//    recursiveSubs().toList().forEach { sub ->
//        if(sub is ReadType){
//            val tp = typeParameters.find { it.name == sub.base }
//            if(tp != null){
//                sub.base = tp.minimum.base + "/*${tp.name}*/"
//                sub.nullable = tp.minimum.nullable
//                sub.typeParameters = tp.minimum.typeParameters
//            }
//        }
//    }
//}