package io.army.mapping;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface UnaryGenericsMapping<E> extends MappingType.GenericsMappingType {

    Class<E> genericsType();

    interface CollectionMapping<E> extends UnaryGenericsMapping<E> {

        Supplier<Collection<E>> collectionConstructor();
    }


    interface ListMapping<E> extends UnaryGenericsMapping<E> {

        Supplier<List<E>> listConstructor();

    }

    interface SetMapping<E> extends UnaryGenericsMapping<E> {

        Supplier<Set<E>> setConstructor();


    }

}
