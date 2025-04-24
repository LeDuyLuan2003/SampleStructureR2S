package com.r2s.ApiWebReview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class ApiWebReviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiWebReviewApplication.class, args);
	}

}
