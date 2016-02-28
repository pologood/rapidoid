package org.rapidoid.http.handler.param;

/*
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.http.Req;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class BeanParamRetriever implements ParamRetriever {

	private final Class<?> type;
	private final String name;

	public BeanParamRetriever(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public Object getParamValue(Req req) {
		Object instance = Cls.newBeanInstance(type);
		Beany.update(instance, req.data());
		return instance;
	}

}