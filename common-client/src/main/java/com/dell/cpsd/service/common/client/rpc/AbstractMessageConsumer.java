/**
 * &copy; 2016 VCE Company, LLC. All rights reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.common.rabbitmq.consumer.UnhandledMessageConsumer;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.log.SCCLMessageCode;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * &copy; 2016 VCE Company, LLC. All rights reserved.
 * VCE Confidential/Proprietary Information
 * </p>
 *
 * @since SINCE-TBD
 */
public abstract class AbstractMessageConsumer extends UnhandledMessageConsumer implements DelegatingMessageConsumer
{
    private Map<Class, ServiceCallbackAdapter> adapters = new HashMap<>();

    @Override
    public <S, D> void addAdapter(ServiceCallbackAdapter<S, D> callback)
    {
        adapters.put(callback.getSourceClass(), callback);
    }

    void handleMessage(Object message)
    {
        if (message != null)
        {
            ServiceCallbackAdapter adapter = adapters.get(message.getClass());
            if (adapter != null)
            {
                handleResponse(message, adapter);
            }
        }
    }

    protected <M, R> void handleResponse(final M message,
            final ServiceCallbackAdapter<M, R> handler)
    {
        if (message == null)
        {
            return;
        }

        final IServiceCallback callback = handler.take(message);

        if (callback == null)
        {
            return;
        }

        final R response = handler.transform(message);

        try
        {
            handler.consume(callback, response);
        }
        catch (Exception exception)
        {
            // log the exception thrown by the compute callback
            Object[] lparams = {"handleResponseCallback", exception.getMessage()};
            getLogger().error(SCCLMessageCode.ERROR_CALLBACK_FAIL_E.getMessageCode(),
                    lparams, exception);
        }
    }

    protected abstract ILogger getLogger();
}
