package io.army.tx.sync;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;


/**
 * @see TransactionDefinitionHolder
 * @see TransactionInterceptor
 * @see Transactional
 */
public class TransactionDefinitionInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionDefinitionInterceptor.class);


    private enum DefType {
        INNER,
        OUTER,
        NONE,
        NOT_SUPPORTED,
        ERROR
    }

    private TransactionAttributeSource transactionAttributeSource;

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        transactionAttributeSource = applicationContext.getBean(TransactionAttributeSource.class);
        Assert.notNull(transactionAttributeSource, "config error");
    }

    /**
     * @see TransactionInterceptor#invoke(MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // Work out the target class: may be {@code null}.
        // The TransactionAttributeSource should be passed the target class
        // as well as the method, which may be from an interface.
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        // get target method with @Transactional
        final TransactionAttribute definition = transactionAttributeSource.getTransactionAttribute(
                invocation.getMethod(), targetClass);

        if (definition == null) {
            throw new IllegalStateException(String.format(
                    "Transactional aop config error,not found TransactionDefinition for method[%s]."
                    , invocation.getMethod()));
        }

        DefType defType;
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // current thread already has active transaction.
            defType = decideDefTypeWithTransaction(invocation, definition);
        } else {
            defType = decideDefTypeWithoutTransaction(invocation, definition);
        }
        try {

            return invocation.proceed();

        } finally {
            doAfterProceed(invocation, defType);
        }
    }

    private void doAfterProceed(MethodInvocation invocation, DefType defType) {

        switch (defType) {
            case OUTER:
                // firstly, pop
                TransactionDefinitionHolder.pop();
                if (LOG.isDebugEnabled()) {
                    String previousName = TransactionDefinitionHolder.getName();
                    if (previousName == null) {
                        LOG.debug("transaction[{}] end,pop stack.", invocation.getMethod());
                    } else {
                        LOG.debug("transaction[{}] end,resume transaction[{}]", invocation.getMethod(), previousName);
                    }
                }
                break;
            case INNER:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("inner transaction end,{}", invocation.getMethod());
                }
                break;
            case NONE:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("non-transactional method end,{}", invocation.getMethod());
                }
                break;
            case NOT_SUPPORTED:
                //firstly, pop
                TransactionDefinitionHolder.pop();

                if (LOG.isDebugEnabled()) {
                    String previousName = TransactionDefinitionHolder.getName();
                    if (previousName == null) {
                        LOG.debug("NOT_SUPPORTED transaction[{}] end.", invocation.getMethod());
                    } else {
                        LOG.debug("NOT_SUPPORTED transaction[{}] end,resume transaction[{}]."
                                , invocation.getMethod(), previousName);
                    }
                }
                break;
            case ERROR:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction error :{}", invocation.getMethod());
                }
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown DefType[%s]", defType));
        }
    }


    private DefType decideDefTypeWithTransaction(MethodInvocation invocation, TransactionAttribute definition) {
        DefType defType;
        switch (definition.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_REQUIRED:
            case TransactionDefinition.PROPAGATION_SUPPORTS:
            case TransactionDefinition.PROPAGATION_MANDATORY:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("inner transaction: {}", invocation.getMethod());
                }
                defType = DefType.INNER;
                break;
            case TransactionDefinition.PROPAGATION_NESTED:
                defType = decideDefTypeForNestedWithTransaction(invocation, definition);
                break;
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW:

                if (LOG.isDebugEnabled()) {
                    String previousTxName = TransactionDefinitionHolder.getName();
                    if (previousTxName == null) {
                        LOG.debug("will start new transaction: {}", invocation.getMethod());
                    } else {
                        LOG.debug("suspend previous transaction[{}],will start new transaction: {}"
                                , previousTxName, invocation.getMethod());
                    }
                }
                // push new definition
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
                defType = DefType.OUTER;
                break;
            case TransactionDefinition.PROPAGATION_NOT_SUPPORTED:
                if (LOG.isDebugEnabled()) {
                    String previousTxName = TransactionDefinitionHolder.getName();
                    if (previousTxName == null) {
                        LOG.debug("Execute non-transactionally,{}", invocation.getMethod());
                    } else {
                        LOG.debug("Execute non-transactionally,{},transaction suspend:{}", invocation.getMethod()
                                , previousTxName);
                    }
                }
                // push new definition
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
                defType = DefType.NOT_SUPPORTED;
                break;
            case TransactionDefinition.PROPAGATION_NEVER:
                defType = DefType.ERROR;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} don't support transaction", TransactionDefinitionHolder.getName());
                }
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown transaction propagation [%s]", definition.getPropagationBehavior()));

        }
        return defType;
    }


    private static DefType decideDefTypeWithoutTransaction(MethodInvocation invocation
            , TransactionAttribute definition) {

        DefType defType;
        switch (definition.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_REQUIRED:
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW:
            case TransactionDefinition.PROPAGATION_NESTED:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction[{}] start.", invocation.getMethod());
                }
                // push new transaction definition
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
                defType = DefType.OUTER;
                break;
            case TransactionDefinition.PROPAGATION_NOT_SUPPORTED:
            case TransactionDefinition.PROPAGATION_SUPPORTS:
            case TransactionDefinition.PROPAGATION_NEVER:
                defType = DefType.NONE;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Execute non-transactionally: {}", invocation.getMethod());
                }
                break;
            case TransactionDefinition.PROPAGATION_MANDATORY:
                defType = DefType.ERROR;
                LOG.debug("MANDATORY transaction[{}] will throw exception.", TransactionDefinitionHolder.getName());
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown transaction propagation [%s]", definition.getPropagationBehavior()));

        }
        return defType;
    }

    private static DefType decideDefTypeForNestedWithTransaction(MethodInvocation invocation
            , TransactionAttribute definition) {
        String txManagerName = definition.getName();
        if (txManagerName == null) {
            throw new TransactionDefinitionHolder
                    .NoTransactionManagerNameException(invocation.getMethod().toString());
        }
        DefType defType;
        if (TransactionDefinitionHolder.useSavepointForNested(txManagerName)) {
            defType = DefType.INNER;
        } else {
            // push new transaction definition
            TransactionDefinitionHolder.push(definition, invocation.getMethod());
            defType = DefType.OUTER;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("DefType[{}] transaction: {}", defType, invocation.getMethod());
        }
        return defType;
    }


}
