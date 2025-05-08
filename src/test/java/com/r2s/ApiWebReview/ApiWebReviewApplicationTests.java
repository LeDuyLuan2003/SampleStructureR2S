package com.r2s.ApiWebReview;

import com.r2s.ApiWebReview.config.AuthControllerTestConfig;
//import com.r2s.ApiWebReview.config.TestMailConfig;
//import com.r2s.ApiWebReview.config.TestRoleSeeder;
import com.r2s.ApiWebReview.config.TestMailConfig;
import com.r2s.ApiWebReview.config.TestRoleSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestMailConfig.class, TestRoleSeeder.class})
class ApiWebReviewApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("✅ Nếu dòng này hiện thì ApplicationContext đã load được");
	}

}
