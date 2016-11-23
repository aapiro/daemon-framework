package de.taimos.daemon.log4j;

/*
 * #%L
 * Daemon Library Log4j extension
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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SyslogAppender;

import com.sumologic.log4j.SumoLogicAppender;

import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.ILoggingConfigurer;

public class Log4jLoggingConfigurer implements ILoggingConfigurer {
	
	private final Logger rlog = Logger.getRootLogger();
	
	private SyslogAppender syslog;
	private DailyRollingFileAppender darofi;
	private ConsoleAppender console;
	
	@Override
	public void initializeLogging() throws Exception {
		// Clear all existing appenders
		this.rlog.removeAllAppenders();
		
		this.rlog.setLevel(Level.INFO);
		
		// only use SYSLOG and DAROFI in production mode
		if (!DaemonStarter.isDevelopmentMode()) {
			this.darofi = new DailyRollingFileAppender();
			this.darofi.setName("DAROFI");
			this.darofi.setLayout(new PatternLayout("%d{HH:mm:ss,SSS} %-5p %c %x - %m%n"));
			this.darofi.setFile("log/" + DaemonStarter.getDaemonName() + ".log");
			this.darofi.setDatePattern("'.'yyyy-MM-dd");
			this.darofi.setAppend(true);
			this.darofi.setThreshold(Level.INFO);
			this.darofi.activateOptions();
			this.rlog.addAppender(this.darofi);
			
			this.syslog = new SyslogAppender();
			this.syslog.setName("SYSLOG");
			this.syslog.setLayout(new PatternLayout(DaemonStarter.getDaemonName() + ": %-5p %c %x - %m%n"));
			this.syslog.setSyslogHost("localhost");
			this.syslog.setFacility("LOCAL0");
			this.syslog.setFacilityPrinting(false);
			this.syslog.setThreshold(Level.INFO);
			this.syslog.activateOptions();
			this.rlog.addAppender(this.syslog);
		}
		if (DaemonStarter.isDevelopmentMode() || DaemonStarter.isRunMode()) {
			// CONSOLE is only active in development and run mode
			this.console = new ConsoleAppender();
			this.console.setName("CONSOLE");
			this.console.setLayout(new PatternLayout("%d{HH:mm:ss,SSS} %-5p %c %x - %m%n"));
			this.console.setTarget(ConsoleAppender.SYSTEM_OUT);
			this.console.activateOptions();
			this.rlog.addAppender(this.console);
		}
	}
	
	@Override
	public void reconfigureLogging() throws Exception {
		final Level logLevel = Level.toLevel(DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_LEVEL), Level.INFO);
		this.rlog.setLevel(logLevel);
		this.rlog.info(String.format("Changed the the log level to %s", logLevel));
		
		if (!DaemonStarter.isDevelopmentMode()) {
			final String fileEnabled = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_FILE, "true");
			final String syslogEnabled = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_SYSLOG, "true");
			final String logglyEnabled = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_LOGGLY, "false");
			final String logentriesEnabled = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_LOGENTRIES, "false");
			final String sumologicEnabled = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_SUMOLOGIC, "false");
			
			if ((fileEnabled != null) && fileEnabled.equals("false")) {
				this.rlog.removeAppender(this.darofi);
				this.darofi = null;
				this.rlog.info("Deactivated the FILE Appender");
			} else {
				this.darofi.setThreshold(logLevel);
				this.darofi.setLayout(this.getLayout());
				this.darofi.activateOptions();
			}
			
			if ((syslogEnabled != null) && syslogEnabled.equals("false")) {
				this.rlog.removeAppender(this.syslog);
				this.syslog = null;
				this.rlog.info("Deactivated the SYSLOG Appender");
			} else {
				final String host = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_HOST, "localhost");
				final String facility = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_FACILITY, "LOCAL0");
				final Level syslogLevel = Level.toLevel(DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_LEVEL), Level.INFO);
				
				this.syslog.setSyslogHost(host);
				this.syslog.setFacility(facility);
				this.syslog.setThreshold(syslogLevel);
				this.syslog.activateOptions();
				this.rlog.info(String.format("Changed the SYSLOG Appender to host %s and facility %s", host, facility));
			}
			
			if ((logglyEnabled != null) && logglyEnabled.equals("false")) {
				this.rlog.info("Deactivated the LOGGLY Appender");
			} else {
				final String token = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGLY_TOKEN);
				if ((token == null) || token.isEmpty()) {
					this.rlog.error("Missing loggly token but loggly is activated");
				} else {
					final String tags = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGLY_TAGS);
					LogglyAppender loggly = new LogglyAppender();
					loggly.setToken(token);
					loggly.setTags(tags);
					loggly.setLayout(this.getLayout());
					loggly.activateOptions();
					this.rlog.addAppender(loggly);
				}
			}
			
			if ((logentriesEnabled != null) && logentriesEnabled.equals("false")) {
				this.rlog.info("Deactivated the LOGENTRIES Appender");
			} else {
				final String token = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGENTRIES_TOKEN);
				if ((token == null) || token.isEmpty()) {
					this.rlog.error("Missing logentries token but logentries is activated");
				} else {
					LogentriesAppender logentries = new LogentriesAppender();
					logentries.setToken(token);
					logentries.setLayout(this.getLayout());
					logentries.activateOptions();
					this.rlog.addAppender(logentries);
				}
			}
			
			if ((sumologicEnabled != null) && sumologicEnabled.equals("false")) {
				this.rlog.info("Deactivated the SUMOLOGIC Appender");
			} else {
				final String url = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SUMOLOGIC_URL);
				if ((url == null) || url.isEmpty()) {
					this.rlog.error("Missing SumoLogic url but SumoLogic is activated");
				} else {
					SumoLogicAppender sumoLogic = new SumoLogicAppender();
					sumoLogic.setUrl(url);
					sumoLogic.setLayout(this.getLayout());
					sumoLogic.activateOptions();
					this.rlog.addAppender(sumoLogic);
				}
			}
		}
		if (DaemonStarter.isDevelopmentMode() || DaemonStarter.isRunMode()) {
			this.console.setLayout(this.getLayout());
			this.console.setThreshold(logLevel);
			this.console.activateOptions();
		}
	}
	
	private Layout getLayout() {
		final String logLayout = System.getProperty(Log4jDaemonProperties.LOGGER_LAYOUT, Log4jDaemonProperties.LOGGER_LAYOUT_PATTERN);
		
		switch (logLayout) {
		case Log4jDaemonProperties.LOGGER_LAYOUT_JSON:
			return new JSONLayout();
		case Log4jDaemonProperties.LOGGER_LAYOUT_PATTERN:
		default:
			final String logPattern = System.getProperty(Log4jDaemonProperties.LOGGER_PATTERN, "%d{HH:mm:ss,SSS} %-5p %c %x - %m%n");
			return new PatternLayout(logPattern);
		}
	}
	
	@Override
	public void simpleLogging() throws Exception {
		// Clear all existing appenders
		this.rlog.removeAllAppenders();
		final Level logLevel = Level.toLevel(System.getProperty(Log4jDaemonProperties.LOGGER_LEVEL), Level.INFO);
		this.rlog.setLevel(logLevel);
		
		this.console = new ConsoleAppender();
		this.console.setName("CONSOLE");
		this.console.setLayout(new PatternLayout("%d{HH:mm:ss,SSS} %-5p %c %x - %m%n"));
		this.console.setTarget(ConsoleAppender.SYSTEM_OUT);
		this.console.activateOptions();
		this.rlog.addAppender(this.console);
	}
	
	public static void setup() {
		System.setProperty(DaemonProperties.LOGGER_CONFIGURER, Log4jLoggingConfigurer.class.getCanonicalName());
	}
	
}
