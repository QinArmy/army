package io.army.tx;

import io.army.dialect.Database;

import javax.transaction.xa.Xid;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ArmyXid implements Xid {

    private static final int INDEX_FORMAT = 1;

    private static final int DIALECT_INDEX_FORMAT = 2;

    private final byte[] gtrid;

    private final byte[] bqual;

    private final int formatID;

    public ArmyXid(byte[] gtrid, int databaseIndex) {
        this.gtrid = gtrid;
        this.bqual = toBytes(databaseIndex);
        this.formatID = INDEX_FORMAT;
    }

    public ArmyXid(byte[] gtrid, Database database, int databaseIndex) {
        this.gtrid = gtrid;
        this.bqual = toBytes(database, databaseIndex);
        this.formatID = DIALECT_INDEX_FORMAT;
    }

    @Override
    public int getFormatId() {
        return this.formatID;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.gtrid;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.bqual;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.gtrid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Xid)) {
            return false;
        }
        Xid xid = (Xid) obj;
        return Arrays.equals(xid.getGlobalTransactionId(), this.gtrid)
                && Arrays.equals(xid.getBranchQualifier(), this.bqual)
                && xid.getFormatId() == this.formatID
                ;
    }


    private static byte[] toBytes(final int databaseIndex) {
        byte[] bytes = new byte[4];
        int num = databaseIndex;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (num & 0xF);
            num >>= 8;
        }
        return bytes;
    }

    private static byte[] toBytes(Database database, int databaseIndex) {
        byte[] databaseBytes = database.name().getBytes(StandardCharsets.UTF_8);
        byte[] databaseIndexBytes = toBytes(databaseIndex);
        byte[] bytes = new byte[databaseBytes.length + databaseIndexBytes.length];

        for (int i = 0; i < bytes.length; i++) {
            if (i < databaseBytes.length) {
                bytes[i] = databaseBytes[i];
            } else {
                bytes[i] = databaseIndexBytes[i];
            }
        }
        return bytes;
    }
}
