package com.clearmind.animeland.core.di


sealed class Failure(val exception: Exception = Exception("Failure")) {
    object None : Failure()
    object NetworkConnection : Failure()
    object ServerError : Failure()

    /** * Extend for custom error.*/
    open class FeatureFailure(featureException: Exception = Exception("Feature failure")) : Failure(featureException)

    override fun equals(other: Any?): Boolean {
        return other is Failure
    }
}