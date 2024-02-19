package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

import java.util.List;

public class RecordArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {


    private final List<MappingType> columnTypeList;


    private RecordArrayType(List<MappingType> columnTypeList) {
        this.columnTypeList = columnTypeList;
    }

    @Override
    public Class<?> javaType() {
        return null;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return null;
    }

    @Override
    public MappingType elementType() {
        return null;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return super.arrayTypeOfThis();
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return null;
    }


}
