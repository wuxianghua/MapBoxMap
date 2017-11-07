package com.palmap.nagrand.support.task

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*


/**
 * Created by wtm on 2017/8/29.
 */
class TaskManager constructor(private val taskQueue: TaskQueue = ArrayBlockTaskQueue(),
                              private val exec: Executor = ThreadPoolExecutor(15,
                                      200,
                                      10,
                                      TimeUnit.SECONDS,
                                      LinkedBlockingQueue<Runnable>())) {

    private val mainHandler = Handler(Looper.getMainLooper())

    private class ArrayBlockTaskQueue constructor(capacity: Int = 20) : TaskQueue {

        override fun poll(): Task<*>? {
            return this.deque.poll()
        }

        override fun peek(): Task<*>? {
            return this.deque.peek()
        }

        override fun checkPopTask(): Boolean {
            val task = this.peek()
            return task != null && (task.state == TaskState.OK || task.state == TaskState.ERROR)
        }

        val deque: ArrayBlockingQueue<Task<*>> = ArrayBlockingQueue(capacity)

        override fun add(task: Task<*>): Boolean {
            return this.deque.add(task)
        }

        override fun  container(task: Task<*>): Boolean {
            return this.deque.contains(task)
        }

        override fun remove(task: Task<*>): Boolean {
           return this.deque.remove(task)
        }

    }

    private inner class LocalTask<T>(
            private val task: Task<T>,
            private val taskQueue: TaskQueue
    ) : Task<T>(), Runnable {

        init {
            this.cancel = task.cancel
        }

        private var throwable: Throwable? = null
        private var result: T? = null

        override fun run() {
            if(this.cancel.get()){
                return
            }
            this.state = TaskState.Runing
            try {
                result = this.doInBackground()
                this.state = TaskState.OK
            } catch (e: Exception) {
                this.state = TaskState.ERROR
                this.throwable = e
            } finally {

                while (this.taskQueue.checkPopTask()){
                    val task = this.taskQueue.poll() as LocalTask<T>
                        if(task.cancel.get()){
                            continue
                        }
                        if (task.state === TaskState.OK && null != task.result) {
                            mainHandler.post { task.onSuccess(task.result!!) }
                        } else {
                            mainHandler.post {
                                task.onError(if (null == task.throwable) NullPointerException("UnKnow exception !!!") else task.throwable!!) }
                        }
                }
            }
        }

        override fun doInBackground(): T {
            return this.task.doInBackground()
        }

        override fun onSuccess(t: T) {
            if(task.cancel.get()){
                return
            }
            this.task.onSuccess(t)
        }

        override fun onError(throwable: Throwable) {
            if(task.cancel.get()){
                return
            }
            this.task.onError(throwable)
        }

        init {
            this.taskQueue.add(this)
        }

    }

    fun <T> execTask(task: Task<T>) {
        this.exec.execute(LocalTask(task, this.taskQueue))
    }

    fun cancelAll(){
        this.taskQueue.poll()?.cancel?.compareAndSet(false,true)
    }

}