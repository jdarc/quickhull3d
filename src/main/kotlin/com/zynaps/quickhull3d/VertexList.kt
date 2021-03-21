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

internal class VertexList {
    private var head: Vertex? = null
    private var tail: Vertex? = null

    val first get() = head

    val isEmpty get() = head == null

    fun clear() {
        tail = null
        head = null
    }

    fun add(v: Vertex) {
        when (head) {
            null -> head = v
            else -> tail?.next = v
        }
        v.prev = tail
        v.next = null
        tail = v
    }

    fun addAll(v: Vertex) {
        var vtx: Vertex? = v
        when (head) {
            null -> head = vtx
            else -> tail?.next = vtx
        }
        vtx?.prev = tail
        while (vtx?.next != null) vtx = vtx.next
        tail = vtx
    }

    fun insertBefore(vtx: Vertex, next: Vertex) {
        vtx.prev = next.prev
        when (next.prev) {
            null -> head = vtx
            else -> next.prev?.next = vtx
        }
        vtx.next = next
        next.prev = vtx
    }

    fun delete(vtx: Vertex) {
        when (vtx.prev) {
            null -> head = vtx.next
            else -> vtx.prev?.next = vtx.next
        }
        when (vtx.next) {
            null -> tail = vtx.prev
            else -> vtx.next?.prev = vtx.prev
        }
    }

    fun delete(vtx1: Vertex, vtx2: Vertex) {
        when (vtx1.prev) {
            null -> head = vtx2.next
            else -> vtx1.prev?.next = vtx2.next
        }
        when (vtx2.next) {
            null -> tail = vtx1.prev
            else -> vtx2.next?.prev = vtx1.prev
        }
    }
}
