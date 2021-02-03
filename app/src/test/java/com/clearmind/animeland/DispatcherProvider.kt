package com.clearmind.animeland

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {

    var io: CoroutineDispatcher
    val ui: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}