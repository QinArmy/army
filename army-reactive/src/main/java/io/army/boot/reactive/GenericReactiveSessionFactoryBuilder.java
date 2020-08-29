package io.army.boot.reactive;

import io.army.boot.GenericFactoryBuilder;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;

import java.util.Collection;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link ReactiveSessionFactoryBuilder}</li>
 *         <li>{@code io.army.boot.reactive.ReactiveTmSessionFactoryBuilder}</li>
 *     </ul>
 * </p>
 */
interface GenericReactiveSessionFactoryBuilder<T extends GenericReactiveSessionFactoryBuilder<T>>
        extends GenericFactoryBuilder<T> {

    T waitCreateSeconds(int seconds);

    T domainInsertAdvice(Collection<ReactiveDomainInsertAdvice> insertAdvices);

    T domainUpdateAdvice(Collection<ReactiveDomainUpdateAdvice> updateAdvices);

    T domainDeleteAdvice(Collection<ReactiveDomainDeleteAdvice> deleteAdvices);

}
