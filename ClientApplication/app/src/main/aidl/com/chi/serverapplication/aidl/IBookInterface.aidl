package com.chi.serverapplication.aidl;

import com.chi.serverapplication.aidl.Book;
import com.chi.serverapplication.aidl.BookListener;

interface IBookInterface {

    void addBook(in Book book);

    List<Book> getBookList();

    void addBookListener(in BookListener listener);

    void removeBookListener(in BookListener listener);
}
