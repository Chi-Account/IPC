package com.chi.serverapplication.messenger

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

/**
 * 1. 处理客户端的请求的 Service
 */
class MessengerService : Service() {

    companion object {
        const val TAG = "MessengerService"
    }

    /**
     * 3. 创建 Handler 对象，并通过它创建 Messenger 对象
     */
    private val messenger = Messenger(MessengerHandler())

    /**
     * 4. 在 onBind() 中返回 Messenger 对象的 Binder
     */
    override fun onBind(intent: Intent): IBinder = messenger.binder

    /**
     * 2. 处理客户端的请求的 Handler
     */
    class MessengerHandler : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    val data = msg.data.getString("data") ?: ""
                    Log.i(TAG, data)

                    /**
                     * Message 的 replyTo 是一个 Messenger 类型的对象
                     * 如果不为空，通过它回复客户端
                     */
                    msg.replyTo?.apply {
                        val bundle = Bundle()
                        bundle.putString("data", "From Server")

                        val message = Message.obtain()
                        message.what = 1
                        message.data = bundle
                        try {
                            send(message)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }
}
