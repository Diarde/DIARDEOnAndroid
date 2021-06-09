package com.studiocinqo.diardeonandroid.engine3d

class Vector3D(val X: Double, val Y: Double, val Z: Double) {

    fun add(other: Vector3D): Vector3D {
        return Vector3D(X + other.X, Y + other.Y, Z + other.Z)
    }

    fun subs(other: Vector3D): Vector3D {
        return Vector3D(X - other.X, Y - other.Y, Z - other.Z)
    }

    fun scale(factor: Double): Vector3D {
        return Vector3D(factor * X, factor * Y, factor * Z)
    }

    fun dot(other: Vector3D): Double {
        return X * other.X + Y * other.Y + Z * other.Z
    }

    fun cross(other: Vector3D): Vector3D {
        return Vector3D((other.Y * Z) - (other.Z * Y), (other.Z * X) - (other.X * Z), (other.X * Y) - (other.Y * X))
    }

    fun norm(): Double {
        return Math.sqrt(X * X + Y * Y + Z * Z)
    }

    fun normalise(): Vector3D {
        val norm: Double = Math.sqrt(X * X + Y * Y + Z * Z)
        return Vector3D(X / norm, Y / norm, Z / norm)
    }

    fun distance(other: Vector3D): Double {
        return Math.sqrt(
            (X - other.X) * (X - other.X) + (Y - other.Y) * (Y - other.Y) + (Z - other.Z) * (Z - other.Z)
        )
    }

    companion object {
        val zeroVector = Vector3D(0.0, 0.0, 0.0)
    }
}