package io.army.mapping.mysql;

import io.army.dialect.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

public final class MySQLLongBlobType extends AbstractMappingType {

    public static final MySQLLongBlobType BYTE_ARRAY_INSTANCE = new MySQLLongBlobType(byte[].class);

    public static final MySQLLongBlobType PATH_INSTANCE = new MySQLLongBlobType(Path.class);

    public static final long MAX_LENGTH = 0xFFFF_FFFFL;

    public static MySQLLongBlobType from(final Class<?> fieldType) {
        final MySQLLongBlobType instance;
        if (fieldType == byte[].class) {
            instance = BYTE_ARRAY_INSTANCE;
        } else if (fieldType == Path.class) {
            instance = PATH_INSTANCE;
        } else {
            throw errorJavaType(MySQLLongBlobType.class, fieldType);
        }
        return instance;
    }

    private final Class<?> javaType;

    private MySQLLongBlobType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySQLTypes.LONGBLOB;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        if (nonNull instanceof Path) {
            if (Files.notExists((Path) nonNull)) {
                String m = String.format("%s not exists.", nonNull);
                throw outRangeOfSqlType(sqlType, nonNull, new IOException(m));
            }
            try {
                final long size;
                size = Files.size((Path) nonNull);
                if (size > MAX_LENGTH) {
                    String m = String.format("%s size[%s] > %s.MAX_LENGTH[%s].", nonNull, size
                            , MySQLLongBlobType.class.getName(), MAX_LENGTH);
                    throw outRangeOfSqlType(sqlType, nonNull, new IllegalArgumentException(m));
                }
            } catch (IOException e) {
                throw outRangeOfSqlType(sqlType, nonNull, e);
            }
        } else if (!(nonNull instanceof byte[])) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return nonNull;
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        final Object value;
        if (this.javaType == byte[].class) {
            if (nonNull instanceof byte[]) {
                value = nonNull;
            } else if (nonNull instanceof Path) {
                value = readBytesFromPath(sqlType, (Path) nonNull);
            } else {
                throw errorJavaTypeForSqlType(sqlType, nonNull);
            }
        } else if (this.javaType == Path.class) {
            if (nonNull instanceof Path) {
                value = nonNull;
            } else if (nonNull instanceof byte[]) {
                value = writeBytesToPath(sqlType, (byte[]) nonNull);
            } else {
                throw errorJavaTypeForSqlType(sqlType, nonNull);
            }
        } else {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        return value;
    }


    private static Path writeBytesToPath(final SqlType sqlType, byte[] bytes) {
        final Path dir = Paths.get(System.getProperty("java.io.tmpdir"), "army/mysql/", LocalDate.now().toString());
        try {
            final Path tempFile;
            tempFile = Files.createTempFile(dir, "longblob", ".blob");
            try (FileChannel channel = FileChannel.open(tempFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                channel.write(ByteBuffer.wrap(bytes));
            }
            return tempFile;
        } catch (IOException e) {
            throw errorValueForSqlType(sqlType, "{byte array}", e);
        }
    }

    private static byte[] readBytesFromPath(final SqlType sqlType, final Path path) {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE)) {
            final long size;
            size = channel.size();
            if (size > Integer.MAX_VALUE) {
                throw errorValueForSqlType(sqlType, path, valueOutOfMapping(path, MySQLLongBlobType.class));
            }
            final byte[] byteBuffer = new byte[(int) size];
            if (channel.read(ByteBuffer.wrap(byteBuffer)) != byteBuffer.length) {
                //jvm no bug,never here
                throw new IOException(String.format("%s size[%s],but read length not match.", path, size));
            }
            return byteBuffer;
        } catch (IOException e) {
            throw errorValueForSqlType(sqlType, path, e);
        }
    }


}
