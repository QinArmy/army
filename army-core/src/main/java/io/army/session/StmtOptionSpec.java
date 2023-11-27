package io.army.session;

import io.army.session.record.ResultStates;

import java.util.function.Consumer;


/**
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link StmtOption}</li>
 *     <li>{@code  io.army.sync.StreamOption}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface StmtOptionSpec {


    int fetchSize();

    Consumer<ResultStates> stateConsumer();


    interface OptionBuilderSpec<B> {

        B fetchSize(int value);

        B stateConsumer(Consumer<ResultStates> consumer);

    }

}
