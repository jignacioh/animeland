package com.clearmind.animeland.usecase

import com.clearmind.animeland.core.di.Failure
import com.clearmind.animeland.core.di.Either
import com.clearmind.animeland.core.executor.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


abstract class UseCase<out Type, in Params> (private val executor: Executor) where Type : Any{


    fun uiExecute(uiFun:suspend ()->Unit){
        executor.uiExec{uiFun()}
    }
    fun asyncExecute(asyncFun:suspend()->Unit){
        executor.asyncExec {asyncFun()}
    }

    abstract suspend fun run(params: Params): Either<Failure, Type>


    open operator fun invoke(
        scope: CoroutineScope,
        params: Params,
        onResult: (Either<Failure, Type>) -> Unit = {}
    ) {
        val backgroundJob = scope.async { run(params) }
        scope.launch { onResult(backgroundJob.await()) }
    }

}
/*
abstract class UseCase(private val executor: Executor){

    fun uiExecute(uiFun:suspend ()->Unit){
        executor.uiExec{uiFun()}
    }
    fun asyncExecute(asyncFun:suspend()->Unit){
        executor.asyncExec {asyncFun()}
    }

}*/