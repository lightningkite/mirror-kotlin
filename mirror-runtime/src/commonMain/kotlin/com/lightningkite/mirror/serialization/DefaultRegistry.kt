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
                "regex" to { coder: Encoder<Any?> ->
                    val stringEncoder = coder.encoder(String::class.type)
                    coder.addEncoder(Regex::class.type){ stringEncoder.invoke(this, it.pattern) }
                },
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
                    coder.addEncoder(ClassInfo::class) { type ->
                        if(type.nullable) return@addEncoder null
                        return@addEncoder { it ->
                            val value = it!!
                            stringEncoder.invoke(this, coder.registry.kClassToExternalNameRegistry[value.kClass]
                                    ?: throw IllegalArgumentException("Can't serialize ClassInfo $value because it's not registered."))
                        }
                    }
                    coder.addEncoder(FieldInfo::class) { type ->
                        if(type.nullable) return@addEncoder null
                        val ownerType = type.param(0)
                        return@addEncoder { it ->
                            val value = it!!
                            val ownerName = (coder.registry.kClassToExternalNameRegistry[value.owner.kClass]
                                    ?: throw IllegalArgumentException("Can't serialize ClassInfo $value because it's not registered."))
                            val name = if (value.owner.kClass == ownerType.type.kClass) value.name
                            else ownerName + "." + value.name
                            stringEncoder.invoke(this, name)
                        }
                    }
                }
        ),
        decoderConfigurators = mapOf(
                "regex" to { coder: Decoder<Any?> ->
                    val stringCoder = coder.decoder(String::class.type)
                    coder.addDecoder(Regex::class.type){ Regex(stringCoder.invoke(this)) }
                },
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
                "regex" to { definer: DefinitionRepository<Any> ->
                    val stringDefine = definer.definition(String::class.type)
                    definer.addDefinition(Regex::class.type, stringDefine)
                },
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