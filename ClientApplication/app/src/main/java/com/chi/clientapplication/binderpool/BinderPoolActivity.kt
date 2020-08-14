package com.chi.clientapplication.binderpool

import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chi.clientapplication.R
import com.chi.serverapplication.binderpool.BinderPool
import com.chi.serverapplication.binderpool.IMinusInterface
import com.chi.serverapplication.binderpool.IPlusInterface

class BinderPoolActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BinderPoolActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binder_pool)
    }

    fun onClick(view: View) {
        BinderPool.queryBinder(this, BinderPool.PLUS, object : BinderPool.Listener {
            override fun onQueryBinderResult(binderCode: Int, binder: IBinder?) {
                if (binder == null) {
                    Log.i(TAG, "获取服务失败")
                } else {
                    val iPlusInterface = IPlusInterface.Stub.asInterface(binder)
                    val result = iPlusInterface.plus(1)
                    Log.i(TAG, "result: $result")
                }
            }
        })
        BinderPool.queryBinder(this, BinderPool.MINUS, object : BinderPool.Listener {
            override fun onQueryBinderResult(binderCode: Int, binder: IBinder?) {
                if (binder == null) {
                    Log.i(TAG, "获取服务失败")
                } else {
                    val iMinusInterface = IMinusInterface.Stub.asInterface(binder)
                    val result = iMinusInterface.minus(1)
                    Log.i(TAG, "result: $result")
                }
            }
        })
    }
}
