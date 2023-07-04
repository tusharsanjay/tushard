/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.web.client;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link RestClient}.
 * <p>
 * This will produce a {@link RestClient.Builder RestClient.Builder} bean with the
 * {@code prototype} scope, meaning each injection point will receive a newly cloned
 * instance of the builder.
 *
 * @author Arjen Poutsma
 * @since 3.2.0
 */
@AutoConfiguration(after = HttpMessageConvertersAutoConfiguration.class)
@ConditionalOnClass(RestClient.class)
@Conditional(RestClientAutoConfiguration.NotReactiveWebApplicationCondition.class)
public class RestClientAutoConfiguration {

	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean
	public RestClient.Builder webClientBuilder(ObjectProvider<RestClientCustomizer> customizerProvider) {
		RestClient.Builder builder = RestClient.builder();
		customizerProvider.orderedStream().forEach((customizer) -> customizer.customize(builder));
		return builder;
	}

	static class NotReactiveWebApplicationCondition extends NoneNestedConditions {

		NotReactiveWebApplicationCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
		private static class ReactiveWebApplication {

		}

	}

}
