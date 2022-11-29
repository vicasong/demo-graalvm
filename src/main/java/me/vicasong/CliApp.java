package me.vicasong;

import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/** 程序入口 */
@SpringBootApplication(scanBasePackages = {
		"me.vicasong",
})
public class CliApp implements ApplicationRunner, Ordered {

	@Autowired
	private ApplicationContext context;

	/** log recorder */
	private static final Logger log = LoggerFactory.getLogger(CliApp.class);


	@Override
	public void run(ApplicationArguments args) throws Exception {
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}


	@SuppressWarnings({"unused"})
	public static void main(String[] args) {
		System.setProperty("https.protocols", "TLSv1.3,TLSv1.2,TLSv1.1,SSLv3");
		PatternLayout.DEFAULT_CONVERTER_MAP
				.put("pid", ProcessIdConverter.class.getName());
		disableHttpClientDebug();


		String profile = "dev";
		SpringApplicationBuilder builder = new SpringApplicationBuilder()
				.bannerMode(Banner.Mode.OFF)
				.web(WebApplicationType.NONE)
				.properties("spring.profiles.active=%s".formatted(profile))
				.main(CliApp.class)
				.sources(CliApp.class);
		ApplicationContext context = builder
					.run(args);
	}

	/** disable apache-http debug log */
	private static void disableHttpClientDebug() {
		List<String> logs = Arrays.asList("org.apache.http", "groovyx.net.http");
		for (String name : logs) {
			ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(name);
			logger.setLevel(Level.INFO);
			logger.setAdditive(false);
		}
	}


	public static class ProcessIdConverter extends ClassicConverter {

		@Override
		public String convert(ILoggingEvent event) {
			return String.valueOf(CliState.PID);
		}
	}
}
