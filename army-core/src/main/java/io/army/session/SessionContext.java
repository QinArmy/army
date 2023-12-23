package io.army.session;

/**
 * <p>This interface representing session context,it is designed for some framework ,for example :
 * {@code org.springframework.transaction.PlatformTransactionManager}.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code  io.army.sync.SyncSessionContext}</li>
 *     <li>{@code }</li>
 * </ul>
 *
 * @since 0.6.2
 */
public interface SessionContext {

    SessionFactory sessionFactory();

    <T extends SessionFactory> T sessionFactory(Class<T> factoryClass);


}
