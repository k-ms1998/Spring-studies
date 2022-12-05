package com.fc.notice_board.notice_board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NoticeBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoticeBoardApplication.class, args);
	}

}
