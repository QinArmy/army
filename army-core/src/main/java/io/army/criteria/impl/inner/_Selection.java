package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.TableField;
import io.army.lang.Nullable;

 public interface _Selection extends Selection, _SelectItem {


     @Nullable
     TableField tableField();

     @Nullable
     Expression underlyingExp();


}
