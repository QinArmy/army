package io.army.dialect;

import io.army.criteria.NamedParam;
import io.army.criteria.Visible;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;
import io.army.stmt.StrictParamValue;

import java.util.ArrayList;
import java.util.List;

 abstract class StmtContext implements _StmtContext {

     static final String SPACE_PLACEHOLDER = " ?";

     protected final ArmyDialect dialect;

     protected final Visible visible;

     protected final StringBuilder sqlBuilder;

     protected final List<ParamValue> paramList;

     protected StmtContext(ArmyDialect dialect, Visible visible) {
         this.dialect = dialect;
         this.visible = visible;
         this.sqlBuilder = new StringBuilder(128);
         if (this instanceof _InsertBlock) {
             this.paramList = createParamList();
         } else {
             this.paramList = new ArrayList<>();
         }

     }

     protected StmtContext(StmtContext outerContext) {
         this.dialect = outerContext.dialect;
         this.visible = outerContext.visible;
         this.sqlBuilder = outerContext.sqlBuilder;
         this.paramList = outerContext.paramList;
     }


    @Override
    public final _Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }


    @Override
    public final void appendParam(final ParamValue paramValue) {
        if (this instanceof _InsertBlock
                && !(paramValue instanceof StrictParamValue || paramValue instanceof NamedParam)) {
            this.valueInsertAppendParam(paramValue);
        } else {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            this.paramList.add(paramValue);
        }

    }

    @Override
    public final Visible visible() {
        return this.visible;
    }


    List<ParamValue> createParamList() {
        return new ArrayList<>();
    }

    private void valueInsertAppendParam(final ParamValue paramValue) {
        final ParamMeta paramMeta = paramValue.paramMeta();
        if (paramMeta.mappingType() instanceof _ArmyNoInjectionMapping) {
            final Object value;
            value = paramValue.value();
            if (value == null) {
                this.sqlBuilder.append(Constant.SPACE_NULL);
            } else {
                this.sqlBuilder.append(Constant.SPACE)
                        .append(this.dialect.literal(paramMeta, value));
            }
        } else {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            this.paramList.add(paramValue);
        }
    }


}
