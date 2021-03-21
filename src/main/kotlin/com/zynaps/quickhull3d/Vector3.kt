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

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

internal data class Vector3(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {

    constructor(p: Point3D) : this(p.x, p.y, p.z)

    operator fun get(i: Int) = doubleArrayOf(x, y, z)[i]

    fun lengthSquared() = dot(this)

    fun length() = sqrt(lengthSquared())

    fun set(x: Double, y: Double, z: Double): Vector3 {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(p: Point3D) = set(p.x, p.y, p.z)

    fun set(v: Vector3) = set(v.x, v.y, v.z)

    fun add(x: Double, y: Double, z: Double) = set(this.x + x, this.y + y, this.z + z)

    fun add(v: Vector3) = add(v.x, v.y, v.z)

    fun sub(v1: Vector3, v2: Vector3) = set(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z)

    fun sub(v: Vector3) = set(x - v.x, y - v.y, z - v.z)

    fun scale(s: Double) = set(x * s, y * s, z * s)

    fun scale(s: Double, v: Vector3) = set(s * v.x, s * v.y, s * v.z)

    fun distanceSquared(v: Vector3) = (x - v.x).pow(2) + (y - v.y).pow(2) + (z - v.z).pow(2)

    fun normalize() = scale(1 / sqrt(lengthSquared()))

    fun dot(v: Vector3): Double = x * v.x + y * v.y + z * v.z

    fun cross(v1: Vector3, v2: Vector3) = set(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x)

    fun setRandom(lower: Double, upper: Double, generator: Random) = set(
        generator.nextDouble(lower, upper),
        generator.nextDouble(lower, upper),
        generator.nextDouble(lower, upper)
    )

    override fun toString() = "Vector3{x=$x, y=$y, z=$z}"
}
