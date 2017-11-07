package com.palmap.nagrand.support.task

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by wtm on 2017/8/29.
 */
abstract class Task<T> {

    var state: TaskState = TaskState.WAIT

    var cancel : AtomicBoolean = AtomicBoolean(false)

    abstract fun doInBackground(): T

    open fun onSuccess(t: T) {}

    open fun onError(throwable: Throwable) {}

}