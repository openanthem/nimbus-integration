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
package com.antheminc.oss.nimbus.integration.sink;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import com.antheminc.oss.nimbus.FrameworkRuntimeException;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableBinding(Sink.class)
@EnableConfigurationProperties({ SinkProperties.class })
public class SinkConfiguration {

	@Autowired
	CommandExecutorGateway executorGateway;
	
	@Autowired
	SinkProperties sink;
	
	@StreamListener(Sink.INPUT)
    public <T> void recievePayload(List<T> data) {
		if(data == null)
			return;
		if(sink.isAuditable()) {
			audit(data);
		}
		data.forEach(m -> {
			try {
				ObjectMapper om = new ObjectMapper();
				String rawPayload = om.writeValueAsString(m);
				executeCommmand(sink.getCommandUrl(), rawPayload);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}	
		});
    }
    
	private <T> void audit(List<T> data) {
		
		try {
			String rawPayload="{\"payload\" : \"" + data.toString() + "\"}";
			executeCommmand(sink.getAuditUrl(), rawPayload);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void executeCommmand(String url, String rawPayload) {
		if(url == null) {
			throw new FrameworkRuntimeException("url cannot be blank");
		}
		Command command = CommandBuilder.withUri(url).getCommand();
		CommandMessage commandMessage = new CommandMessage(command,rawPayload);
		executorGateway.execute(commandMessage);
	}
}
