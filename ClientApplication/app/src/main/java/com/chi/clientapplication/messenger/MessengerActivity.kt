package com.chi.clientapplication.messenger

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chi.clientapplication.R

class MessengerActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MessengerActivity"
    }

    lateinit var serverMessenger: Messenger

    /**
     * 接收服务端回复的 Messenger
     */
    val clientMessenger = Messenger(MessengerHandler())

    val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.let {
                /**
                 * 2. 绑定成功后用服务端返回的 IBinder 对象创建一个 Messenger
                 */
                serverMessenger = Messenger(it)

                val bundle = Bundle()
                bundle.putString("data", "From Client")

                val message = Message.obtain()
                message.what = 1
                message.data = bundle
                /**
                 * Message 的 replyTo 是一个 Messenger 类型的对象
                 * 如果不为空，服务端通过它回复
                 */
                message.replyTo = clientMessenger
                /**
                 * 3. 通过 Messenger 向服务端发送请求
                 */
                try {
                    serverMessenger.send(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        /**
         * 1. 绑定服务端的 Service
         */
        val intent = Intent("com.chi.serverapplication.ACTION_MESSENGER_SERVICE")
        intent.setPackage("com.chi.serverapplication")
        bindService(
            intent,
            serviceConnection,
            Service.BIND_AUTO_CREATE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    /**
     * 接收服务端回复的 Handler
     */
    class MessengerHandler : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    val data = msg.data.getString("data") ?: ""
                    Log.i(TAG, data)
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }
}
