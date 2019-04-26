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
package com.antheminc.oss.nimbus.integration.source.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import com.antheminc.oss.nimbus.FrameworkRuntimeException;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.integration.source.SourceProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * @author Sandeep Mantha
 *
 */
public class AuditInterceptor implements ChannelInterceptor{
	
	@Autowired
	CommandExecutorGateway executorGateway;
	
	@Autowired
	private SourceProperties source;
			
	@Override
    public Message<?> preSend(final Message<?> inMessage,
        final MessageChannel inChannel) {
		if(source.isAuditable()) {
			 auditMessage(inMessage);
		}
        return inMessage;
    }
	
	protected void auditMessage(final Message<?> inMessage) {
		if(source.getAuditUrl() == null) {
			throw new FrameworkRuntimeException("audit url cannot be blank");
		}
		Command command = CommandBuilder.withUri(source.getAuditUrl()).getCommand();
		try {
			ObjectMapper om = new ObjectMapper();
			String rawPayload = om.writeValueAsString(inMessage);
			CommandMessage commandMessage = new CommandMessage(command,rawPayload);
			executorGateway.execute(commandMessage);
			System.out.println(inMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

