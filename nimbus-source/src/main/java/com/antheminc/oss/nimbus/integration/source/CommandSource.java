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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.endpoint.AbstractMessageSource;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;

/**
 * @author Sandeep Mantha
 *
 */
public class CommandSource extends AbstractMessageSource<Object> {

	private final String commandUrl;
	private final CommandMessage commandMessage;
	
	@Autowired
	CommandExecutorGateway executorGateway;

	public CommandSource(String commandUrl) {
		Assert.notNull(commandUrl, "'commandUrl' must not be null");
		this.commandUrl = commandUrl;
		Command command = CommandBuilder.withUri(commandUrl).getCommand();
		CommandMessage commandMessage = new CommandMessage(command,null);	
		this.commandMessage = commandMessage; 
	}
	
	@Override
	public String getComponentType() {
		return "nimbus:inbound-channel-adapter";
	}
	
	@Override
	protected Object doReceive() {
		MultiOutput output = executorGateway.execute(commandMessage);
		List<Object> data = (List<Object>) output.getSingleResult();
		return MessageBuilder.withPayload(data).build();
	}
	

}
