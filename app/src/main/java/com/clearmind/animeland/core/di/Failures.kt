package com.clearmind.animeland.core.di

sealed class GetTasksFailure {
    class NetworkConnection: GetTasksFailure()
}
