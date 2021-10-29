package io.army.sync;

import io.army.advice.sync.DomainAdvice;
import io.army.boot.GenericFactoryBuilder;

import java.util.Collection;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link SessionFactoryBuilder}</li>
 *         <li>{@code io.army.boot.sync.TmSessionFactionBuilder}</li>
 *     </ul>
 * </p>
 */
interface SyncSessionFactoryBuilder<T extends SyncSessionFactoryBuilder<T>> extends GenericFactoryBuilder<T> {


    T domainInterceptor(Collection<DomainAdvice> domainInterceptors);
}
