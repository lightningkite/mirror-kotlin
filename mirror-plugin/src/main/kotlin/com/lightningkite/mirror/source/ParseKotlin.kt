package com.lightningkite.mirror.source

import com.lightningkite.mirror.representation.ReadClassInfo
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File

fun File.classes(): List<ReadClassInfo>{

    val lexer = KotlinLexer(ANTLRFileStream(this.toString()))
    val tokenStream = CommonTokenStream(lexer)
    val parser = KotlinParser(tokenStream)
    val listener = SourceListener()
    ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())
    return listener.classes
}