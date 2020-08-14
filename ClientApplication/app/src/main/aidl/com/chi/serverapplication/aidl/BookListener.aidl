package com.chi.serverapplication.aidl;

import com.chi.serverapplication.aidl.Book;

interface BookListener {

    void onNewBookAdded(in Book book);
}
