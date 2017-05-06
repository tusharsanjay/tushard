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

package org.springframework.boot.context.properties.source;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link PropertySourceConfigurationPropertySource}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class PropertySourceConfigurationPropertySourceTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createWhenPropertySourceIsNullShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("PropertySource must not be null");
		new PropertySourceConfigurationPropertySource(null, mock(PropertyMapper.class),
				null);
	}

	@Test
	public void createWhenMapperIsNullShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Mapper must not be null");
		new PropertySourceConfigurationPropertySource(mock(PropertySource.class), null,
				null);
	}

	@Test
	public void getValueShouldUseDirectMapping() throws Exception {
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("key1", "value1");
		source.put("key2", "value2");
		source.put("key3", "value3");
		PropertySource<?> propertySource = new MapPropertySource("test", source);
		TestPropertyMapper mapper = new TestPropertyMapper();
		ConfigurationPropertyName name = ConfigurationPropertyName.of("my.key");
		mapper.addFromConfigurationProperty(name, "key2");
		PropertySourceConfigurationPropertySource adapter = new PropertySourceConfigurationPropertySource(
				propertySource, mapper, null);
		assertThat(adapter.getConfigurationProperty(name).getValue()).isEqualTo("value2");
	}

	@Test
	public void getValueShouldUseExtractor() throws Exception {
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("key", "value");
		PropertySource<?> propertySource = new MapPropertySource("test", source);
		TestPropertyMapper mapper = new TestPropertyMapper();
		ConfigurationPropertyName name = ConfigurationPropertyName.of("my.key");
		mapper.addFromConfigurationProperty(name, "key",
				(value) -> value.toString().replace("ue", "let"));
		PropertySourceConfigurationPropertySource adapter = new PropertySourceConfigurationPropertySource(
				propertySource, mapper, null);
		assertThat(adapter.getConfigurationProperty(name).getValue()).isEqualTo("vallet");
	}

	@Test
	public void getValueOrigin() throws Exception {
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("key", "value");
		PropertySource<?> propertySource = new MapPropertySource("test", source);
		TestPropertyMapper mapper = new TestPropertyMapper();
		ConfigurationPropertyName name = ConfigurationPropertyName.of("my.key");
		mapper.addFromConfigurationProperty(name, "key");
		PropertySourceConfigurationPropertySource adapter = new PropertySourceConfigurationPropertySource(
				propertySource, mapper, null);
		assertThat(adapter.getConfigurationProperty(name).getOrigin().toString())
				.isEqualTo("\"key\" from property source \"test\"");
	}

	@Test
	public void getValueWhenOriginCapableShouldIncludeSourceOrigin() throws Exception {
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("key", "value");
		PropertySource<?> propertySource = new OriginCapablePropertySource<>(
				new MapPropertySource("test", source));
		TestPropertyMapper mapper = new TestPropertyMapper();
		ConfigurationPropertyName name = ConfigurationPropertyName.of("my.key");
		mapper.addFromConfigurationProperty(name, "key");
		PropertySourceConfigurationPropertySource adapter = new PropertySourceConfigurationPropertySource(
				propertySource, mapper, null);
		assertThat(adapter.getConfigurationProperty(name).getOrigin().toString())
				.isEqualTo("TestOrigin key");
	}

	@Test
	public void containsDescendantOfShouldReturnEmpty() throws Exception {
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("foo.bar", "value");
		PropertySource<?> propertySource = new MapPropertySource("test", source);
		PropertySourceConfigurationPropertySource adapter = new PropertySourceConfigurationPropertySource(
				propertySource, new DefaultPropertyMapper(), null);
		assertThat(adapter.containsDescendantOf(ConfigurationPropertyName.of("foo")))
				.isEmpty();
	}

	/**
	 * Test {@link PropertySource} that's also a {@link OriginLookup}.
	 */
	private static class OriginCapablePropertySource<T> extends PropertySource<T>
			implements OriginLookup<String> {

		private final PropertySource<T> propertySource;

		OriginCapablePropertySource(PropertySource<T> propertySource) {
			super(propertySource.getName(), propertySource.getSource());
			this.propertySource = propertySource;
		}

		@Override
		public Object getProperty(String name) {
			return this.propertySource.getProperty(name);
		}

		@Override
		public Origin getOrigin(String name) {
			return new Origin() {

				@Override
				public String toString() {
					return "TestOrigin " + name;
				}

			};
		}

	}

}
