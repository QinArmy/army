package io.army.type;

import java.nio.file.Path;

public interface PathParameter extends BigParameter {

    /**
     * Whether delete file or not when close.
     *
     * @return true : delete file  when close.
     * @see java.nio.file.StandardOpenOption#DELETE_ON_CLOSE
     */
    boolean isDeleteOnClose();

    /**
     * file path
     *
     * @return file path
     */
    @Override
    Path value();


}
