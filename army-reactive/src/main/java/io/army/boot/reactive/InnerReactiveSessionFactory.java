package io.army.boot.reactive;

import io.army.reactive.ReactiveSessionFactory;

interface InnerReactiveSessionFactory extends ReactiveSessionFactory, InnerReactiveApiSessionFactory
        , InnerGenericRmSessionFactory {

}
