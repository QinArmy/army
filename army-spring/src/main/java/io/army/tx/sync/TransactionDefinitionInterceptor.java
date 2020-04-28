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

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        if (targetClass == null) {
            throw new IllegalStateException(
                    String.format("Transactional aop config error,class[%s],method[%s]", targetClass, invocation.getMethod()));
        }
        // get target method with @Transactional
        final TransactionAttribute definition = transactionAttributeSource.getTransactionAttribute(
                invocation.getMethod(), targetClass);

        if (definition == null) {
            throw new IllegalStateException(String.format("Transactional aop config error,method[%s]", invocation.getMethod()));
        }

        DefType defType;
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            defType = decideDefTypeWithTx(invocation, definition);
        } else {
            defType = decideDefTypeWithoutTx(invocation, definition);
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
                if (LOG.isDebugEnabled()) {
                    String previousName = TransactionDefinitionHolder.getName();
                    if (previousName == null) {
                        LOG.debug("NOT_SUPPORTED transaction[{}] end.", invocation.getMethod());
                    } else {
                        LOG.debug("NOT_SUPPORTED transaction[{}] end,resume transaction[{}]."
                                , invocation.getMethod(), previousName);
                    }
                }
                //lastly, pop previous transaction
                TransactionDefinitionHolder.pop();
                break;
            case ERROR:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction :{}", invocation.getMethod());
                }
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown DefType[%s]", defType));
        }
    }


    private DefType decideDefTypeWithTx(MethodInvocation invocation, TransactionAttribute definition) {
        DefType defType;
        switch (definition.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_REQUIRED:
            case TransactionDefinition.PROPAGATION_SUPPORTS:
            case TransactionDefinition.PROPAGATION_MANDATORY:
            case TransactionDefinition.PROPAGATION_NESTED:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("inner transaction: {}", invocation.getMethod());
                }
                defType = DefType.INNER;
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
                // previous transaction push to stack
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
                                , TransactionDefinitionHolder.getName());
                    }
                }
                defType = DefType.NOT_SUPPORTED;
                //lastly,push previous transaction to stack
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
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

    /**
     * 当前没有事务的环境下决定事务定义类型
     */
    private DefType decideDefTypeWithoutTx(MethodInvocation invocation, TransactionAttribute definition) {
        DefType defType;
        switch (definition.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_REQUIRED:
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW:
            case TransactionDefinition.PROPAGATION_NESTED:
                // push new transaction definition
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
                defType = DefType.OUTER;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction[{}] start.", invocation.getMethod());
                }
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

}
