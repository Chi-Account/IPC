package com.chi.serverapplication.aidl

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteCallbackList
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class BookService : Service() {

    companion object {
        const val TAG = "BookService"
    }

    val isRunning = AtomicBoolean(false)

    /**
     * CopyOnWriteArrayList 支持并发读写。
     * 虽然 AIDL 接口只支持 ArrayList, 由于声明时使用的是 List,
     * 会按照 List 的规范去访问 bookList,
     * 最后生成一个新的 ArrayList 返回给客户端。
     */
    val bookList = CopyOnWriteArrayList<Book>()

    val listenerList = RemoteCallbackList<BookListener>()

    val binder = object : IBookInterface.Stub() {

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (checkCallingPermission("com.chi.serverapplication.permission.BOOK_SERVICE") == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission Granted")
                return super.onTransact(code, data, reply, flags)
            } else {
                Log.i(TAG, "Permission Denied")
                return false
            }
        }

        override fun addBook(book: Book?) {
            book?.let {
                Log.i(TAG, "addBook: $it")
                this@BookService.bookList.add(it)
            }
        }

        override fun getBookList(): MutableList<Book> {
            Log.i(TAG, "getBookList: ${this@BookService.bookList}")
            return this@BookService.bookList
        }

        override fun addBookListener(listener: BookListener?) {
            listenerList.register(listener)
            // 通过 beginBroadcast() 获取元素数量
            // beginBroadcast() 和 finishBroadcast() 配对使用
            val listenerCount = listenerList.beginBroadcast()
            Log.i(TAG, "addBookListener: $listenerCount")
            listenerList.finishBroadcast()
        }

        override fun removeBookListener(listener: BookListener?) {
            listenerList.unregister(listener)
            val listenerCount = listenerList.beginBroadcast()
            Log.i(TAG, "removeBookListener: $listenerCount")
            listenerList.finishBroadcast()
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning.set(true)
        bookList.add(Book("Book ${bookList.size + 1}"))
        bookList.add(Book("Book ${bookList.size + 1}"))
        thread {
            while (isRunning.get()) {
                try {
                    val book = Book("Book ${bookList.size + 1}")
                    bookList.add(book)
                    val listenerCount = listenerList.beginBroadcast()
                    for (i in 0 until listenerCount) {
                        listenerList.getBroadcastItem(i)?.onNewBookAdded(book)
                    }
                    listenerList.finishBroadcast()
                    Thread.sleep(2000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning.set(false)
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind")
        return super.onUnbind(intent)
    }
}
