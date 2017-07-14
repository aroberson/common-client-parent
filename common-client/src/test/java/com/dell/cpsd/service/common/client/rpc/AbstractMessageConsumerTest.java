package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.common.logging.LoggingManager;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.callback.ServiceTimeout;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO: Document usage.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class AbstractMessageConsumerTest
{
    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void handleMessage() throws Exception
    {

        String message = "my message";
        MockServiceCallbackAdapter adapter = new MockServiceCallbackAdapter();

        AbstractMessageConsumer messageConsumer = new AbstractMessageConsumerToTest();
        messageConsumer.addAdapter(adapter);
        messageConsumer.handleMessage(message);

        assertTrue(adapter.isTransformInvoked());
        assertTrue(adapter.isConsumeInvoked());
    }

    class AbstractMessageConsumerToTest extends AbstractMessageConsumer
    {
        private ILogger logger = new LoggingManager().getLogger(AbstractMessageConsumerToTest.class);

        @Override
        protected ILogger getLogger()
        {
            return logger;
        }
    }

    class MockServiceCallbackAdapter implements ServiceCallbackAdapter
    {

        boolean transformInvoked;
        boolean consumeInvoked;

        @Override
        public Object transform(final Object source)
        {
            transformInvoked = true;
            return source;
        }

        @Override
        public void consume(final IServiceCallback callback, final Object destination)
        {
            consumeInvoked = true;
        }

        @Override
        public IServiceCallback take(final Object source)
        {
            return new MockServiceCallback();
        }

        @Override
        public Class getSourceClass()
        {
            return String.class;
        }

        boolean isTransformInvoked()
        {
            return transformInvoked;
        }

        boolean isConsumeInvoked()
        {
            return consumeInvoked;
        }
    }

    class MockServiceCallback implements IServiceCallback
    {

        @Override
        public void handleServiceError(final ServiceError error)
        {

        }

        @Override
        public void handleServiceTimeout(final ServiceTimeout timeout)
        {

        }

        @Override
        public void handleServiceResponse(final ServiceResponse serviceResponse)
        {

        }
    }

}