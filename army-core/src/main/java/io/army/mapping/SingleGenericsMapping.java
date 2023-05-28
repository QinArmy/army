package io.army.mapping;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface SingleGenericsMapping<E> extends MappingType.GenericsMappingType {

    Class<E> genericsType();

    interface CollectionMapping<E> extends SingleGenericsMapping<E> {

        Supplier<Collection<E>> collectionConstructor();
    }


    interface ListMapping<E> extends SingleGenericsMapping<E> {

        Supplier<List<E>> listConstructor();

    }

    interface SetMapping<E> extends SingleGenericsMapping<E> {

        Supplier<Set<E>> setConstructor();


    }

}
