/**
 *
 * dry-redis: In-memory pure java implementation to Redis
 * Copyright (c) 2016, Sandeep Gupta
 * 
 * http://sangupta.com/projects/dry-redis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.dryredis.support;

import org.junit.Assert;
import org.junit.Test;

public class TestDryRedisCacheType {
	
	@Test
	public void testDryRedisCacheType() {
		Assert.assertEquals(1, DryRedisCacheType.LIST.getCode());
		Assert.assertEquals(2, DryRedisCacheType.SET.getCode());
		Assert.assertEquals(3, DryRedisCacheType.SORTED_SET.getCode());
		Assert.assertEquals(4, DryRedisCacheType.STRING.getCode());
		Assert.assertEquals(5, DryRedisCacheType.HASH.getCode());
		Assert.assertEquals(6, DryRedisCacheType.GEO.getCode());
		Assert.assertEquals(7, DryRedisCacheType.HYPER_LOG_LOG.getCode());
	}

}
