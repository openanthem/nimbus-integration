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

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sandeep Mantha
 *
 */
@ConfigurationProperties
@Getter @Setter
public class SinkProperties {
	/**
     * Provide the  url in command dsl format that needs to be executed when triggered
     */
	private String commandUrl;
	
	/**
     * Set to true if messages are to be audited
     */
	private boolean auditable = false;
	
	/**
     * Provide the  url in command dsl format that gets executed if the auditable = true and saved the messages that are downstream
     */
	private String auditUrl;
}
