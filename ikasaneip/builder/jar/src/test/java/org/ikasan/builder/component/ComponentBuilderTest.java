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
package org.ikasan.builder.component;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.filter.configuration.FilterConfiguration;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;
import javax.transaction.TransactionManager;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class ComponentBuilderTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock applicationContext
     */
    final ApplicationContext applicationContext = mockery.mock(ApplicationContext.class, "mockApplicationContext");
    final TransactionManager transactionManager = mockery.mock(TransactionManager.class, "mockTransactionManager");
    final JtaTransactionManager jtaTransactionManager = mockery.mock(JtaTransactionManager.class, "mockJtaTransactionManager");

    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");
    final DuplicateFilterService duplicateFilterService = mockery.mock(DuplicateFilterService.class, "mockDuplicateFilterService");
    final FilterEntryConverter filterEntryConverter = mockery.mock(FilterEntryConverter.class, "mockFilterEntryConverter");

    @Test
    public void test_successful_scheduledConsumer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(Scheduler.class);
                will(returnValue(scheduler));
                oneOf(applicationContext).getBean(ScheduledJobFactory.class);
                will(returnValue(scheduledJobFactory));

                oneOf(applicationContext).getBean(AopProxyProvider.class);
                will(returnValue(aopProxyProvider));
            }
        });

        componentBuilder.scheduledConsumer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_jmsConsumer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(TransactionManager.class);
                will(returnValue(transactionManager));
                oneOf(applicationContext).getBean(JtaTransactionManager.class);
                will(returnValue(jtaTransactionManager));

                oneOf(applicationContext).getBean(AopProxyProvider.class);
                will(returnValue(aopProxyProvider));
            }
        });

        componentBuilder.jmsConsumer();

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_jmsProducer() {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        // expectations
        mockery.checking(new Expectations()
        {
            {


            }
        });

        componentBuilder.jmsProducer();

        mockery.assertIsSatisfied();
    }


    /**
     * Test listSplitter.
     */
    @Test
    public void test_successful_listSplitter()
    {
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Splitter splitter = componentBuilder.listSplitter().build();
        assertTrue("instance should be a Splitter", splitter instanceof Splitter);
    }

    /**
     * Test messageFilterBuilder.
     */
    @Test
    public void test_successful_messageFilterBuilder_withConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(applicationContext).getBean(DuplicateFilterService.class);
                will(returnValue(duplicateFilterService));
            }
        });

        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Filter filter = componentBuilder.messageFilter().setFilterEntryConverter(filterEntryConverter)
                .setApplyFilter(true).setLogFilter(true).setConfiguredResourceId("configuredResourceId").build();
        assertTrue("instance should be a Filter", filter instanceof Filter);

        FilterConfiguration configuration = ((ConfiguredResource<FilterConfiguration>)filter).getConfiguration();
        assertTrue("configuredResourceId should be 'configuredResourceId'",  "configuredResourceId".equals(((ConfiguredResource<FilterConfiguration>) filter).getConfiguredResourceId()));
        assertTrue("applyFilter should be true",  configuration.isApplyFilter());
        assertTrue("logFiltered should be true",  configuration.isLogFiltered());
    }

}