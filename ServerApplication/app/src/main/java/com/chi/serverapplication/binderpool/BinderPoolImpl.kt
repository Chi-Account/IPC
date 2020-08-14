package com.chi.serverapplication.binderpool

import android.os.IBinder

class BinderPoolImpl : IBinderPoolInterface.Stub() {

    companion object {
        const val PLUS = 1
        const val MINUS = 2
    }

    override fun queryBinder(binderCode: Int): IBinder? = when (binderCode) {
        PLUS -> PlusImpl()
        MINUS -> MinusImpl()
        else -> null
    }
}