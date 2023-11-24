package io.army.reactive.type;

import io.army.type.BigParameter;
import org.reactivestreams.Publisher;

public interface PublisherParameter extends BigParameter {

    @Override
    Publisher<?> value();

}
