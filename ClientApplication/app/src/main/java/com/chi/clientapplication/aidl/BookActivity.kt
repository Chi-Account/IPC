package com.chi.clientapplication.aidl

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chi.clientapplication.R
import com.chi.serverapplication.aidl.Book
import com.chi.serverapplication.aidl.BookListener
import com.chi.serverapplication.aidl.IBookInterface
import kotlin.concurrent.thread

class BookActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BookActivity"
    }

    var bookInterface: IBookInterface? = null

    var isBind = false

    val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "onServiceConnected")
            service?.let {
                bookInterface = IBookInterface.Stub.asInterface(service)
                // 注册死亡监听
                bookInterface?.asBinder()?.linkToDeath(deathRecipient, 0)
                bookInterface?.addBookListener(bookListener)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected")
            bookInterface = null
            // 重新绑定远程服务
        }
    }

    val bookListener = object : BookListener.Stub() {

        override fun onNewBookAdded(book: Book?) {
            Log.i(TAG, "onNewBookAdded: $book")
        }
    }

    val deathRecipient = object : IBinder.DeathRecipient {

        override fun binderDied() {
            bookInterface?.asBinder()?.unlinkToDeath(this, 0)
            bookInterface = null
            // 重新绑定远程服务
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBind) {
            bookInterface?.removeBookListener(bookListener)
            unbindService(serviceConnection)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.button -> button()
            R.id.button2 -> button2()
        }
    }

    /**
     * 绑定服务端 Service
     */
    fun button() {
        if (isBind) {
            Toast.makeText(this, "服务已绑定", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent("com.chi.serverapplication.ACTION_BOOK_SERVICE")
            intent.setPackage("com.chi.serverapplication")
            isBind = bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
            if (isBind) {
                Toast.makeText(this, "服务绑定成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "服务绑定失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun button2() {
        /**
         * 客户端调用服务端方法，服务端处理过程中，客户端线程被挂起
         */
        thread {
            val bookList = bookInterface?.bookList
            runOnUiThread {
                if (bookList == null) {
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "Book List: $bookList")
                }
            }
        }
    }
}
