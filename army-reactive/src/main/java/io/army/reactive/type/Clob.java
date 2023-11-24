package io.army.reactive.type;

import org.reactivestreams.Publisher;

public interface Clob extends PublisherParameter {


    /**
     * publisher
     *
     * @return {@link String} {@link Publisher}
     */
    Publisher<String> value();

    /**
     * create {@link Blob} instance.
     *
     * @param source non-null
     * @return non-null
     */
    static Clob from(Publisher<String> source) {
        return TypeFactory.clobParam(source);
    }

}
