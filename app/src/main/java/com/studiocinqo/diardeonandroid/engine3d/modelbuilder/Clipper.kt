package com.studiocinqo.diardeonandroid.engine3d.modelbuilder

class Clipper(private val clipper: List<DoubleArray>) {


    fun clipPolygon(subject: List<DoubleArray>): List<DoubleArray> {
        var result = subject.toMutableList()
        val len = clipper.size
        for (i in 0 until len) {
            val len2 = result.size
            val input = result
            result = mutableListOf<DoubleArray>()
            val a = clipper[(i + len - 1) % len]
            val b = clipper[i]

            for (j in 0 until len2) {
                val p = input[(j + len2 - 1) % len2]
                val q = input[j]

                if (isInside(a, b, q)) {
                    if (!isInside(a, b, p)) result.add(intersection(a, b, p, q))
                    result.add(q)
                }
                else if (isInside(a, b, p)) result.add(intersection(a, b, p, q))
            }
        }
        return result
    }

    private fun isInside(a: DoubleArray, b: DoubleArray, c: DoubleArray) =
        (a[0] - c[0]) * (b[1] - c[1]) > (a[1] - c[1]) * (b[0] - c[0])

    private fun intersection(a: DoubleArray, b: DoubleArray,
                             p: DoubleArray, q: DoubleArray): DoubleArray {
        val a1 = b[1] - a[1]
        val b1 = a[0] - b[0]
        val c1 = a1 * a[0] + b1 * a[1]

        val a2 = q[1] - p[1]
        val b2 = p[0] - q[0]
        val c2 = a2 * p[0] + b2 * p[1]

        val d = a1 * b2 - a2 * b1
        val x = (b2 * c1 - b1 * c2) / d
        val y = (a1 * c2 - a2 * c1) / d

        return doubleArrayOf(x, y)
    }

}
