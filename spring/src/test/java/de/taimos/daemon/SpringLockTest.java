package de.taimos.daemon;

/*
 * #%L
 * Daemon Library Spring extension
 * %%
 * Copyright (C) 2012 - 2016 Taimos GmbH
 * %%
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
 * #L%
 */

import org.junit.Assert;
import org.junit.Test;

public class SpringLockTest {
	
	@Test
	public void startContext() throws Exception {
		TestAdapter adapter = new TestAdapter("good");
		adapter.doStart();
	}
	
	@Test
	public void testCtx() throws Exception {
		TestAdapter adapter = new TestAdapter("good");
		adapter.doStart();
		Assert.assertNotNull(adapter.getContext());
		Assert.assertNotNull(adapter.getContext().getId());
		adapter.doStop();
	}
	
	@Test
	public void testStopBad() {
		TestAdapter adapter = new TestAdapter("bad");
		try {
			adapter.doStart();
			Assert.fail();
		} catch (Exception e) {
			// should happen
		}
		Assert.assertNull(adapter.getContext());
		try {
			adapter.doStop();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(RuntimeException.class, e.getClass());
		}
	}
	
	@Test
	public void testDoubleStart() throws Exception {
		TestAdapter adapter = new TestAdapter("good");
		adapter.doStart();
		Assert.assertNotNull(adapter.getContext());
		try {
			adapter.doStart();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(RuntimeException.class, e.getClass());
		}
		adapter.doStop();
	}
	
	@Test
	public void testDoubleStop() throws Exception {
		TestAdapter adapter = new TestAdapter("good");
		adapter.doStart();
		Assert.assertNotNull(adapter.getContext());
		adapter.doStop();
		try {
			adapter.doStop();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(RuntimeException.class, e.getClass());
		}
	}
}
