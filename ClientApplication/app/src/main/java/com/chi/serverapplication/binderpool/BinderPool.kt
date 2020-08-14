package com.chi.serverapplication.binderpool

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

object BinderPool {

    const val TAG = "BinderPool"

    const val PLUS = BinderPoolImpl.PLUS

    const val MINUS = BinderPoolImpl.MINUS

    private var iBinderPoolInterface: IBinderPoolInterface? = null

    private val queryBinderRequestList = ArrayList<QueryBinderRequest>()

    private lateinit var lContext: Context

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG,"queryBinder")
            iBinderPoolInterface = IBinderPoolInterface.Stub.asInterface(service)
            while (queryBinderRequestList.isNotEmpty()) {
                val queryBinderRequest = queryBinderRequestList.removeAt(0)
                queryBinderRequest.listener.onQueryBinderResult(
                    queryBinderRequest.binderCode,
                    iBinderPoolInterface?.queryBinder(queryBinderRequest.binderCode)
                )
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iBinderPoolInterface = null
            bindService()
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private const val QUERY_TIMEOUT: Long = 10 * 1000

    private val timeoutRunnable = Runnable {
        val list = ArrayList<QueryBinderRequest>()
        for (item in queryBinderRequestList) {
            if ((System.currentTimeMillis() - item.createTime) >= QUERY_TIMEOUT) {
                list.add(item)
            }
        }
        queryBinderRequestList.removeAll(list)
        for (item in list) {
            item.listener.onQueryBinderResult(item.binderCode, null)
        }
    }

    fun queryBinder(context: Context, binderCode: Int, listener: Listener) {
        if (!::lContext.isInitialized) {
            lContext = context.applicationContext
        }
        if (iBinderPoolInterface == null) {
            val queryBinderRequest =
                QueryBinderRequest(System.currentTimeMillis(), binderCode, listener)
            queryBinderRequestList.add(queryBinderRequest)
            bindService()
        } else {
            val binder = iBinderPoolInterface?.queryBinder(binderCode)
            listener.onQueryBinderResult(binderCode, binder)
        }
    }

    private fun bindService() {
        val intent = Intent("com.chi.serverapplication.ACTION_AIDL_SERVICE")
        intent.setPackage("com.chi.serverapplication")
        val isBind = lContext.bindService(
            intent,
            serviceConnection,
            Service.BIND_AUTO_CREATE
        )
        if (!isBind) {
            Log.i(TAG, "绑定失败")
            while (queryBinderRequestList.isNotEmpty()) {
                val queryBinderRequest = queryBinderRequestList.removeAt(0)
                queryBinderRequest.listener.onQueryBinderResult(queryBinderRequest.binderCode, null)
            }
        } else {
            handler.postDelayed(timeoutRunnable, QUERY_TIMEOUT)
        }
    }

    data class QueryBinderRequest(val createTime: Long, val binderCode: Int, val listener: Listener)

    interface Listener {

        fun onQueryBinderResult(binderCode: Int, binder: IBinder?)
    }
}