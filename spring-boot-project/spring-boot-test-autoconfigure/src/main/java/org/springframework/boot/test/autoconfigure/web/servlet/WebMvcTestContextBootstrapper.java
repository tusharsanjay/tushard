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

package org.springframework.boot.test.autoconfigure.web.servlet;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContextBootstrapper;
import org.springframework.test.context.web.WebMergedContextConfiguration;

/**
 * {@link TestContextBootstrapper} for {@link WebMvcTest @WebMvcTest} support.
 *
 * @author Phillip Webb
 * @author Artsiom Yudovin
 */
public class WebMvcTestContextBootstrapper extends SpringBootTestContextBootstrapper {

	@Override
	protected MergedContextConfiguration processMergedContextConfiguration(
			MergedContextConfiguration mergedConfig) {
		return new WebMergedContextConfiguration(
				super.processMergedContextConfiguration(mergedConfig), "");
	}

	@Override
	protected String[] getProperties(Class<?> testClass) {
		WebMvcTest annotation = getWebMvcTestAnnotation(testClass);
		return (annotation != null) ? annotation.properties() : null;
	}

	private WebMvcTest getWebMvcTestAnnotation(Class<?> testClass) {
		return AnnotatedElementUtils.getMergedAnnotation(testClass, WebMvcTest.class);
	}

}
