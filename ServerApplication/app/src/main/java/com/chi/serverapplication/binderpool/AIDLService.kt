package com.chi.serverapplication.binderpool

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AIDLService : Service() {

    companion object {
        const val TAG = "AIDLService"
    }

    private val binder = BinderPoolImpl()

    override fun onBind(intent: Intent): IBinder = binder
}
