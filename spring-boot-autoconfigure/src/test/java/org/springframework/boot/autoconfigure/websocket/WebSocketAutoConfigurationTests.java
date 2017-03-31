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

package org.springframework.boot.autoconfigure.websocket;

import java.net.URL;

import javax.websocket.server.ServerContainer;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link WebSocketAutoConfiguration}
 *
 * @author Andy Wilkinson
 */
public class WebSocketAutoConfigurationTests {

	private AnnotationConfigServletWebServerApplicationContext context;

	@Before
	public void createContext() {
		this.context = new AnnotationConfigServletWebServerApplicationContext();
	}

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@BeforeClass
	@AfterClass
	public static void uninstallUrlStreamHandlerFactory() {
		ReflectionTestUtils.setField(TomcatURLStreamHandlerFactory.class, "instance",
				null);
		ReflectionTestUtils.setField(URL.class, "factory", null);
	}

	@Test
	public void tomcatServerContainerIsAvailableFromTheServletContext() {
		serverContainerIsAvailableFromTheServletContext(TomcatConfiguration.class,
				WebSocketAutoConfiguration.TomcatWebSocketConfiguration.class);
	}

	@Test
	public void jettyServerContainerIsAvailableFromTheServletContext() {
		serverContainerIsAvailableFromTheServletContext(JettyConfiguration.class,
				WebSocketAutoConfiguration.JettyWebSocketConfiguration.class);
	}

	private void serverContainerIsAvailableFromTheServletContext(
			Class<?>... configuration) {
		this.context.register(configuration);
		this.context.refresh();
		Object serverContainer = this.context.getServletContext()
				.getAttribute("javax.websocket.server.ServerContainer");
		assertThat(serverContainer).isInstanceOf(ServerContainer.class);

	}

	static class CommonConfiguration {

		@Bean
		public WebServerFactoryCustomizerBeanPostProcessor ServletWebServerCustomizerBeanPostProcessor() {
			return new WebServerFactoryCustomizerBeanPostProcessor();
		}

	}

	@Configuration
	static class TomcatConfiguration extends CommonConfiguration {

		@Bean
		public ServletWebServerFactory webServerFactory() {
			TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
			factory.setPort(0);
			return factory;
		}

	}

	@Configuration
	static class JettyConfiguration extends CommonConfiguration {

		@Bean
		public ServletWebServerFactory webServerFactory() {
			JettyServletWebServerFactory JettyServletWebServerFactory = new JettyServletWebServerFactory();
			JettyServletWebServerFactory.setPort(0);
			return JettyServletWebServerFactory;
		}

	}

}
