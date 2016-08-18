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

import de.taimos.daemon.spring.SpringDaemonAdapter;

public class TestAdapter extends SpringDaemonAdapter {
	
	private String res;
	
	
	/**
	 * @param res
	 */
	public TestAdapter(String res) {
		this.res = res;
	}
	
	@Override
	protected String getSpringResource() {
		return "spring/" + this.res + ".xml";
	}
	
}
