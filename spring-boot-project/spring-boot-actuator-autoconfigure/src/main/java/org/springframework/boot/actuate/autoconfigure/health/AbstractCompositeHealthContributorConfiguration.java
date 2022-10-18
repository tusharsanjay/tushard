/*
 * Copyright 2012-2022 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure.health;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/**
 * Base class for health contributor configurations that can combine source beans into a
 * composite.
 *
 * @param <C> the contributor type
 * @param <I> the health indicator type
 * @param <B> the bean type
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @since 2.2.0
 */
public abstract class AbstractCompositeHealthContributorConfiguration<C, I extends C, B> {

	private final Function<B, I> indicatorFactory;

	/**
	 * Creates a {@code AbstractCompositeHealthContributorConfiguration} that will use
	 * reflection to create health indicator instances.
	 * @deprecated since 3.0.0 in favor of
	 * {@link #AbstractCompositeHealthContributorConfiguration(Function)}
	 */
	@Deprecated(since = "3.0.0", forRemoval = true)
	protected AbstractCompositeHealthContributorConfiguration() {
		ResolvableType type = ResolvableType.forClass(AbstractCompositeHealthContributorConfiguration.class,
				getClass());
		Class<?> indicatorType = type.resolveGeneric(1);
		Class<?> beanType = type.resolveGeneric(2);
		this.indicatorFactory = (bean) -> {
			try {
				@SuppressWarnings("unchecked")
				Constructor<I> constructor = (Constructor<I>) indicatorType.getDeclaredConstructor(beanType);
				return BeanUtils.instantiateClass(constructor, bean);
			}
			catch (Exception ex) {
				throw new IllegalStateException(
						"Unable to create health indicator " + indicatorType + " for bean type " + beanType, ex);
			}
		};
	}

	/**
	 * Creates a {@code AbstractCompositeHealthContributorConfiguration} that will use the
	 * given {@code indicatorFactory} to create health indicator instances.
	 * @param indicatorFactory the function to create health indicators
	 * @since 3.0.0
	 */
	protected AbstractCompositeHealthContributorConfiguration(Function<B, I> indicatorFactory) {
		this.indicatorFactory = indicatorFactory;
	}

	protected final C createContributor(Map<String, B> beans) {
		Assert.notEmpty(beans, "Beans must not be empty");
		if (beans.size() == 1) {
			return createIndicator(beans.values().iterator().next());
		}
		return createComposite(beans);
	}

	protected abstract C createComposite(Map<String, B> beans);

	protected I createIndicator(B bean) {
		return this.indicatorFactory.apply(bean);
	}

}
