package io.army.stmt;

import io.army.beans.ObjectWrapper;

import java.util.List;

public interface ValueInsertStmt extends SimpleStmt {

     List<ObjectWrapper> domainWrappers();
}
