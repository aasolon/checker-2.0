package com.bftcom.devtournament.checker;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

@SpringBootApplication
public class CheckerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CheckerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CheckerApplication.class);
	}

	/**
	 * TCP connection to connect with SQL clients to the embedded h2 database.
	 *
	 * Connect to "jdbc:h2:tcp://localhost:9092/testdb", username "sa", password empty.
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	@ConditionalOnProperty("h2.tcp.enabled")
	public Server h2TcpServer(@Value("${h2.tcp.port:9092}") String h2TcpPort) throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2TcpPort);
	}

	/**
	 * Web console for the embedded h2 database.
	 *
	 * Go to http://localhost:8082 and connect to the database "jdbc:h2:tcp:testdb", username "sa", password empty.
	 */
	@Bean
	@ConditionalOnProperty("h2.web.enabled")
	public Server h2WebServer(@Value("${h2.web.port:8082}") String h2WebPort) throws SQLException {
		return Server.createWebServer("-web", "-webAllowOthers", "-webPort", h2WebPort).start();
	}
}
