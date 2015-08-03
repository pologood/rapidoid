package org.rapidoid.aop;

/*
 * #%L
 * rapidoid-aop
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.lang.reflect.Method;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AOPTest extends TestCommons {

	@SuppressWarnings("unchecked")
	@Test
	public void testAOP() {
		Log.debugging();
		AOP.reset();

		Method m = Cls.getMethod(MyService.class, "hey");
		MyService service = new MyService();

		eq(service.hey(), "hey");
		eq(AOP.invoke(null, m, service), "hey");

		AOP.reset();
		AOP.intercept(new Wrap("ab"), A.class, B.class);
		AOP.intercept(new Wrap("c"), C.class);

		eq(service.hey(), "hey");
		eq(AOP.invoke(null, m, service), "ab:c:hey:c:ab");

		AOP.reset();
		AOP.intercept(new Wrap("ab"), A.class);

		eq(service.hey(), "hey");
		eq(AOP.invoke(null, m, service), "ab:hey:ab");

		AOP.reset();
		AOP.intercept(new Wrap("c"), C.class);

		eq(service.hey(), "hey");
		eq(AOP.invoke(null, m, service), "c:hey:c");

		AOP.reset();
		AOP.intercept(new Wrap("c"), C.class);
		AOP.intercept(new Wrap("ab"), A.class, B.class);

		eq(service.hey(), "hey");
		eq(AOP.invoke(null, m, service), "c:ab:hey:ab:c");
	}

}
