package com.lightningkite.mirror.serialization

import com.lightningkite.kommon.native.SharedImmutable
import com.lightningkite.mirror.info.ClassInfo
import com.lightningkite.mirror.info.FieldInfo
import com.lightningkite.mirror.info.PrimitiveClassInfoRegistry
import com.lightningkite.mirror.info.type
import kotlin.reflect.KClass

@SharedImmutable
val DefaultRegistry = SerializationRegistry(
        classInfoRegistry = PrimitiveClassInfoRegistry,
        encoderConfigurators = mapOf(
                "enum" to { it: Encoder<Any?> ->
                    it.addEncoder(EnumCoderGenerators.EncoderGenerator(it))
                },
                "reflective" to { coder: Encoder<Any?> ->
                    val stringEncoder = coder.encoder(String::class.type)
                    coder.addEncoder(KClass::class) { _ ->
                        return@addEncoder {
                            stringEncoder.invoke(this, coder.registry.kClassToExternalNameRegistry[it]
                                    ?: throw IllegalArgumentException("Can't serialize KClass $it because it's not registered."))
                        }
                    }
                    coder.addEncoder(ClassInfo::class) { _ ->
                        return@addEncoder {
                            stringEncoder.invoke(this, coder.registry.kClassToExternalNameRegistry[it.kClass]
                                    ?: throw IllegalArgumentException("Can't serialize ClassInfo $it because it's not registered."))
                        }
                    }
                    coder.addEncoder(FieldInfo::class) { type ->
                        val ownerType = type.param(0)
                        return@addEncoder {
                            val ownerName = (coder.registry.kClassToExternalNameRegistry[it.owner.kClass]
                                    ?: throw IllegalArgumentException("Can't serialize ClassInfo $it because it's not registered."))
                            val name = if (it.owner.kClass == ownerType.type.kClass) it.name
                            else ownerName + "." + it.name
                            stringEncoder.invoke(this, name)
                        }
                    }
                }
        ),
        decoderConfigurators = mapOf(
                "enum" to { it: Decoder<Any?> ->
                    it.addDecoder(EnumCoderGenerators.DecoderGenerator(it))
                },
                "reflective" to { coder: Decoder<Any?> ->
                    val stringDecoder = coder.decoder(String::class.type)
                    coder.addDecoder(KClass::class) { _ ->
                        return@addDecoder {
                            coder.registry.externalNameToInfo[stringDecoder.invoke(this)]!!.kClass
                        }
                    }
                    coder.addDecoder(ClassInfo::class) { _ ->
                        return@addDecoder {
                            coder.registry.externalNameToInfo[stringDecoder.invoke(this)]!!
                        }
                    }
                    coder.addDecoder(FieldInfo::class) { type ->
                        val ownerType = coder.registry.classInfoRegistry[type.param(0).type.kClass]
                        return@addDecoder {
                            val text = stringDecoder.invoke(this)
                            if(text.contains('.')){
                                val className = text.substringBeforeLast('.')
                                val fieldName = text.substringAfterLast('.')
                                val foundType = coder.registry.externalNameToInfo[className]
                                        ?: throw IllegalArgumentException("No info found for a type with external name $className")
                                foundType.fields.find { it.name == fieldName }!!
                            } else {
                                ownerType!!.fields.find { it.name == text }!!
                            }
                        }
                    }
                }
        ),
        defineConfigurators = mapOf(
                "enum" to { definer: DefinitionRepository<Any> ->
                    definer.addDefinition(EnumCoderGenerators.DefineGenerator(definer))
                },
                "reflective" to { coder: DefinitionRepository<Any> ->
                    val stringDefine = coder.definition(String::class.type)
                    coder.addDefinition(KClass::class.type, stringDefine)
                    coder.addDefinition(ClassInfo::class.type, stringDefine)
                    coder.addDefinition(FieldInfo::class.type, stringDefine)
                }
        )
)