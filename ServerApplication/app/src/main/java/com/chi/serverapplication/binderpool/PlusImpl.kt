package com.chi.serverapplication.binderpool

class PlusImpl : IPlusInterface.Stub() {

    override fun plus(number: Int): Int = number + 1
}