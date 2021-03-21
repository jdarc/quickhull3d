/*
 * A Robust 3D Convex Hull Algorithm in Kotlin
 *
 * Copyright (C) 2021 Jean d'Arc - Kotlin port
 * Copyright (C) 2004 - 2014 John E. Lloyd - Java original
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.zynaps.quickhull3d

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class QuickHull3DTest {

    @Test
    fun `should throw when points are coincident`() {
        Assertions.assertDoesNotThrow {
            for (i in 0..9) testException(
                TestHelper.randomDegeneratePoints(10, 0),
                "Input points appear to be coincident"
            )
        }
    }

    @Test
    fun `should throw when points are colinear`() {
        Assertions.assertDoesNotThrow {
            for (i in 0..9) testException(
                TestHelper.randomDegeneratePoints(10, 1),
                "Input points appear to be colinear"
            )
        }
    }

    @Test
    fun `should throw when points are coplanar`() {
        Assertions.assertDoesNotThrow {
            for (i in 0..9) testException(
                TestHelper.randomDegeneratePoints(10, 2),
                "Input points appear to be coplanar"
            )
        }
    }

    @Test
    fun `explicit test 1`() {
        Assertions.assertDoesNotThrow {
            test(doubleArrayOf(
                21.0, 0.0, 0.0,
                0.0, 21.0, 0.0,
                0.0, 0.0, 0.0,
                18.0, 2.0, 6.0,
                1.0, 18.0, 5.0,
                2.0, 1.0, 3.0,
                14.0, 3.0, 10.0,
                4.0, 14.0, 14.0,
                3.0, 4.0, 10.0,
                10.0, 6.0, 12.0,
                5.0, 10.0, 15.0)
            )
        }
    }

    @Test
    fun `explicit test 2`() {
        Assertions.assertDoesNotThrow {
            test(doubleArrayOf(
                0.0, 0.0, 0.0,
                21.0, 0.0, 0.0,
                0.0, 21.0, 0.0,
                2.0, 1.0, 2.0,
                17.0, 2.0, 3.0,
                1.0, 19.0, 6.0,
                4.0, 3.0, 5.0,
                13.0, 4.0, 5.0,
                3.0, 15.0, 8.0,
                6.0, 5.0, 6.0,
                9.0, 6.0, 11.0)
            )
        }
    }

    @Test
    fun `should succeed with 20 to 200 random points `() {
        Assertions.assertDoesNotThrow {
            var n = 20
            while (n < 200) {
                for (i in 0..9) test(TestHelper.randomPoints(n, 1.0))
                n += 10
            }
        }
    }

    @Test
    fun `should succeed with 20 to 200 random points in a sphere`() {
        Assertions.assertDoesNotThrow {
            var n = 20
            while (n < 200) {
                for (i in 0..9) test(TestHelper.randomSphericalPoints(n, 1.0))
                n += 10
            }
        }
    }

    @Test
    fun `should succeed with 20 to 200 random points clipped to a cube`() {
        Assertions.assertDoesNotThrow {
            var n = 20
            while (n < 200) {
                for (i in 0..9) test(TestHelper.randomCubedPoints(n, 1.0, 0.5))
                n += 10
            }
        }
    }

    @Test
    fun `should succeed with 8 to 1000 randomly shuffled points on a grid`() {
        Assertions.assertDoesNotThrow {
            for (n in 2..10) for (i in 0..9) test(TestHelper.randomGridPoints(n, 4.0))
        }
    }

    @Test
    fun `should generate convex hull`() {
        val points = arrayOf(
            Point3D(0.3215426810286406, 0.1678336189760208, -0.2203710966001927),
            Point3D(0.2229772524190855, -0.4213242506806965, -0.1966818060695024),
            Point3D(0.3688830163971363, -0.1831502133823468, -0.2056387967482571),
            Point3D(-0.1712592515826777, -0.3542439228428937, 0.2223876390814666),
            Point3D(-0.3309556113844324, -0.370961861099081, 0.2439994981922204),
            Point3D(-0.1004397059794885, -0.09014152417903909, -0.008600084584765189),
            Point3D(0.458374538420117, -0.09914027349943322, -0.2505798421339875),
            Point3D(-0.4954086979808367, -0.3339869997780649, -0.3195065691317492),
            Point3D(0.053091190339151, 0.3036317017894533, 0.1380056861210668),
            Point3D(0.4615616439483703, 0.4665423151725366, 0.1766835406205464),
            Point3D(-0.4797380864431505, 0.0419809916447671, -0.4254776681079321),
            Point3D(-0.003168473023146823, -0.2525299883005488, -0.27151530400991),
            Point3D(-0.3577162826971303, -0.1375644040643837, -0.04494194644032229),
            Point3D(-0.3392973838740004, 0.4288679723896719, -0.01599531622230571),
            Point3D(0.1667164640191164, 0.003605551555385444, -0.4014989499947977),
            Point3D(0.00714666676441833, 0.1140243407469469, 0.407090128778564),
            Point3D(-0.03621271768232132, 0.3728502838619522, 0.4947140370446388),
            Point3D(-0.3411871756810576, -0.3328629143842151, -0.4270033635450559),
            Point3D(0.3544683273457627, -0.450828987127942, -0.0827870439577727),
            Point3D(-0.4018510635028137, 0.08917494033386464, -0.2367824197158054),
            Point3D(0.3978697768392692, -0.002667689232777493, 0.1641431727112673),
            Point3D(-0.245701439441835, 0.495905311308713, -0.3194406286994373),
            Point3D(0.161352035739787, -0.1563404972258401, 0.3852604361113724),
            Point3D(0.07214279572678994, -0.4960366976410492, 0.1112227161519441),
            Point3D(0.3201855824516951, 0.359077846965825, 0.02136723140381946),
            Point3D(0.1190541238701475, -0.05734495917087884, 0.2032677509852384),
            Point3D(0.3210853052521919, 0.4807189479290684, 0.4433501688235907),
            Point3D(0.3862800354941562, 0.2085496142586224, 0.09336129957191763),
            Point3D(0.1233572616459404, 0.265491605052251, 0.117400122450106),
            Point3D(0.1438531872293476, -0.2594872752758556, -0.2026374435076839),
            Point3D(0.2724846394476338, -0.3506708492996831, 0.2750346518820475),
            Point3D(-0.4926118841325975, -0.3279366743079728, 0.3683135596740186),
            Point3D(0.2459906458351674, 0.3647787136629026, -0.1641662355178652),
            Point3D(-0.141922976953837, -0.2994764654892278, -0.3009570467294725),
            Point3D(-0.1850859398814719, 0.2606059478228967, 0.004159106876849283),
            Point3D(-0.09789466634196664, -0.3156603563722785, -0.303610991503681),
            Point3D(0.2100642609503719, -0.4499717643018549, 0.3245569875692548),
            Point3D(-0.1707163766685095, -0.2301452446078371, -0.05112823569320907),
            Point3D(-0.312260808713977, -0.1674135249735914, 0.2808831662692904),
            Point3D(-0.1966306233747216, 0.2291105671125563, -0.3387042454804333)
        )

        val expected = arrayOf(
            Point3D(0.3215426810286406, 0.1678336189760208, -0.2203710966001927),
            Point3D(0.2229772524190855, -0.4213242506806965, -0.1966818060695024),
            Point3D(0.458374538420117, -0.09914027349943322, -0.2505798421339875),
            Point3D(-0.4954086979808367, -0.3339869997780649, -0.3195065691317492),
            Point3D(0.4615616439483703, 0.4665423151725366, 0.1766835406205464),
            Point3D(-0.4797380864431505, 0.0419809916447671, -0.4254776681079321),
            Point3D(-0.3392973838740004, 0.4288679723896719, -0.01599531622230571),
            Point3D(0.1667164640191164, 0.003605551555385444, -0.4014989499947977),
            Point3D(-0.03621271768232132, 0.3728502838619522, 0.4947140370446388),
            Point3D(-0.3411871756810576, -0.3328629143842151, -0.4270033635450559),
            Point3D(0.3544683273457627, -0.450828987127942, -0.0827870439577727),
            Point3D(0.3978697768392692, -0.002667689232777493, 0.1641431727112673),
            Point3D(-0.245701439441835, 0.495905311308713, -0.3194406286994373),
            Point3D(0.161352035739787, -0.1563404972258401, 0.3852604361113724),
            Point3D(0.07214279572678994, -0.4960366976410492, 0.1112227161519441),
            Point3D(0.3210853052521919, 0.4807189479290684, 0.4433501688235907),
            Point3D(0.2724846394476338, -0.3506708492996831, 0.2750346518820475),
            Point3D(-0.4926118841325975, -0.3279366743079728, 0.3683135596740186),
            Point3D(0.2459906458351674, 0.3647787136629026, -0.1641662355178652),
            Point3D(0.2100642609503719, -0.4499717643018549, 0.3245569875692548)
        )

        Assertions.assertDoesNotThrow {
            val result = QuickHull3D().build(points)
            Assertions.assertEquals(expected.size, result.vertices.size)
            result.vertices.forEach { Assertions.assertTrue(expected.contains(it)) }
        }
    }

    private fun testException(coords: DoubleArray, msg: String) {
        var captured: Exception? = null
        try {
            QuickHull3D().build(coords)
        } catch (e: Exception) {
            captured = e
        }
        if (captured == null) {
            println("Expected exception $msg")
            println("Actual is no exception")
            println("Input points:")
            TestHelper.printCoords(coords)
            throw AssertionError()
        } else if (captured.message ?: "" != msg) {
            println("Expected exception $msg")
            println("Actual is exception ${captured.message}")
            println("Input points:")
            TestHelper.printCoords(coords)
            throw AssertionError()
        }
    }

    private fun test(coords: DoubleArray) {
        val list1 = doubleArrayOf(0.0, 0.0, 0.0)
        val list2 = doubleArrayOf(10.0, 20.0, 30.0)
        val list3 = doubleArrayOf(-45.0, 60.0, 91.0)
        val list4 = doubleArrayOf(125.0, 67.0, 81.0)
        val rpyList = arrayOf(list1, list2, list3, list4)
        singleTest(coords)
        val rotated = DoubleArray(coords.size)
        rpyList.forEach {
            TestHelper.rotateCoords(rotated, coords, Math.toRadians(it[0]), Math.toRadians(it[1]), Math.toRadians(it[2]))
            singleTest(rotated)
        }
    }

    private fun singleTest(coords: DoubleArray) {
        val hull = QuickHull3D()
        val result = hull.build(coords)
        if (!result.check({ println(it) })) throw AssertionError()
        val degenerates = TestHelper.addDegeneracy(TestHelper.VERTEX_DEGENERACY, coords, hull)
        try {
            if (!hull.build(degenerates).check({ println(it) })) throw AssertionError()
        } catch (e: Exception) {
            var i = 0
            while (i < degenerates.size / 3) {
                println(degenerates[i * 3 + 0].toString() + ", " + degenerates[i * 3 + 1] + ", " + degenerates[i * 3 + 2] + ", ")
                i++
            }
            throw AssertionError()
        }
    }
}
