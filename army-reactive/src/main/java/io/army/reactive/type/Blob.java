package io.army.reactive.type;

import org.reactivestreams.Publisher;

public interface Blob extends PublisherParameter {

    @Override
    Publisher<byte[]> value();

    /**
     * create {@link Blob} instance.
     *
     * @param source non-null
     * @return non-null
     */
    static Blob from(Publisher<byte[]> source) {
        return TypeFactory.blobParam(source);
    }

}
