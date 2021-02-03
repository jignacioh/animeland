package com.clearmind.animeland.core.di

sealed class Either<out L, out R> {
    //Failure
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    //Success
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <L> left(a: L) = Left(a)
    fun <R> right(b: R) = Right(b)

    fun either(fnL: (L) -> Any, fnR: (R) -> Any): Any =
            when (this) {
                is Left -> {
                    fnL(a)
                }
                is Right -> {
                    fnR(b)
                }
            }

}

 fun <L, R, T> Either<L, R>.fold(left: (L) -> T, right: (R) -> T): T =
        when (this) {
            is Either.Left -> left(a)
            is Either.Right -> right(b)
        }
 fun <L, R, T> Either<L, R>.flatMap(f: (R) -> Either<L, T>): Either<L, T> =
        fold({ this as Either.Left }, f)


 fun <L, R, T> Either<L, R>.map(f: (R) -> T): Either<L, T> =
        flatMap { Either.Right(f(it)) }

/**
 * Composes 2 functions
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

/** Returns the value from this `Right` or the given argument if this is a `Left`.
 *  Right(12).getOrElse(17) RETURNS 12 and Left(12).getOrElse(17) RETURNS 17
 */
fun <L, R> Either<L, R>.getOrElse(value: R): R =
        when (this) {
            is Either.Left -> value
            is Either.Right -> b
        }

/**
 * Left-biased onFailure() FP convention dictates that when this class is Left, it'll perform
 * the onFailure functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
fun <L, R> Either<L, R>.onFailure(fn: (failure: L) -> Unit): Either<L, R> =
        this.apply { if (this is Either.Left) fn(a) }

/**
 * Right-biased onSuccess() FP convention dictates that when this class is Right, it'll perform
 * the onSuccess functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
fun <L, R> Either<L, R>.onSuccess(fn: (success: R) -> Unit): Either<L, R> =
        this.apply { if (this is Either.Right) fn(b) }
