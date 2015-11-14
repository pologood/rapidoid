package org.rapidoid.main;

/*
 * #%L
 * rapidoid-main
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.ConfigHelp;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.cache.guava.GuavaCachePlugin;
import org.rapidoid.plugins.templates.MustacheTemplatesPlugin;
import org.rapidoid.quick.Quick;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.util.UTILS;
import org.rapidoid.webapp.AppClasspathEntitiesPlugin;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Rapidoid {

	private static WebApp webApp;

	public static synchronized WebApp run(String[] args, Object... config) {
		initAndStart(null, args, config);
		return webApp;
	}

	public static synchronized WebApp run(WebApp app, String[] args, Object... config) {
		initAndStart(app, args, config);
		return app;
	}

	public static synchronized boolean isInitialized() {
		return webApp != null;
	}

	private static synchronized void initAndStart(WebApp app, String[] args, Object... config) {
		if (webApp != null) {
			return;
		}

		Log.info("Starting Rapidoid...", "version", UTILS.version());

		ConfigHelp.processHelp(args);

		// print internal state
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		Conf.init(args, config);

		Log.info("Working directory is: " + System.getProperty("user.dir"));

		inferAndSetRootPackage();

		if (app == null) {
			app = AppTool.createRootApp();
		}

		registerDefaultPlugins();

		Quick.run(app, args, config);

		System.out.println();
		StatusPrinter.print(lc);

		Log.info("Rapidoid is ready.");

		Rapidoid.webApp = app;
	}

	private static synchronized void registerDefaultPlugins() {
		Plugins.register(new MustacheTemplatesPlugin());
		Plugins.register(new AppClasspathEntitiesPlugin());
		Plugins.register(new GuavaCachePlugin());
	}

	private static void inferAndSetRootPackage() {
		Class<?> callerCls = UTILS.getCallingClassOf(Rapidoid.class);

		if (callerCls != null) {
			String rootPkg = callerCls.getPackage().getName();
			Log.info("Setting root application package: " + rootPkg);
			ClasspathUtil.setRootPackage(rootPkg);
		} else {
			Log.warn("Couldn't calculate the application root package!");
		}
	}

	public static synchronized void register(WebApp app) {
		WebAppGroup.main().register(app);
	}

	public static synchronized void unregister(WebApp app) {
		WebAppGroup.main().unregister(app);
	}

	static synchronized void notifyGuiInit() {
		initAndStart(null, new String[] { "managed=true" });
	}

}
