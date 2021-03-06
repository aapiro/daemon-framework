/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.daemon.spring.annotations;

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import de.taimos.daemon.spring.Configuration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Controller
@Profile(Configuration.PROFILES_PRODUCTION)
public @interface ProdController {
	
	/**
	 * The value may indicate a suggestion for a logical component name, to be turned into a Spring bean in case of an autodetected
	 * component.
	 * 
	 * @return the suggested component name, if any
	 */
	String value() default "";
}
