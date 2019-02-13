package com.adb.ws.shared.dto.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// As Utils class's hasTokenExpired method uses value from SPring COntext
// This test class is added with below annotations so that Sp Context is 
// run when this test is ran. It's a integration test (spring context ni test vai ra cha)
@ExtendWith(SpringExtension.class) //Load App Spring Context
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {

		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		
		assertNotNull(userId);
		assertNotNull(userId2);
		assertTrue(userId.length() == 30);
		assertTrue(userId2.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId2));
	}

	@Test
	//@Disabled //@Ignore if Junit4
	void testHasTokenNotExpired() {

		String token = utils.generateEmailVerificationToken("3j49fr4mdl");
		assertNotNull(token);
		
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}
	
	@Test
	final void testHasTokenExpired() {
		
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmRURnRnJFdmI4d1paQWc1d0pzY2hoTThhVE5GaGEiLCJleHAiOjE1NDgzODUzODJ9._KqB_E3pp6r7HrlAlmW-dc3fh1MMQkyf98_xniGu3ZzGD39egm_dxjX_GBVo4BdOyMJ042OQnlN9WNuhnld9ng";
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
		assertTrue(hasTokenExpired);
	}

}
