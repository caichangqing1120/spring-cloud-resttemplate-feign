package com.cht.rst.feign;

import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Target;

/**
 * @author Spencer Gibb
 */
class DefaultTargeter implements Targeter {

	@Override
	public <T> T target(ChtFeignClientFactoryBean factory, ChtFeign.Builder feignBuilder, ChtFeignContext context,
						Target.HardCodedTarget<T> target) {
		return feignBuilder.target(target);
	}
}
