package io.army.sync;

import io.army.codec.CodecContext;
import io.army.codec.StatementType;
import io.army.lang.Nullable;

interface InnerCodecContext extends CodecContext {

    void statementType(@Nullable StatementType statementType);
}
