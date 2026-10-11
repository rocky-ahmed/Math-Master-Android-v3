package com.example

import com.example.math.MathProblemSolverEngine
import com.example.math.SolverMode
import com.example.math.SolverTopic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Focused unit tests for Phase 4: Math Problem Solver.
 * Covers all 13 supported topics, 3 learning modes, verification, and mathematical edge cases.
 */
class MathProblemSolverUnitTest {

    // -------------------------------------------------------------
    // 1. BASIC ARITHMETIC & BODMAS
    // -------------------------------------------------------------
    @Test
    fun testArithmeticBodmasCorrectness() {
        val res = MathProblemSolverEngine.solveArithmetic("(১৫ + ৫) × ৪ ÷ ২ - ৬", SolverMode.LEARNING)
        assertTrue(res.isSuccess)
        // (15 + 5) * 4 / 2 - 6 = 20 * 4 / 2 - 6 = 80 / 2 - 6 = 40 - 6 = 34
        assertTrue(res.finalAnswerBn.contains("৩৪"))
        assertTrue(res.steps.isNotEmpty())
        assertNotNull(res.verificationBn)
    }

    @Test
    fun testArithmeticParenthesesError() {
        val unbalanced = MathProblemSolverEngine.solveArithmetic("(১৫ + ৫ × ৪", SolverMode.QUICK)
        assertFalse(unbalanced.isSuccess)
        assertTrue(unbalanced.errorMessageBn?.contains("বন্ধনীর") == true)
    }

    @Test
    fun testArithmeticDivisionByZero() {
        val divZero = MathProblemSolverEngine.solveArithmetic("২৫ ÷ (৫ - ৫)", SolverMode.LEARNING)
        assertFalse(divZero.isSuccess)
        assertTrue(divZero.errorMessageBn?.contains("শূন্য") == true || divZero.errorMessageBn?.contains("০") == true)
    }

    // -------------------------------------------------------------
    // 2. FRACTIONS & DECIMALS
    // -------------------------------------------------------------
    @Test
    fun testFractionAdditionAndSimplification() {
        // 3/4 + 2/5 = (15 + 8)/20 = 23/20 = 1 3/20
        val res = MathProblemSolverEngine.solveFraction(3L, 4L, "+", 2L, 5L, SolverMode.LEARNING)
        assertTrue(res.isSuccess)
        assertTrue(res.finalAnswerBn.contains("২৩/২০"))
        // Check LCM step
        val lcmStep = res.steps.find { it.titleBn.contains("ল.সা.গু") }
        assertNotNull(lcmStep)
        assertTrue(lcmStep?.expressionOrMathBn?.contains("২০") == true)
    }

    @Test
    fun testFractionReductionByGcd() {
        // 3/4 - 1/4 = 2/4 = 1/2
        val res = MathProblemSolverEngine.solveFraction(3L, 4L, "-", 1L, 4L, SolverMode.QUICK)
        assertTrue(res.isSuccess)
        assertTrue(res.finalAnswerBn.contains("১/২"))
    }

    @Test
    fun testFractionMultiplicationAndDivision() {
        // 2/3 * 3/4 = 6/12 = 1/2
        val mul = MathProblemSolverEngine.solveFraction(2L, 3L, "*", 3L, 4L, SolverMode.LEARNING)
        assertTrue(mul.isSuccess)
        assertTrue(mul.finalAnswerBn.contains("১/২"))

        // 3/4 / 2/5 = 3/4 * 5/2 = 15/8
        val div = MathProblemSolverEngine.solveFraction(3L, 4L, "/", 2L, 5L, SolverMode.BCS)
        assertTrue(div.isSuccess)
        assertTrue(div.finalAnswerBn.contains("১৫/৮"))
        assertNotNull(div.bcsShortcutBn)
    }

    @Test
    fun testFractionZeroDenominator() {
        val zeroDen = MathProblemSolverEngine.solveFraction(3L, 0L, "+", 2L, 5L, SolverMode.LEARNING)
        assertFalse(zeroDen.isSuccess)
    }

    // -------------------------------------------------------------
    // 3. PERCENTAGE
    // -------------------------------------------------------------
    @Test
    fun testPercentageCalculations() {
        // 15% of 500 = 75
        val p1 = MathProblemSolverEngine.solvePercentage(
            MathProblemSolverEngine.PercentageType.OF_NUMBER,
            15.0,
            500.0,
            SolverMode.LEARNING
        )
        assertTrue(p1.isSuccess)
        assertTrue(p1.finalAnswerBn.contains("৭৫"))

        // 45 is what % of 300 = 15%
        val p2 = MathProblemSolverEngine.solvePercentage(
            MathProblemSolverEngine.PercentageType.WHAT_PERCENT_OF,
            45.0,
            300.0,
            SolverMode.QUICK
        )
        assertTrue(p2.isSuccess)
        assertTrue(p2.finalAnswerBn.contains("১৫%"))

        // 80 to 100 = 25% increase
        val p3 = MathProblemSolverEngine.solvePercentage(
            MathProblemSolverEngine.PercentageType.INCREASE_DECREASE,
            80.0,
            100.0,
            SolverMode.LEARNING
        )
        assertTrue(p3.isSuccess)
        assertTrue(p3.finalAnswerBn.contains("২৫% বৃদ্ধি"))

        // Successive: 10% and 20% -> 10 + 20 + (10*20)/100 = 32%
        val p4 = MathProblemSolverEngine.solvePercentage(
            MathProblemSolverEngine.PercentageType.SUCCESSIVE,
            10.0,
            20.0,
            SolverMode.BCS
        )
        assertTrue(p4.isSuccess)
        assertTrue(p4.finalAnswerBn.contains("৩২%"))
    }

    // -------------------------------------------------------------
    // 4. RATIO & PROPORTION
    // -------------------------------------------------------------
    @Test
    fun testRatioDivisionAndProportion() {
        // 1200 in 3:5 -> 1200 * 3/8 = 450, 1200 * 5/8 = 750
        val r1 = MathProblemSolverEngine.solveRatio(
            MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY,
            1200.0,
            3.0,
            5.0,
            null,
            SolverMode.LEARNING
        )
        assertTrue(r1.isSuccess)
        assertTrue(r1.finalAnswerBn.contains("৪৫০") && r1.finalAnswerBn.contains("৭৫০"))

        // 4 : 8 = 12 : X -> X = (8 * 12) / 4 = 24
        val r2 = MathProblemSolverEngine.solveRatio(
            MathProblemSolverEngine.RatioType.FOURTH_PROPORTIONAL,
            12.0,
            4.0,
            8.0,
            null,
            SolverMode.QUICK
        )
        assertTrue(r2.isSuccess)
        assertTrue(r2.finalAnswerBn.contains("২৪"))

        // 24 : 36 -> 2 : 3
        val r3 = MathProblemSolverEngine.solveRatio(
            MathProblemSolverEngine.RatioType.SIMPLIFY,
            0.0,
            24.0,
            36.0,
            null,
            SolverMode.LEARNING
        )
        assertTrue(r3.isSuccess)
        assertTrue(r3.finalAnswerBn.contains("২ : ৩"))
    }

    // -------------------------------------------------------------
    // 5. PROFIT AND LOSS
    // -------------------------------------------------------------
    @Test
    fun testProfitAndLossCalculations() {
        // CP 500, SP 550 -> 50 profit (10%)
        val pl1 = MathProblemSolverEngine.solveProfitLoss(
            MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT,
            500.0,
            550.0,
            null,
            null,
            SolverMode.LEARNING
        )
        assertTrue(pl1.isSuccess)
        assertTrue(pl1.finalAnswerBn.contains("৫০") && pl1.finalAnswerBn.contains("১০%"))

        // CP 500, SP 400 -> 100 loss (20%)
        val pl2 = MathProblemSolverEngine.solveProfitLoss(
            MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT,
            500.0,
            400.0,
            null,
            null,
            SolverMode.QUICK
        )
        assertTrue(pl2.isSuccess)
        assertTrue(pl2.finalAnswerBn.contains("১০০") && pl2.finalAnswerBn.contains("২০%"))

        // Find SP: CP 800, 15% profit -> 800 * 1.15 = 920
        val pl3 = MathProblemSolverEngine.solveProfitLoss(
            MathProblemSolverEngine.ProfitLossType.FIND_SP_FROM_CP_AND_PERCENT,
            800.0,
            null,
            15.0,
            null,
            SolverMode.BCS
        )
        assertTrue(pl3.isSuccess)
        assertTrue(pl3.finalAnswerBn.contains("৯২০"))

        // Find CP: SP 550, 10% profit -> CP = 550 * 100 / 110 = 500
        val pl4 = MathProblemSolverEngine.solveProfitLoss(
            MathProblemSolverEngine.ProfitLossType.FIND_CP_FROM_SP_AND_PERCENT,
            null,
            550.0,
            10.0,
            null,
            SolverMode.LEARNING
        )
        assertTrue(pl4.isSuccess)
        assertTrue(pl4.finalAnswerBn.contains("৫০০"))

        // Marked price 1000, 15% discount -> SP = 850
        val pl5 = MathProblemSolverEngine.solveProfitLoss(
            MathProblemSolverEngine.ProfitLossType.DISCOUNT_MARKED_PRICE,
            null,
            null,
            15.0,
            1000.0,
            SolverMode.LEARNING
        )
        assertTrue(pl5.isSuccess)
        assertTrue(pl5.finalAnswerBn.contains("৮৫০"))
    }

    // -------------------------------------------------------------
    // 6. AVERAGE
    // -------------------------------------------------------------
    @Test
    fun testAverageCalculation() {
        val avg = MathProblemSolverEngine.solveAverage(listOf(10.0, 20.0, 30.0, 40.0, 50.0), SolverMode.LEARNING)
        assertTrue(avg.isSuccess)
        // Sum = 150, Count = 5, Avg = 30
        assertTrue(avg.finalAnswerBn.contains("৩০"))
        assertNotNull(avg.verificationBn)

        // Empty list
        val emptyAvg = MathProblemSolverEngine.solveAverage(emptyList(), SolverMode.QUICK)
        assertFalse(emptyAvg.isSuccess)
    }

    // -------------------------------------------------------------
    // 7. SIMPLE INTEREST
    // -------------------------------------------------------------
    @Test
    fun testSimpleInterestAllCases() {
        // P 5000, r 10%, t 3 -> I = 1500, A = 6500
        val si1 = MathProblemSolverEngine.solveSimpleInterest(5000.0, 10.0, 3.0, null, null, SolverMode.LEARNING)
        assertTrue(si1.isSuccess)
        assertTrue(si1.finalAnswerBn.contains("১৫০০") && si1.finalAnswerBn.contains("৬৫০০"))

        // Solve for P: I 1500, r 10, t 3 -> P = 5000
        val si2 = MathProblemSolverEngine.solveSimpleInterest(null, 10.0, 3.0, 1500.0, null, SolverMode.QUICK)
        assertTrue(si2.isSuccess)
        assertTrue(si2.finalAnswerBn.contains("৫০০০"))

        // Solve for r: P 5000, I 1500, t 3 -> r = 10%
        val si3 = MathProblemSolverEngine.solveSimpleInterest(5000.0, null, 3.0, 1500.0, null, SolverMode.LEARNING)
        assertTrue(si3.isSuccess)
        assertTrue(si3.finalAnswerBn.contains("১০%"))

        // Solve for t: P 5000, I 1500, r 10 -> t = 3
        val si4 = MathProblemSolverEngine.solveSimpleInterest(5000.0, 10.0, null, 1500.0, null, SolverMode.BCS)
        assertTrue(si4.isSuccess)
        assertTrue(si4.finalAnswerBn.contains("৩"))
    }

    // -------------------------------------------------------------
    // 8. TIME AND WORK
    // -------------------------------------------------------------
    @Test
    fun testTimeAndWork() {
        // A in 10, B in 15 -> (10 * 15) / (10 + 15) = 150 / 25 = 6 days
        val w1 = MathProblemSolverEngine.solveTimeAndWork(
            MathProblemSolverEngine.WorkType.TWO_PERSONS,
            10.0,
            15.0,
            null,
            SolverMode.LEARNING
        )
        assertTrue(w1.isSuccess)
        assertTrue(w1.finalAnswerBn.contains("৬"))
        assertNotNull(w1.verificationBn)

        // Three persons: A 10, B 12, C 15 -> 1 / (1/10 + 1/12 + 1/15) = 1 / (6+5+4 / 60) = 60/15 = 4 days
        val w2 = MathProblemSolverEngine.solveTimeAndWork(
            MathProblemSolverEngine.WorkType.THREE_PERSONS,
            10.0,
            12.0,
            15.0,
            SolverMode.QUICK
        )
        assertTrue(w2.isSuccess)
        assertTrue(w2.finalAnswerBn.contains("৪"))

        // Pipe Cistern: Fill in 10, Empty in 15 -> (10 * 15) / (15 - 10) = 150 / 5 = 30 hrs
        val w3 = MathProblemSolverEngine.solveTimeAndWork(
            MathProblemSolverEngine.WorkType.PIPE_CISTERN,
            10.0,
            15.0,
            null,
            SolverMode.LEARNING
        )
        assertTrue(w3.isSuccess)
        assertTrue(w3.finalAnswerBn.contains("৩০"))

        // Pipe Cistern invalid: Empty in 10, Fill in 15 -> cistern will never fill
        val w4 = MathProblemSolverEngine.solveTimeAndWork(
            MathProblemSolverEngine.WorkType.PIPE_CISTERN,
            15.0,
            10.0,
            null,
            SolverMode.LEARNING
        )
        assertFalse(w4.isSuccess)
    }

    // -------------------------------------------------------------
    // 9. SPEED, DISTANCE, AND TIME
    // -------------------------------------------------------------
    @Test
    fun testSpeedDistanceTime() {
        // Speed: D 120 km, T 2 hrs -> S = 60 km/h = 16.67 m/s
        val s1 = MathProblemSolverEngine.solveSpeedDistanceTime(null, 120.0, 2.0, SolverMode.LEARNING)
        assertTrue(s1.isSuccess)
        assertTrue(s1.finalAnswerBn.contains("৬০ km/h"))

        // Distance: S 60, T 2.5 -> D = 150 km
        val s2 = MathProblemSolverEngine.solveSpeedDistanceTime(60.0, null, 2.5, SolverMode.QUICK)
        assertTrue(s2.isSuccess)
        assertTrue(s2.finalAnswerBn.contains("১৫০ km"))

        // Time: D 150, S 50 -> T = 3 hrs
        val s3 = MathProblemSolverEngine.solveSpeedDistanceTime(50.0, 150.0, null, SolverMode.BCS)
        assertTrue(s3.isSuccess)
        assertTrue(s3.finalAnswerBn.contains("৩ ঘন্টা"))
    }

    // -------------------------------------------------------------
    // 10. NUMBER SYSTEM & DIVISIBILITY
    // -------------------------------------------------------------
    @Test
    fun testDivisibilityRules() {
        // Divisible by 3: 7521
        val d3 = MathProblemSolverEngine.solveDivisibility(7521L, 3, SolverMode.LEARNING)
        assertTrue(d3.isSuccess)
        assertTrue(d3.finalAnswerBn.contains("নিঃশেষে বিভাজ্য"))

        // Not divisible by 3: 7522
        val d3not = MathProblemSolverEngine.solveDivisibility(7522L, 3, SolverMode.LEARNING)
        assertTrue(d3not.isSuccess)
        assertTrue(d3not.finalAnswerBn.contains("বিভাজ্য নয়"))

        // Divisible by 11: 91828
        val d11 = MathProblemSolverEngine.solveDivisibility(91828L, 11, SolverMode.BCS)
        assertTrue(d11.isSuccess)
        assertTrue(d11.finalAnswerBn.contains("নিঃশেষে বিভাজ্য"))
    }

    // -------------------------------------------------------------
    // 11. HCF AND LCM
    // -------------------------------------------------------------
    @Test
    fun testHcfLcm() {
        // 24 and 36 -> HCF 12, LCM 72
        val hl = MathProblemSolverEngine.solveHcfLcm(listOf(24L, 36L), SolverMode.LEARNING)
        assertTrue(hl.isSuccess)
        assertTrue(hl.finalAnswerBn.contains("১২") && hl.finalAnswerBn.contains("৭২"))
        // Check verification invariant
        assertNotNull(hl.verificationBn)
    }

    // -------------------------------------------------------------
    // 12. SQUARES, CUBES, AND ROOTS
    // -------------------------------------------------------------
    @Test
    fun testSquareCubeRoots() {
        // Square of 12 -> 144
        val sq = MathProblemSolverEngine.solveSquareCube(12.0, MathProblemSolverEngine.SquareCubeOp.SQUARE, SolverMode.LEARNING)
        assertTrue(sq.isSuccess)
        assertTrue(sq.finalAnswerBn.contains("১৪৪"))

        // Sqrt of 144 -> 12
        val sqrt = MathProblemSolverEngine.solveSquareCube(144.0, MathProblemSolverEngine.SquareCubeOp.SQUARE_ROOT, SolverMode.LEARNING)
        assertTrue(sqrt.isSuccess)
        assertTrue(sqrt.finalAnswerBn.contains("১২"))

        // Negative sqrt error
        val negSqrt = MathProblemSolverEngine.solveSquareCube(-25.0, MathProblemSolverEngine.SquareCubeOp.SQUARE_ROOT, SolverMode.LEARNING)
        assertFalse(negSqrt.isSuccess)

        // Cube of 5 -> 125
        val cb = MathProblemSolverEngine.solveSquareCube(5.0, MathProblemSolverEngine.SquareCubeOp.CUBE, SolverMode.LEARNING)
        assertTrue(cb.isSuccess)
        assertTrue(cb.finalAnswerBn.contains("১২৫"))

        // Cube root of 125 -> 5
        val cbrt = MathProblemSolverEngine.solveSquareCube(125.0, MathProblemSolverEngine.SquareCubeOp.CUBE_ROOT, SolverMode.LEARNING)
        assertTrue(cbrt.isSuccess)
        assertTrue(cbrt.finalAnswerBn.contains("৫"))
    }

    // -------------------------------------------------------------
    // 13. LINEAR EQUATIONS
    // -------------------------------------------------------------
    @Test
    fun testLinearEquations() {
        // 3x + 12 = 45 -> 3x = 33 -> x = 11
        val eq1 = MathProblemSolverEngine.solveLinearEquation("3x + 12 = 45", SolverMode.LEARNING)
        assertTrue(eq1.isSuccess)
        assertTrue(eq1.finalAnswerBn.contains("১১"))
        assertNotNull(eq1.verificationBn)

        // 5x - 8 = 2x + 16 -> 3x = 24 -> x = 8
        val eq2 = MathProblemSolverEngine.solveLinearEquation("5x - 8 = 2x + 16", SolverMode.BCS)
        assertTrue(eq2.isSuccess)
        assertTrue(eq2.finalAnswerBn.contains("৮"))
        assertNotNull(eq2.bcsShortcutBn)

        // Invalid equation without '='
        val noEq = MathProblemSolverEngine.solveLinearEquation("3x + 12", SolverMode.QUICK)
        assertFalse(noEq.isSuccess)
    }

    // -------------------------------------------------------------
    // LEARNING MODES & SIMILAR PROBLEM GENERATOR
    // -------------------------------------------------------------
    @Test
    fun testLearningModesDifferences() {
        val learning = MathProblemSolverEngine.solvePercentage(
            MathProblemSolverEngine.PercentageType.OF_NUMBER,
            20.0,
            100.0,
            SolverMode.LEARNING
        )
        assertNotNull(learning.verificationBn)

        val bcs = MathProblemSolverEngine.solvePercentage(
            MathProblemSolverEngine.PercentageType.OF_NUMBER,
            20.0,
            100.0,
            SolverMode.BCS
        )
        assertNotNull(bcs.bcsShortcutBn)
    }

    @Test
    fun testSimilarProblemGeneratorAllTopics() {
        for (topic in SolverTopic.entries) {
            val inputs = MathProblemSolverEngine.generateSimilarInputs(topic)
            assertTrue("Topic $topic should have inputs", inputs.isNotEmpty())
        }
    }
}
