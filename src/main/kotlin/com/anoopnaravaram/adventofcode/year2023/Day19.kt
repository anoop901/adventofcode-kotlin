package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.linesGrouped
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken

class Day19 : PuzzleSolution(
    inputFilePath = "input/2023/day19/input.txt",
    exampleInput = """
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}

        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
    """.trimIndent(),
//    useInputFile = false
) {
    private val inputLineGroups = input.linesGrouped().toList()

    private enum class Category { X, M, A, S }

    private enum class Inequality {
        LESS, GREATER;

        fun evaluate(arg1: Int, arg2: Int): Boolean {
            return when (this) {
                LESS -> arg1 < arg2
                GREATER -> arg1 > arg2
            }
        }
    }

    private data class XMASRanges(val ranges: Map<Category, IntRange>) {
        companion object {
            val ALL = XMASRanges(Category.entries.associateWith { 1..4000 })
        }

        fun and(condition: Workflow.Condition): XMASRanges {
            val newRanges = ranges.toMutableMap()
            newRanges.compute(condition.category) { _, range ->
                requireNotNull(range)
                when (condition.inequality) {
                    Inequality.LESS -> range.first..range.last.coerceAtMost(condition.threshold - 1)
                    Inequality.GREATER -> range.first.coerceAtLeast(condition.threshold + 1)..range.last
                }
            }
            return XMASRanges(newRanges)
        }

        fun countParts(): Long {
            return Category.entries.map { category ->
                val range = requireNotNull(ranges[category])
                (range.last - range.first + 1).coerceAtLeast(0).toLong()
            }.reduce(Long::times)
        }
    }

    private data class Workflow(val steps: List<Step>) {
        data class Step(val condition: Condition?, val nextWorkflowName: String)
        data class Condition(val category: Category, val inequality: Inequality, val threshold: Int) {
            fun passes(part: Part): Boolean {
                val rating = requireNotNull(part.ratings[category])
                return inequality.evaluate(rating, threshold)
            }

            val inverse
                get() = when (inequality) {
                    Inequality.LESS -> Condition(category, Inequality.GREATER, threshold - 1)
                    Inequality.GREATER -> Condition(category, Inequality.LESS, threshold + 1)
                }
        }

        fun run(part: Part): String {
            return steps.first { it.condition == null || it.condition.passes(part) }.nextWorkflowName
        }
    }

    private data class Part(val ratings: Map<Category, Int>) {
        fun sumOfAllRatings() = ratings.values.sum()
    }

    private val workflows = inputLineGroups[0].associate { line ->
        val (_, name, workflowStr) = "([a-z]+)\\{(.*)}".toRegex().matchEntire(line)!!.groupValues
        val workflowStepsStrs = workflowStr.split(",")
        val steps = workflowStepsStrs.map { workflowStepStr ->
            if (workflowStepStr.contains(":")) {
                val (conditionStr, nextWorkflowName) = workflowStepStr.split(":")
                val category = when (conditionStr[0]) {
                    'x' -> Category.X
                    'm' -> Category.M
                    'a' -> Category.A
                    's' -> Category.S
                    else -> throw IllegalArgumentException()
                }
                val inequality = when (conditionStr[1]) {
                    '<' -> Inequality.LESS
                    '>' -> Inequality.GREATER
                    else -> throw IllegalArgumentException()
                }
                val threshold = conditionStr.drop(2).toInt()
                val condition = Workflow.Condition(category, inequality, threshold)
                Workflow.Step(condition, nextWorkflowName)
            } else {
                Workflow.Step(null, workflowStepStr)
            }
        }
        name to Workflow(steps)
    }

    private val startingWorkflowName = "in"

    private object PartGrammar : Grammar<Part>() {
        val leftBracket by literalToken("{")
        val rightBracket by literalToken("}")
        val comma by literalToken(",")
        val equals by literalToken("=")
        val numberToken by regexToken("\\d+")
        val categoryToken by regexToken("[xmas]")

        val number by numberToken use { text.toInt() }
        val category by categoryToken use {
            when (text) {
                "x" -> Category.X
                "m" -> Category.M
                "a" -> Category.A
                "s" -> Category.S
                else -> throw IllegalArgumentException()
            }
        }
        val rating = category * -equals * number map { (category, number) -> Pair(category, number) }

        override val rootParser by -leftBracket * separatedTerms(rating, comma) * -rightBracket map { ratings ->
            Part(ratings.toMap())
        }
    }

    private val parts = inputLineGroups[1].map { PartGrammar.parseToEnd(it) }

    private fun partIsAccepted(part: Part): Boolean {
        return (generateSequence(startingWorkflowName) {
            workflows[it]!!.run(part)
        }.first { it in listOf("A", "R") }) == "A"
    }

    override fun part1(): Number {
        return parts.filter { partIsAccepted(it) }.sumOf { it.sumOfAllRatings() }
    }

    private fun countAcceptedParts(
        workflowName: String = startingWorkflowName,
        outerRanges: XMASRanges = XMASRanges.ALL
    ): Long {
        val workflow = workflows[workflowName]
        requireNotNull(workflow)

        return sequence {
            var outerRangesAndPreviousNegations = outerRanges
            for (step in workflow.steps) {
                var currentRanges = outerRangesAndPreviousNegations
                if (step.condition != null) {
                    currentRanges = currentRanges.and(step.condition)
                }
                when (step.nextWorkflowName) {
                    "A" -> yield(currentRanges.countParts())
                    "R" -> yield(0)
                    else -> yield(countAcceptedParts(step.nextWorkflowName, currentRanges))
                }
                if (step.condition != null) {
                    outerRangesAndPreviousNegations = outerRangesAndPreviousNegations.and(step.condition.inverse)
                } else {
                    break
                }
            }
        }.sum()
    }

    override fun part2(): Number {
        return countAcceptedParts()
    }
}

fun main() {
    val solution = Day19()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
