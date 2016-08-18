package de.taimos.daemon.properties;

/*
 * #%L
 * Daemon Library
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

import org.apache.http.HttpResponse;

import de.taimos.httputils.WS;

public class SimpleHTTPPropertyProvider extends HTTPPropertyProvider {
	
	private String url;
	
	
	public SimpleHTTPPropertyProvider(String url) {
		this.url = url;
	}
	
	@Override
	protected String getDescription() {
		return this.url;
	}
	
	@Override
	protected HttpResponse getResponse() {
		return WS.url(this.url).get();
	}
	
}
