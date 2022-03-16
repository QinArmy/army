package io.army.sync;

import io.army.session.GenericCurrentSession;

/**
 * This interface is a proxy of {@link Session} in current thread context.
 * Classic use case is than is used by DAO in spring application.
 */
public interface CurrentSession extends GenericCurrentSession, SyncSession {


}
