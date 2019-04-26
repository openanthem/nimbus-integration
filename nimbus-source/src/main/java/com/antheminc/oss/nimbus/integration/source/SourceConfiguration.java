/**
 *  Copyright 2016-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.antheminc.oss.nimbus.integration.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.trigger.TriggerConfiguration;
import org.springframework.cloud.stream.app.trigger.TriggerPropertiesMaxMessagesDefaultUnlimited;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import com.antheminc.oss.nimbus.integration.source.interceptor.AuditInterceptor;

/**
 * @author Sandeep Mantha
 *
 */
@EnableBinding(Source.class)
@EnableConfigurationProperties({ SourceProperties.class, TriggerPropertiesMaxMessagesDefaultUnlimited.class })
@Import({ TriggerConfiguration.class })
public class SourceConfiguration {

	@Autowired
	private SourceProperties source;

	@Autowired
	@Qualifier("defaultPoller")
	private PollerMetadata poller;

	@Autowired
	@Qualifier(Source.OUTPUT)
	private MessageChannel output;

	@Bean
	protected MessageSource<Object> cmdMessageSource() {
		CommandSource cs = new CommandSource(source.getCommandUrl());
		return cs;
	}
	
	@Bean
	@GlobalChannelInterceptor(patterns = Source.OUTPUT)
	public ChannelInterceptor globalChannelInterceptor() {
		return new AuditInterceptor();
	}

	@Bean
	public IntegrationFlow startFlow() throws Exception {
		IntegrationFlowBuilder flow = IntegrationFlows.from(cmdMessageSource(),
				c -> c.poller(Pollers.trigger(poller.getTrigger())));
		
		flow.channel(output);
		return flow.get();
	}

}
