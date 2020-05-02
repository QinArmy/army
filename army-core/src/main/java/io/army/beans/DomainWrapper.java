package io.army.beans;

public interface DomainWrapper extends BeanWrapper, DomainReadonlyWrapper {

    @Override
    DomainReadonlyWrapper getReadonlyWrapper();


}
