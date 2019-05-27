package com.clearmind.animeland.core.executor

interface Executor {
    fun uiExec(uiFun:suspend ()->Unit)
    fun asyncExec(asyncFun:suspend()->Unit)
}