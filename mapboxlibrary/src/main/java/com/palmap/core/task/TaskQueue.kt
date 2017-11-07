package com.palmap.nagrand.support.task

/**
 * Created by wtm on 2017/8/28.
 */
interface TaskQueue {

    fun add(task: Task<*>): Boolean

    fun container(task: Task<*>): Boolean

    fun remove(task: Task<*>): Boolean

    fun checkPopTask(): Boolean

    fun poll(): Task<*>?

    fun peek(): Task<*>?
}
