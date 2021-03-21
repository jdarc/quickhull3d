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

class BuildResult internal constructor(
    val vertices: Array<Point3D>,
    val polygons: Array<IntArray>,
    val distanceTolerance: Double,
    private val points: Array<Point3D>,
    private val facets: ArrayList<Face?>
) {

    @JvmOverloads
    fun check(cb: (String) -> Unit, tolerance: Double = distanceTolerance): Boolean {
        var convex = true
        facets.forEach { if (it?.mark == Face.VISIBLE && !checkFaceConvexity(it, tolerance, cb)) convex = false }
        if (!convex) return false

        for (i in points.indices) {
            val pnt = points[i]
            for (face in facets) {
                if (face?.mark == Face.VISIBLE) {
                    val dist = face.distanceToPlane(Vector3(pnt))
                    if (dist > tolerance * 10.0) {
                        cb("Point $i $dist above face ${face.vertexString}")
                        return false
                    }
                }
            }
        }
        return true
    }

    private companion object {

        fun checkFaceConvexity(face: Face, tolerance: Double, cb: (String) -> Unit): Boolean {
            var he = face.firstEdge
            do {
                face.checkConsistency()
                // make sure edge is convex
                var dist = oppFaceDistance(he!!)
                if (dist > tolerance) {
                    cb("Edge ${he.vertexString} non-convex by $dist")
                    return false
                }
                dist = oppFaceDistance(he.opposite!!)
                if (dist > tolerance) {
                    cb("Opposite edge ${he.opposite?.vertexString} non-convex by $dist")
                    return false
                }
                if (he.next!!.oppositeFace == he.oppositeFace) {
                    cb("Redundant vertex ${he.vertex.index} in face ${face.vertexString}")
                    return false
                }
                he = he.next
            } while (he != face.firstEdge)
            return true
        }

        fun oppFaceDistance(he: HalfEdge) = he.face.distanceToPlane(he.opposite!!.face.centroid)
    }
}
