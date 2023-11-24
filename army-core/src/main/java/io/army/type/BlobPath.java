package io.army.type;

import java.nio.file.Path;

public interface BlobPath extends PathParameter {

    /**
     * create {@link BlobPath} instance.
     *
     * @param deleteOnClose true : should delete after close, see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}.
     * @param path          non-null
     * @return non-null
     */
    static BlobPath from(boolean deleteOnClose, Path path) {
        return TypeFactory.blobPath(deleteOnClose, path);
    }

}
