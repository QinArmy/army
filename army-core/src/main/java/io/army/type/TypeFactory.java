package io.army.type;

import io.army.util._StringUtils;

import java.nio.charset.Charset;
import java.nio.file.Path;

abstract class TypeFactory {

    private TypeFactory() {
        throw new UnsupportedOperationException();
    }


    static TextPath textPath(boolean deleteOnClose, Charset charset, Path path) {
        return new ArmyTextPath(deleteOnClose, charset, path);
    }

    static BlobPath blobPath(boolean deleteOnClose, Path path) {
        return new ArmyBlobPath(deleteOnClose, path);
    }


    private static final class ArmyBlobPath implements BlobPath {

        private final boolean deleteOnClose;
        private final Path path;

        private ArmyBlobPath(boolean deleteOnClose, Path path) {
            this.deleteOnClose = deleteOnClose;
            this.path = path;
        }

        @Override
        public boolean isDeleteOnClose() {
            return this.deleteOnClose;
        }

        @Override
        public Path value() {
            return this.path;
        }


        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(getClass().getName())
                    .append("[deleteOnClose:")
                    .append(this.deleteOnClose)
                    .append(",path:")
                    .append(this.path)
                    .append(",hash:")
                    .append(System.identityHashCode(this))
                    .append(']')
                    .toString();
        }


    } // ArmyBlobPath


    private static final class ArmyTextPath implements TextPath {

        private final boolean deleteOnClose;
        private final Charset charset;
        private final Path path;

        private ArmyTextPath(boolean deleteOnClose, Charset charset, Path path) {
            this.deleteOnClose = deleteOnClose;
            this.charset = charset;
            this.path = path;
        }

        @Override
        public boolean isDeleteOnClose() {
            return this.deleteOnClose;
        }

        @Override
        public Path value() {
            return this.path;
        }

        @Override
        public Charset charset() {
            return this.charset;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(getClass().getName())
                    .append("[deleteOnClose:")
                    .append(this.deleteOnClose)
                    .append(",charset：")
                    .append(this.charset.name())
                    .append(",path:")
                    .append(this.path)
                    .append(",hash:")
                    .append(System.identityHashCode(this))
                    .append(']')
                    .toString();
        }

    } // ArmyTextPath


}
