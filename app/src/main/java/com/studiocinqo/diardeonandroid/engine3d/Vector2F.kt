package com.studiocinqo.diardeonandroid.engine3d

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector2F(val X: Float, val Y: Float) {


    fun add(other: Vector2F): Vector2F {
        return Vector2F(X + other.X, Y + other.Y)
    }

    fun add(x: Float, y: Float): Vector2F {
        return Vector2F(X + x, Y + y)
    }

    fun subs(other: Vector2F): Vector2F {
        return Vector2F(X - other.X, Y - other.Y)
    }

    fun scale(factor:Float): Vector2F {
        return Vector2F(factor * X, factor * Y)
    }

    fun dot(other: Vector2F): Float {
        return X * other.X + Y * other.Y
    }

    fun norm(): Float {
        return sqrt(X * X + Y * Y)
    }

    fun normalise(): Vector2F {
        val norm: Float = sqrt(X * X + Y * Y)
        return Vector2F(X / norm, Y / norm)
    }

    fun distance(other: Vector2F): Float {
        return sqrt(
            (X - other.X) * (X - other.X) + (Y - other.Y) * (Y - other.Y)
        )
    }

    fun rotate(rad: Float): Vector2F {
        return rotate(sin(rad), cos(rad))
    }

    fun rotate(sin: Float, cos: Float): Vector2F {
        return Vector2F(cos * X + sin * Y, cos * Y - sin * X)
    }

    companion object {
        val zeroVector = Vector2F(0f, 0f)
    }
}