package io.army.boot.reactive;

import io.army.reactive.ReactiveSession;

interface InnerReactiveSession extends ReactiveSession, InnerGenericRmSession, InnerTransactionSession {


}
