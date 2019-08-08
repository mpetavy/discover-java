package org.mpetavy.discover;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@SpringBootApplication
public class DiscoverApplication {

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx,Discover discover,DiscoverConfiguration cfg) {
        return args -> {
            discover.FindPeers(cfg);
        };
    }
	public static void main(String[] args) {
		SpringApplication.run(DiscoverApplication.class, args);
	}
}
