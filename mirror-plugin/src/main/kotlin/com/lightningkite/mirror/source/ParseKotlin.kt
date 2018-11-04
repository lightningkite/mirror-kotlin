package com.lightningkite.mirror.source

import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener
import java.io.File

fun File.kotlinNode(): Node {
    val lexer = KotlinLexer(ANTLRFileStream(this.toString()))
    val tokenStream = CommonTokenStream(lexer)
    val parser = KotlinParser(tokenStream)
    val ruleNames = parser.ruleNames

    val lists = ArrayList<ArrayList<Node>>()
    lists.add(ArrayList())

    ParseTreeWalker.DEFAULT.walk(object : KotlinParserBaseListener() {

        val edges = listOf(
                "expression",
                "simpleIdentifier",
                "getter",
                "setter",
                "modifier"
        )
        val edgeIndices = edges.map { ruleNames.indexOf(it) }.toSet()
        var edgeDepth = 0
        var depth = 0

        override fun enterEveryRule(ctx: ParserRuleContext) {
            super.enterEveryRule(ctx)
            if (ctx.ruleIndex in edgeIndices) {
                edgeDepth++
            } else {
                if (edgeDepth > 0) return
                lists.add(ArrayList())
            }
        }

        override fun exitEveryRule(ctx: ParserRuleContext) {
            super.exitEveryRule(ctx)
            if (ctx.ruleIndex in edgeIndices) {
                lists.last().add(Node(
                        ruleNames[ctx.ruleIndex],
                        ctx.text,
                        listOf(),
                        listOf()
                ))
                edgeDepth--
            } else {
                if (edgeDepth > 0) return
                val pulled = lists.removeAt(lists.lastIndex)
                lists.last().add(Node(
                        ruleNames[ctx.ruleIndex],
                        null,
                        pulled,
                        ctx.children?.mapNotNull { (it as? TerminalNode)?.text } ?: listOf()
                ))
            }
        }
    }, parser.kotlinFile())

    return lists.first().first()
}