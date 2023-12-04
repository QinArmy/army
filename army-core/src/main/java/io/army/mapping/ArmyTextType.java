package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;


/**
 * <p>Package class
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link TinyTextType}</li>
 *     <li>{@link TextType}</li>
 *     <li>{@link MediumTextType}</li>
 * </ul>
 *
 * @since 1.0
 */
abstract class ArmyTextType extends _ArmyBuildInMapping implements MappingType.SqlTextType {


    ArmyTextType() {
    }

    @Override
    public final Class<?> javaType() {
        return String.class;
    }


    @Override
    public final String convert(MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return StringType.toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
