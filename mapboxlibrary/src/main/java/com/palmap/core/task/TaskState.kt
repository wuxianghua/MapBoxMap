package com.palmap.nagrand.support.task

/**
 * Created by wtm on 2017/8/28.
 */
enum class TaskState {
    WAIT,//等待执行
    Runing, //执行中
    OK, //执行完成
    ERROR,//执行错误
}