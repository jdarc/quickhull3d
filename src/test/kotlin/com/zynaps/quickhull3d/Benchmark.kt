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

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.math.pow

/**
 * Runs benchmarks on QuickHull3D, and prints the results to the standard output stream.
 */
internal class Benchmark {

    @Test
    fun test() {
        val hull = QuickHull3D()
        println("warming up... ")
        for (i in 0..2) hull.build(TestHelper.randomSphericalPoints(10000, 1.0))

        for (i in 0..3) {
            val n = (10.0 * 10.0.pow(i + 1)).toInt()
            val repetitions = 10
            val coords = TestHelper.randomSphericalPoints(n, 1.0)
            val t0 = System.nanoTime()
            for (k in 0 until repetitions) hull.build(coords)
            val duration = TimeUnit.MILLISECONDS.convert((System.nanoTime() - t0) / repetitions, TimeUnit.NANOSECONDS)
            println("$n points in $duration milliseconds")
        }
    }
}
