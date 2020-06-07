package io.army.codec;

import io.army.meta.FieldMeta;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractImmutableKeyFieldCodec implements FieldCodec {


    @Override
    public final Object encode(FieldMeta<?, ?> fieldMeta, Object nonNullFieldValue, CodecContext codecContext)
            throws FieldCodecException {
        try {
            return doEncode(fieldMeta, nonNullFieldValue);
        } catch (IllegalStateException e) {
            throw FieldCodecException.KeyError("not found key[%s]", getCodecKeyName());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw FieldCodecException.KeyError("codec key[%s] error.", getCodecKeyName());
        } catch (Exception e) {
            throw FieldCodecException.dataError("FieldMeta[%s] cannot encode by %s"
                    , fieldMeta, getCodecKeyName());
        }
    }

    @Override
    public final Object decode(FieldMeta<?, ?> fieldMeta, Object nonNullValueFromDB, CodecContext codecContext)
            throws FieldCodecException {
        try {
            return doDecode(fieldMeta, nonNullValueFromDB);
        } catch (IllegalStateException e) {
            throw FieldCodecException.KeyError("not found key[%s]", getCodecKeyName());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw FieldCodecException.KeyError("codec key[%s] error.", getCodecKeyName());
        } catch (Exception e) {

            throw FieldCodecException.dataError("FieldMeta[%s] cannot decode by %s", fieldMeta, getCodecKeyName());
        }
    }

    protected abstract String getCodecKeyName();


    protected abstract Object doEncode(FieldMeta<?, ?> fieldMeta, Object nonNullFieldValue)
            throws Exception;

    protected abstract Object doDecode(FieldMeta<?, ?> fieldMeta, Object nonNullValueFromDB)
            throws Exception;

}
