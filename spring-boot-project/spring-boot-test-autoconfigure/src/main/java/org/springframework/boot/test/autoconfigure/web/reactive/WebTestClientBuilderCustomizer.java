/*
 * Copyright 2012-2017 the original author or authors.
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

package org.springframework.boot.test.autoconfigure.web.reactive;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

/**
 * A customizer for a {@link WebTestClient.Builder}. Any
 * {@code WebTestClientBuilderCustomizer} beans found in the application context will be
 * {@link #customize called} to customize the auto-configured {@link WebTestClient.Builder}.
 *
 * @author Andy Wilkinson
 * @author Roman Zaynetdinov
 * @since 2.0.0
 * @see WebTestClientAutoConfiguration
 */
@FunctionalInterface
public interface WebTestClientBuilderCustomizer {

	/**
	 * Customize the given {@code builder}.
	 * @param builder the builder
	 */
	void customize(WebTestClient.Builder builder);

}
