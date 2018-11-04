package com.lightningkite.kotlinx.persistence

import com.lightningkite.kotlinx.reflection.ExternalReflection

@ExternalReflection
data class ChangeEvent<T : Model<ID>, ID>(val item: T, val type: ChangeEvent.Type) {

    @ExternalReflection
    enum class Type {
        Insertion, Modification, Deletion
    }
}
