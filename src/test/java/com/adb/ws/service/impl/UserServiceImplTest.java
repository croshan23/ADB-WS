package com.adb.ws.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.adb.ws.io.entity.UserEntity;
import com.adb.ws.repository.UserRepository;
import com.adb.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks // Mock annotation that allows user service impl component to allow mock
					// autowired component for eg. userRepository
	UserServiceImpl userServiceImpl;

	@Mock
	UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {
		// For Mockito to instantiate this object
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testGetUser() {

		// Stub object, hard coded object created for testing
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Roshan");
		userEntity.setUserId("abcdef");
		userEntity.setEncryptedPassword("4849dmd44dldk");

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		/*
		 * In above code we created UserEntity stub obj to test in assert functions
		 * below We Mocked UserRepository so that when we use getUser service of
		 * UserServiceImpl, it won't search/access database for data, rather it gets
		 * mocked stub obj created above because of the when condition written above
		 */
		UserDto userDto = userServiceImpl.getUser("asAnyStringIsMentionedAbove");

		assertNotNull(userDto);
		assertEquals("Roshan", userDto.getFirstName());
	}

	@Test // If it was jUnit 4 > @Test(expected = UsernameNotFoundException.class)
	void testGetUser_UsernameNotFoundException() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows (UsernameNotFoundException.class, () -> {
			userServiceImpl.getUser("asAnyStringIsMentionedAbove");
		});
	}

}
