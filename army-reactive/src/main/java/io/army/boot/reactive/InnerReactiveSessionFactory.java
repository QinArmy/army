package io.army.boot.reactive;

import io.army.reactive.SessionFactory;

interface InnerReactiveSessionFactory extends SessionFactory, InnerReactiveApiSessionFactory
        , InnerGenericRmSessionFactory {

}
