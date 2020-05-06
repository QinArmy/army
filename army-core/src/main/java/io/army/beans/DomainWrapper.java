package io.army.beans;

public interface DomainWrapper extends ObjectWrapper, DomainReadonlyWrapper {

    @Override
    DomainReadonlyWrapper getReadonlyWrapper();


}
