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

import kotlin.math.abs
import kotlin.math.sqrt

internal class Face {
    var area = 0.0
    var mark = VISIBLE
    var firstEdge: HalfEdge? = null
    var outside: Vertex? = null
    val centroid = Vector3()

    private var count = 0
    private var planeOffset = 0.0
    private val normal = Vector3()

    val vertexCount get() = count

    private fun computeCentroid(centroid: Vector3) {
        centroid.set(0.0, 0.0, 0.0)
        var he = firstEdge
        do {
            he?.vertex?.point?.apply { centroid.add(x, y, z) }
            he = he?.next
        } while (he != firstEdge)
        centroid.scale(1.0 / count)
    }

    private fun computeNormal(normal: Vector3) {
        val he1 = firstEdge!!.next
        var he2 = he1!!.next
        val p0 = firstEdge!!.vertex.point
        var p2 = he1.vertex.point
        var d2x = p2.x - p0.x
        var d2y = p2.y - p0.y
        var d2z = p2.z - p0.z
        var x = 0.0
        var y = 0.0
        var z = 0.0
        count = 2
        while (he2 != firstEdge) {
            val d1x = d2x
            val d1y = d2y
            val d1z = d2z
            p2 = he2!!.vertex.point
            d2x = p2.x - p0.x
            d2y = p2.y - p0.y
            d2z = p2.z - p0.z
            x += d1y * d2z - d1z * d2y
            y += d1z * d2x - d1x * d2z
            z += d1x * d2y - d1y * d2x
            he2 = he2.next
            count++
        }
        normal.set(x, y, z)
        area = normal.length()
        normal.scale(1.0 / area)
    }

    private fun computeNormal(normal: Vector3, minArea: Double) {
        computeNormal(normal)
        if (area < minArea) {
            // make the normal more robust by removing components parallel to the longest edge
            var hedgeMax: HalfEdge? = null
            var lenSqrMax = 0.0
            var hedge = firstEdge
            do {
                val lenSqr = hedge!!.lengthSquared
                if (lenSqr > lenSqrMax) {
                    hedgeMax = hedge
                    lenSqrMax = lenSqr
                }
                hedge = hedge.next
            } while (hedge != firstEdge)
            val p2 = hedgeMax!!.vertex.point
            val p1 = hedgeMax.tail!!.point
            val lenMax = sqrt(lenSqrMax)
            val ux = (p2.x - p1.x) / lenMax
            val uy = (p2.y - p1.y) / lenMax
            val uz = (p2.z - p1.z) / lenMax
            val dot = normal.x * ux + normal.y * uy + normal.z * uz
            normal.x -= dot * ux
            normal.y -= dot * uy
            normal.z -= dot * uz
            normal.normalize()
        }
    }

    fun distanceToPlane(p: Vector3) = normal.x * p.x + normal.y * p.y + normal.z * p.z - planeOffset

    fun getEdge(io: Int): HalfEdge? {
        var i = io
        var he = firstEdge
        while (i > 0) {
            he = he?.next
            i--
        }
        while (i < 0) {
            he = he?.prev
            i++
        }
        return he
    }

    val vertexString: String
        get() {
            val s = StringBuilder("")
            var he = firstEdge
            do {
                he?.vertex?.index.apply { s.append(this).append(" ") }
                he = he?.next
            } while (he != firstEdge)
            return s.trim().toString()
        }

    fun mergeAdjacentFace(hedgeAdj: HalfEdge, discarded: Array<Face?>): Int {
        val oppFace = hedgeAdj.oppositeFace!!
        var numDiscarded = 0
        discarded[numDiscarded++] = oppFace
        oppFace.mark = DELETED
        val hedgeOpp = hedgeAdj.opposite
        var hedgeAdjPrev = hedgeAdj.prev
        var hedgeAdjNext = hedgeAdj.next
        var hedgeOppPrev = hedgeOpp!!.prev
        var hedgeOppNext = hedgeOpp.next
        
        while (hedgeAdjPrev?.oppositeFace == oppFace) {
            hedgeAdjPrev = hedgeAdjPrev.prev
            hedgeOppNext = hedgeOppNext!!.next
        }
        while (hedgeAdjNext?.oppositeFace == oppFace) {
            hedgeOppPrev = hedgeOppPrev!!.prev
            hedgeAdjNext = hedgeAdjNext.next
        }
        var hedge = hedgeOppNext
        while (hedge != hedgeOppPrev?.next) {
            hedge?.face = this
            hedge = hedge?.next
        }

        if (hedgeAdj == firstEdge) firstEdge = hedgeAdjNext

        // handle the half edges at the head
        var discardedFace = connectHalfEdges(hedgeOppPrev, hedgeAdjNext)
        if (discardedFace != null) {
            discarded[numDiscarded++] = discardedFace
        }

        // handle the half edges at the tail
        discardedFace = connectHalfEdges(hedgeAdjPrev, hedgeOppNext)
        if (discardedFace != null) {
            discarded[numDiscarded++] = discardedFace
        }

        computeNormalAndCentroid()
        checkConsistency()

        return numDiscarded
    }

    private fun computeNormalAndCentroid() {
        computeNormal(normal)
        computeCentroid(centroid)
        planeOffset = normal.dot(centroid)
        var numv = 0
        var he = firstEdge
        do {
            numv++
            he = he!!.next
        } while (he != firstEdge)
        if (numv != count) {
            throw InternalErrorException("face $vertexString vertex count is $count should be $numv")
        }
    }

    private fun computeNormalAndCentroid(minArea: Double) {
        computeNormal(normal, minArea)
        computeCentroid(centroid)
        planeOffset = normal.dot(centroid)
    }

    private fun connectHalfEdges(hedgePrev: HalfEdge?, hedge: HalfEdge?): Face? {
        var discardedFace: Face? = null
        if (hedgePrev!!.oppositeFace == hedge!!.oppositeFace) {
            val oppFace = hedge.oppositeFace
            val hedgeOpp: HalfEdge?
            if (hedgePrev == firstEdge) {
                firstEdge = hedge
            }
            if (oppFace?.vertexCount == 3) {
                hedgeOpp = hedge.opposite!!.prev!!.opposite
                oppFace.mark = DELETED
                discardedFace = oppFace
            } else {
                hedgeOpp = hedge.opposite!!.next
                if (oppFace?.firstEdge == hedgeOpp!!.prev) {
                    oppFace?.firstEdge = hedgeOpp
                }
                hedgeOpp.prev = hedgeOpp.prev!!.prev
                hedgeOpp.prev!!.next = hedgeOpp
            }
            hedge.prev = hedgePrev.prev
            hedge.prev!!.next = hedge
            hedge.opposite = hedgeOpp
            hedgeOpp!!.opposite = hedge
            oppFace?.computeNormalAndCentroid()
        } else {
            hedgePrev.next = hedge
            hedge.prev = hedgePrev
        }
        return discardedFace
    }

    fun checkConsistency() {
        if (count < 3) throw InternalErrorException("degenerate face: $vertexString")

        var numv = 0
        var maxd = 0.0
        var hedge = firstEdge

        do {
            val hedgeOpp = hedge!!.opposite
            when {
                hedgeOpp == null -> throw InternalErrorException("face $vertexString: unreflected half edge $hedge.vertexString")
                hedgeOpp.opposite != hedge -> throw InternalErrorException("face $vertexString: opposite half edge $hedgeOpp.vertexString has opposite $hedgeOpp.opposite!!.vertexString")
                hedgeOpp.vertex != hedge.tail || hedge.vertex != hedgeOpp.tail -> throw InternalErrorException("face $vertexString: half edge $hedge.vertexString reflected by $hedgeOpp.vertexString")
                else -> {
                    val oppFace = hedgeOpp.face
                    if (oppFace.mark == DELETED) throw InternalErrorException("face $vertexString: opposite face $oppFace.vertexString not on hull")
                    val d = abs(distanceToPlane(hedge.vertex.point))
                    if (d > maxd) maxd = d
                    numv++
                    hedge = hedge.next
                }
            }
        } while (hedge != firstEdge)

        if (numv != count) throw InternalErrorException("face $vertexString vertex count is $count should be $numv")
    }

    companion object {
        const val DELETED = 3
        const val NON_CONVEX = 2
        const val VISIBLE = 1

        fun createTriangle(v0: Vertex, v1: Vertex, v2: Vertex, minArea: Double = 0.0): Face {
            val face = Face()
            val he0 = HalfEdge(v0, face)
            val he1 = HalfEdge(v1, face)
            val he2 = HalfEdge(v2, face)
            he0.prev = he2
            he0.next = he1
            he1.prev = he0
            he1.next = he2
            he2.prev = he1
            he2.next = he0
            face.firstEdge = he0
            face.computeNormalAndCentroid(minArea)
            return face
        }
    }
}
