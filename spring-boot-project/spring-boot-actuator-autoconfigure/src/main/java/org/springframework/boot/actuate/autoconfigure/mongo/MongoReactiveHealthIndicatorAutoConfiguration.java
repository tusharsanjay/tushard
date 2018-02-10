/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.mongo;

import java.util.Map;

import org.springframework.boot.actuate.autoconfigure.health.CompositeReactiveHealthIndicatorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.mongo.MongoReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;



/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link MongoReactiveHealthIndicator}.
 *
 * @author Yulin Qin
 * @since 2.0.0
 */
@Configuration
@ConditionalOnClass(ReactiveMongoTemplate.class)
@ConditionalOnBean(ReactiveMongoTemplate.class)
@ConditionalOnEnabledHealthIndicator("mongo")
@AutoConfigureBefore(HealthIndicatorAutoConfiguration.class)
@AutoConfigureAfter({MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
public class MongoReactiveHealthIndicatorAutoConfiguration extends
		CompositeReactiveHealthIndicatorConfiguration<MongoReactiveHealthIndicator, ReactiveMongoTemplate> {

	private final Map<String, ReactiveMongoTemplate> reactiveMongoTemplate;

	public MongoReactiveHealthIndicatorAutoConfiguration(
			Map<String, ReactiveMongoTemplate> reactiveMongoTemplate) {
		this.reactiveMongoTemplate = reactiveMongoTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(name = "mongoReactiveHealthIndicator")
	public ReactiveHealthIndicator mongoReactiveHealthIndicator() {
		return createHealthIndicator(this.reactiveMongoTemplate);
	}

}
