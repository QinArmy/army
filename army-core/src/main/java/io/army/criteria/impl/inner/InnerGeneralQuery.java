package io.army.criteria.impl.inner;

import io.army.criteria.SelectPart;

import java.util.List;

@DeveloperForbid
public interface InnerGeneralQuery extends InnerSQL {

    List<SelectPart> selectPartList();

}
