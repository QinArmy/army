package io.army.beans;

class DirectFieldAccessor extends org.springframework.beans.DirectFieldAccessor implements ConfigurablePropertyAccessor {

    DirectFieldAccessor(Object object) {
        super(object);
    }


}
