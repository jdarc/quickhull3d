# QuickHull3D - A Robust 3D Convex Hull Algorithm in Kotlin

This is a Kotlin port of the Java original by John E. Lloyd 

The original codebase is available here: https://github.com/Quickhull3d/com.zynaps.quickhull3d

The algorithm is based on the original paper by Barber, Dobkin, and Huhdanpaa 
and derived from the C implementation known as qhull. 

The algorithm has O(n log(n)) complexity, works with double precision numbers, 
is fairly robust with respect to degenerate situations, and allows the merging of 
co-planar faces.

This Kotlin port is NOT an exact copy of the original, the API differs from the 
original as I prefer to use constructors for configuration purposes, such as
dependency injection, and delegate the work to methods.

## Building/Running

To run tests, execute:
```
./gradlew clean test
```

## LICENSE
Copyright (c) 2021, Jean d'Arc - Kotlin port

Copyright (c) 2004 - 2014, John E. Lloyd - Java Original

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
