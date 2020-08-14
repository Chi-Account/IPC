package com.chi.serverapplication.binderpool;

interface IBinderPoolInterface {

    /**
     * @param binderCode the unique token of specific Binder.
     * @return specific Binder Who's token is binderCode.
     */
    IBinder queryBinder(int binderCode);
}
