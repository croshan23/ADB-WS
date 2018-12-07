package com.adb.ws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/*
 * we needed UserService reference in AuthenticationFilter class to add userID in the token.
 * We could've autowired it but as AuthenticationFilter is not a bean so it can't autowire other beans.
 * Hence, it needs Spring Application Context to instantiate that bean manually.
 * Hence, this class is created!!!
 */
public class SpringApplicationContext implements ApplicationContextAware {
	private static ApplicationContext CONTEXT;

    @Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CONTEXT = context;
	}

	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}
}
