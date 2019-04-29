/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.endpoint.consumer;

import org.ikasan.component.endpoint.consumer.EventGeneratingConsumer;
import org.ikasan.component.endpoint.consumer.api.TechEndpoint;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

/**
 * Test class for EventGeneratingConsumer.
 * 
 * @author Ikasan Development Team
 */
public class EventGeneratingConsumerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private EventFactory flowEventFactory = mockery.mock(EventFactory.class);
    private EventListener eventListener = mockery.mock(EventListener.class);
    private TechEndpoint apiEventProvider = mockery.mock(TechEndpoint.class);
    private ExecutorService executorService = mockery.mock(ExecutorService.class);
    private FlowEvent flowEvent = mockery.mock(FlowEvent.class);

    /**
     * Test failed constructor
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_executorService()
    {
        new EventGeneratingConsumer(null, null);
    }

    /**
     * Test failed constructor
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_messageListener()
    {
        new EventGeneratingConsumer(executorService, null);
    }

    /**
     * Test eventGenerator with a limit of 1 event
     */
    @Test
    public void test_eventGenerator_message_generator_callback()
    {
        EventGeneratingConsumer eventGeneratingConsumer = new EventGeneratingConsumer(executorService, this.apiEventProvider);
        eventGeneratingConsumer.setEventFactory(flowEventFactory);
        eventGeneratingConsumer.setListener(eventListener);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // new flowEvent
                one(flowEventFactory).newEvent("message", "message");
                will(returnValue(flowEvent));

                // event limit of 1
                exactly(1).of(eventListener).invoke(flowEvent);
            }
        });

       // FIXME  APIEventProvider apiEventProvider = new APIEventProviderImpl();
       // FIXME  eventGeneratingConsumer.onMessage(apiEventProvider.getEvent());
       // mockery.assertIsSatisfied();
    }

    /**
     * Test eventGenerator start
     */
    @Test
    public void test_eventGenerator_start()
    {
        EventGeneratingConsumer eventGeneratingConsumer = new EventGeneratingConsumer(executorService, apiEventProvider);
        eventGeneratingConsumer.setEventFactory(flowEventFactory);
        eventGeneratingConsumer.setListener(eventListener);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // start
                one(executorService).submit(apiEventProvider);
            }
        });

        eventGeneratingConsumer.start();
        mockery.assertIsSatisfied();
    }
}