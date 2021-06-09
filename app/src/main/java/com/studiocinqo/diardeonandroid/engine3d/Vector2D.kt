package com.studiocinqo.diardeonandroid.engine3d

class Vector2D(val X: Double, val Y: Double) {
    class Line(val A: Vector2D, val B: Vector2D)
    class Circle(val A: Vector2D, val radius: Double)
    class Rect(val x1: Float, val y1: Float, val x2: Float, val y2: Float)

    fun add(other: Vector2D): Vector2D {
        return Vector2D(X + other.X, Y + other.Y)
    }

    fun add(x: Double, y: Double): Vector2D {
        return Vector2D(X + x, Y + y)
    }

    fun subs(other: Vector2D): Vector2D {
        return Vector2D(X - other.X, Y - other.Y)
    }

    fun scale(factor: Double): Vector2D {
        return Vector2D(factor * X, factor * Y)
    }

    fun dot(other: Vector2D): Double {
        return X * other.X + Y * other.Y
    }

    fun norm(): Double {
        return Math.sqrt(X * X + Y * Y)
    }

    fun normalise(): Vector2D {
        val norm: Double = Math.sqrt(X * X + Y * Y)
        return Vector2D(X / norm, Y / norm)
    }

    fun distance(other: Vector2D): Double {
        return Math.sqrt(
            (X - other.X) * (X - other.X) + (Y - other.Y) * (Y - other.Y)
        )
    }

    fun rotate(rad: Double): Vector2D {
        return rotate(Math.sin(rad), Math.cos(rad))
    }

    fun rotate(sin: Double, cos: Double): Vector2D {
        return Vector2D(cos * X + sin * Y, cos * Y - sin * X)
    }

    companion object {
        val zeroVector = Vector2D(0.0, 0.0)
    }
}