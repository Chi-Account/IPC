package com.chi.serverapplication.binderpool

class MinusImpl : IMinusInterface.Stub() {

    override fun minus(number: Int): Int = number - 1
}