package com.lightningkite.mirror.serialization

import com.lightningkite.mirror.info.ClassInfo
import com.lightningkite.mirror.info.ClassInfoRegistry
import com.lightningkite.mirror.info.localName


data class SerializationRegistry(
        val classInfoRegistry: ClassInfoRegistry,
        val externalNameToInfo: Map<String, ClassInfo<*>> = classInfoRegistry.values.associate {
            (it.annotations.find { it.name.endsWith("ExternalName") }?.name ?: it.localName) to it
        },
        val encoderConfigurators: Map<String, (Encoder<Any?>)->Unit> = mapOf(),
        val decoderConfigurators: Map<String, (Decoder<Any?>)->Unit> = mapOf(),
        val defineConfigurators: Map<String, (DefinitionRepository<Any>)->Unit> = mapOf()
){
    val kClassToExternalNameRegistry = externalNameToInfo.entries.associate { it.value.kClass to it.key }
    operator fun plus(other: SerializationRegistry):SerializationRegistry = SerializationRegistry(
            classInfoRegistry = this.classInfoRegistry + other.classInfoRegistry,
            externalNameToInfo = this.externalNameToInfo + other.externalNameToInfo,
            encoderConfigurators = this.encoderConfigurators + other.encoderConfigurators,
            decoderConfigurators = this.decoderConfigurators + other.decoderConfigurators,
            defineConfigurators = this.defineConfigurators + other.defineConfigurators
    )
    operator fun plus(other: ClassInfoRegistry):SerializationRegistry = SerializationRegistry(
            classInfoRegistry = this.classInfoRegistry + other,
            externalNameToInfo = this.externalNameToInfo + other.values.associate {
                (it.annotations.find { it.name.endsWith("ExternalName") }?.name ?: it.localName) to it
            },
            encoderConfigurators = this.encoderConfigurators,
            decoderConfigurators = this.decoderConfigurators,
            defineConfigurators = this.defineConfigurators
    )
}

operator fun ClassInfoRegistry.plus(other: SerializationRegistry):SerializationRegistry = other.plus(this)
fun ClassInfoRegistry.toSerializationRegistry():SerializationRegistry = SerializationRegistry(this)