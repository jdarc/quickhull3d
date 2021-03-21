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

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

internal object TestHelper {
    private const val TOLERANCE = 2.2204460492503131e-16
    private const val EPS_SCALE = 2.0
    private val RANDOM = Random(0x1234)

    const val VERTEX_DEGENERACY = 2

    fun printCoords(coords: DoubleArray) = coords.toList().windowed(3, 3).forEach { (x, y, z) -> println("$x, $y, $z") }

    fun addDegeneracy(type: Int, coords: DoubleArray, hull: QuickHull3D): DoubleArray {
        val result = hull.build(coords)
        var numv = coords.size / 3
        val faces: Array<IntArray> = result.polygons
        val coordsx = DoubleArray(coords.size + faces.size * 3)
        for (i in coords.indices) {
            coordsx[i] = coords[i]
        }
        val lam = DoubleArray(3)
        val eps: Double = result.distanceTolerance
        for (i in faces.indices) {
            // random point on an edge
            lam[0] = RANDOM.nextDouble()
            lam[1] = 1 - lam[0]
            lam[2] = 0.0
            if (type == VERTEX_DEGENERACY && i % 2 == 0) {
                lam[0] = 1.0
                lam[2] = 0.0
                lam[1] = lam[2]
            }
            for (j in 0..2) {
                val vtxi = faces[i][j]
                for (k in 0..2) {
                    coordsx[numv * 3 + k] += lam[j] * coords[vtxi * 3 + k] + EPS_SCALE * eps * (RANDOM.nextDouble() - 0.5)
                }
            }
            numv++
        }
        shuffleCoords(coordsx)
        return coordsx
    }

    /**
     * Returns randomly shuffled coordinates for points on a three-dimensional
     * grid, with a presecribed width between each point.
     */
    fun randomGridPoints(gridSize: Int, width: Double): DoubleArray {
        // gridSize gives the number of points across a given dimension
        // any given coordinate indexed by i has value
        // (i/(gridSize-1) - 0.5)*width
        val num = gridSize * gridSize * gridSize
        val coords = DoubleArray(num * 3)
        var idx = 0
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                for (k in 0 until gridSize) {
                    coords[idx * 3 + 0] = (i / (gridSize - 1).toDouble() - 0.5) * width
                    coords[idx * 3 + 1] = (j / (gridSize - 1).toDouble() - 0.5) * width
                    coords[idx * 3 + 2] = (k / (gridSize - 1).toDouble() - 0.5) * width
                    idx++
                }
            }
        }
        shuffleCoords(coords)
        return coords
    }

    /**
     * Returns the coordinates for `num` points whose x, y, and z
     * values are randomly chosen within a given range.
     */
    fun randomPoints(num: Int, range: Double): DoubleArray {
        val coords = DoubleArray(num * 3)
        for (i in 0 until num) {
            for (k in 0..2) {
                coords[i * 3 + k] = 2.0 * range * (RANDOM.nextDouble() - 0.5)
            }
        }
        return coords
    }

    /**
     * Returns the coordinates for `num` points whose x, y, and z values are randomly chosen to lie within a sphere.
     */
    fun randomSphericalPoints(num: Int, radius: Double): DoubleArray {
        var i = 0
        val pnt = Vector3()
        val coords = DoubleArray(num * 3)
        while (i < num) {
            pnt.setRandom(-radius, radius, RANDOM)
            if (pnt.length() <= radius) {
                coords[i * 3 + 0] = pnt.x
                coords[i * 3 + 1] = pnt.y
                coords[i * 3 + 2] = pnt.z
                i++
            }
        }
        return coords
    }

    /**
     * Returns the coordinates for `num` randomly chosen points which
     * are degenerate which respect to the specified dimensionality.
     */
    fun randomDegeneratePoints(count: Int, dimension: Int): DoubleArray {
        val pnt = Vector3()
        val base = Vector3().setRandom(-1.0, 1.0, RANDOM)
        val coords = DoubleArray(count * 3)
        when (dimension) {
            0 -> for (i in 0 until count) {
                randomlyPerturb(pnt.set(base))
                coords[i * 3 + 0] = pnt.x
                coords[i * 3 + 1] = pnt.y
                coords[i * 3 + 2] = pnt.z
            }
            1 -> {
                val u = Vector3().setRandom(-1.0, 1.0, RANDOM).normalize()
                for (i in 0 until count) {
                    randomlyPerturb(pnt.scale((RANDOM.nextDouble() - 0.5) * 2.0, u).add(base))
                    coords[i * 3 + 0] = pnt.x
                    coords[i * 3 + 1] = pnt.y
                    coords[i * 3 + 2] = pnt.z
                }
            }
            else -> {
                val nrm = Vector3().setRandom(-1.0, 1.0, RANDOM).normalize()
                for (i in 0 until count) {
                    randomlyPerturb(pnt.sub(Vector3().scale(pnt.setRandom(-1.0, 1.0, RANDOM).dot(nrm), nrm)).add(base))
                    coords[i * 3 + 0] = pnt.x
                    coords[i * 3 + 1] = pnt.y
                    coords[i * 3 + 2] = pnt.z
                }
            }
        }
        return coords
    }

    /**
     * Returns the coordinates for `num` points whose x, y, and z
     * values are each randomly chosen to lie within a specified range, and then
     * clipped to a maximum absolute value. This means a large number of points
     * may lie on the surface of cube, which is useful for creating degenerate
     * convex hull situations.
     */
    fun randomCubedPoints(num: Int, range: Double, max: Double): DoubleArray {
        val coords = DoubleArray(num * 3)
        for (i in 0 until num) {
            for (k in 0..2) {
                coords[i * 3 + k] = (2.0 * range * (RANDOM.nextDouble() - 0.5)).coerceIn(-max, max)
            }
        }
        return coords
    }

    fun rotateCoords(res: DoubleArray, xyz: DoubleArray, roll: Double, pitch: Double, yaw: Double) {
        val sroll = sin(roll)
        val croll = cos(roll)
        val spitch = sin(pitch)
        val cpitch = cos(pitch)
        val syaw = sin(yaw)
        val cyaw = cos(yaw)
        val m00 = croll * cpitch
        val m10 = sroll * cpitch
        val m20 = -spitch
        val m01 = croll * spitch * syaw - sroll * cyaw
        val m11 = sroll * spitch * syaw + croll * cyaw
        val m21 = cpitch * syaw
        val m02 = croll * spitch * cyaw + sroll * syaw
        val m12 = sroll * spitch * cyaw - croll * syaw
        val m22 = cpitch * cyaw
        var i = 0
        while (i < xyz.size - 2) {
            res[i + 0] = m00 * xyz[i + 0] + m01 * xyz[i + 1] + m02 * xyz[i + 2]
            res[i + 1] = m10 * xyz[i + 0] + m11 * xyz[i + 1] + m12 * xyz[i + 2]
            res[i + 2] = m20 * xyz[i + 0] + m21 * xyz[i + 1] + m22 * xyz[i + 2]
            i += 3
        }
    }

    private fun randomlyPerturb(point: Vector3) {
        point.x += (RANDOM.nextDouble() - 0.5) * TOLERANCE
        point.y += (RANDOM.nextDouble() - 0.5) * TOLERANCE
        point.z += (RANDOM.nextDouble() - 0.5) * TOLERANCE
    }

    private fun shuffleCoords(coords: DoubleArray): DoubleArray {
        val num = coords.size / 3
        for (i in 0 until num) {
            val i1 = RANDOM.nextInt(num)
            val i2 = RANDOM.nextInt(num)
            for (k in 0..2) {
                val tmp = coords[i1 * 3 + k]
                coords[i1 * 3 + k] = coords[i2 * 3 + k]
                coords[i2 * 3 + k] = tmp
            }
        }
        return coords
    }
}
