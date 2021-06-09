package com.studiocinqo.diardeonandroid.utility

class EitherMonad<T, W> {

    private val left: T?
    private val right: W?

    private constructor(left: T?, right: W?) {
        this.left = left
        this.right = right
    }

    fun fold(onLeft: (value: T) -> Unit, onRight: (value: W) -> Unit) {
        left?.run {
            onLeft(this)

        }
        right?.run {
            onRight(this);
        }
    }

    fun isLeft(): Boolean {
        return this.left !== null;
    }

    fun isRight(): Boolean {
        return this.right !== null;
    }

    companion object {

        fun <T, W> left(value: T): EitherMonad<T, W> {
            return EitherMonad<T, W>(value, null)
        }

        fun <T, W> right(value: W): EitherMonad<T, W> {
            return EitherMonad<T, W>(null, value)
        }

    }

}