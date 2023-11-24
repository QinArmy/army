package io.army.type;

import java.nio.charset.Charset;
import java.nio.file.Path;

public interface TextPath extends PathParameter {


    /**
     * @return text file charset
     */
    Charset charset();

    /**
     * create {@link TextPath} instance.
     *
     * @param deleteOnClose true : should delete after close, see {@link java.nio.file.StandardOpenOption#DELETE_ON_CLOSE}.
     */
    static TextPath from(boolean deleteOnClose, Charset charset, Path path) {
        return TypeFactory.textPath(deleteOnClose, charset, path);
    }

}
