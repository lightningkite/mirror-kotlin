//package com.lightningkite.mirror.old
//
//import emitStringActual
//
//
//fun TabWriter.write(item: KxvDirectory) = with(item) {
//
//    writeln("package ${qualifiedValName.substringBeforeLast('.')}")
//    writeln()
//    writeln("import com.lightningkite.mirror.info.*")
//    writeln()
//    if(classes.isNotEmpty()){
//        writelnList(
//                list = classes.filter { it.isNotBlank() },
//                prepend = "val ${qualifiedValName.substringAfterLast('.')} = listOf(",
//                suffix = ")",
//                howToWrite = { writeln(it) }
//        )
//    }
//    writeln()
//}
//
//fun TabWriter.write(item: ReadFileInfo) = with(item) {
//    writeln("package $packageName")
//    writeln()
//    writeln("import com.lightningkite.mirror.*")
//    for (generalImport in generalImports) {
//        writeln("import $generalImport.*")
//    }
//    for (specificImport in specificImports) {
//        writeln("import $specificImport")
//    }
//    writeln()
//    for (it in classes) {
//        write(it)
//        writeln()
//    }
//    writeln()
//}
//
//fun TabWriter.write(item: ReadClassInfo) = with(item) {
//    anyfy()
//    val selfType = ReadType(simpleName, false, (0 until typeParameters.size).map { ReadTypeProjection.STAR })
//    val selfTypeAny = ReadType(simpleName, false, (0 until typeParameters.size).map { ReadTypeProjection(ReadType("Any", true)) })
//
//    writeln("object $reflectiveObjectName: KxClass<${selfType.emitStringActual()}> {")
//    tabs++
//
//    writeln("object Fields {")
//    tabs++
//    for (decl in fields.values) {
//        writeln("val ${decl.name} by lazy { ")
//        tabs++
//        write(this, decl)
//        tabs--
//        writeln("}")
//    }
//    tabs--
//    writeln("}")
//
//    writeln("override val kclass get() = $simpleName::class")
//
//    writeln("override val implements: List<KxType> by lazy {")
//    tabs++
//    writelnList(
//            list = implements,
//            prepend = "listOf<KxType>(",
//            suffix = ")",
//            howToWrite = {write(it)}
//    )
//    tabs--
//    writeln("}")
//
//    writeln("override val simpleName: String = \"$simpleName\"")
//    writeln("override val qualifiedName: String = \"$packageName.$simpleName\"")
//
//    writeln("override val values: Map<String, KxValue<${selfType.emitStringActual()}, *>> by lazy {")
//    tabs++
//    writeln(fields.values.filter { !it.mutable }.joinToString(", ", "mapOf<String, KxValue<${selfType.emitStringActual()}, *>>(", ")"){ """"${it.name}" to Fields.${it.name}""" })
//    tabs--
//    writeln("}")
//
//    writeln("override val fields: Map<String, KxVariable<${selfType.emitStringActual()}, *>> by lazy {")
//    tabs++
//    writeln(fields.values.filter { it.mutable }.joinToString(", ", "mapOf<String, KxVariable<${selfType.emitStringActual()}, *>>(", ")"){ """"${it.name}" to Fields.${it.name}""" })
//    tabs--
//    writeln("}")
//
//    writeln("override val functions: List<KxFunction<*>> by lazy {")
//    tabs++
//    writelnList(
//            list = functions,
//            prepend = "listOf<KxFunction<*>>(",
//            suffix = ")",
//            howToWrite = {write(it)}
//    )
//    tabs--
//    writeln("}")
//
//    writeln("override val constructors: List<KxFunction<${selfType.emitStringActual()}>> by lazy {")
//    tabs++
//    writelnList(
//            list = constructors,
//            prepend = "listOf<KxFunction<${selfType.emitStringActual()}>>(",
//            suffix = ")",
//            howToWrite = {write(it)}
//    )
//    tabs--
//    writeln("}")
//
//    writelnList(
//            list = annotations,
//            prepend = "override val annotations: List<KxAnnotation> = listOf<KxAnnotation>(",
//            suffix = ")",
//            howToWrite = {write(it)}
//    )
//
//    writeln("override val modifiers: List<ClassInfoModifier> = listOf<ClassInfoModifier>(${modifiers.joinToString { "ClassInfoModifier." + it.name }})")
//
//    if (enumValues == null) {
//        writeln("override val enumValues: List<${selfType.emitStringActual()}>? = null")
//    } else {
//        writelnList(
//                list = enumValues!!,
//                prepend = "override val enumValues: List<${selfType.emitStringActual()}>? = listOf<${selfType.emitStringActual()}>(",
//                suffix = ")",
//                howToWrite = {writeln("$simpleName.$it")}
//        )
//    }
//
//    tabs--
//    writeln("}")
//}
//
//
//fun TabWriter.write(item: ReadTypeProjection) {
//    if (item.isStar) writeln("KxTypeProjection.STAR")
//    else {
//        writeln("KxTypeProjection(")
//        tabs++
//        writeln("type = ")
//        tabs++
//        write(item.type)
//        tabs--
//        writeln(",")
//        writeln("variance = KxVariance.${item.variance.name}")
//        tabs--
//        writeln(")")
//    }
//}
//
//fun TabWriter.write(item: ReadType) = with(item) {
//    writeln("KxType(")
//    tabs++
//
//    writeln("base = $base::class.kxReflect,")
//    writeln("nullable = $nullable,")
//
//    writelnList(
//            list = typeParameters,
//            prepend = "typeParameters = listOf(",
//            suffix = "),",
//            howToWrite = {write(it)}
//    )
//
//    writelnList(
//            list = annotations,
//            prepend = "annotations = listOf(",
//            suffix = ")",
//            howToWrite = {write(it)}
//    )
//
//    tabs--
//    writeln(")")
//}
//
//fun TabWriter.write(owner: ReadClassInfo, item: ReadField) = with(item) {
//
//    if (mutable) {
//        writeln("KxVariable<${owner.selfType.emitStringActual()}, ${type.emitStringActual()}>(")
//    } else {
//        writeln("KxValue<${owner.selfType.emitStringActual()}, ${type.emitStringActual()}>(")
//    }
//    tabs++
//
//    writeln("owner = ${owner.reflectiveObjectName},")
//    writeln("""name = "$name",""")
//
//    writeln("type = ")
//    tabs++
//    write(type)
//    tabs--
//    writeln(",")
//
//    writeln("get = { owner -> owner.$name as ${type.emitStringActual()} },")
//
//    if(mutable){
//        writeln("set = " + if (owner.typeParameters.isNotEmpty()) {
//            """{ owner, value -> (owner as ${owner.selfTypeAny.emitStringActual()}).$name = (value as ${type.emitStringActual()}) }"""
//        } else {
//            """{ owner, value -> owner.$name = value }"""
//        } + ",")
//    }
//
//    writeln("artificial = $artificial,")
//
//    writelnList(
//            list = annotations,
//            prepend = "annotations = listOf(",
//            suffix = ")",
//            howToWrite = {write(it)}
//    )
//
//    tabs--
//    writeln(")")
//}
//
//fun TabWriter.write(item: ReadAnnotationInfo) = with(item){
//    writeln("KxAnnotation(")
//    tabs++
//    writeln("""name = "$name",""")
//    writeln("arguments = listOf(${arguments.joinToString()})")
//    tabs--
//    writeln(")")
//}