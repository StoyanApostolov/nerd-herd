package com.nerd.herd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan({"com.nerd.herd"})
@EnableScheduling
/**
 *  TODO add ascii art
 */
public class NerdHerdRun {
	public static void main(String[] args) {
		SpringApplication.run(NerdHerdRun.class, args);
	}
}
