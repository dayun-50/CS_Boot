package com.kedu.project.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringProvider implements ApplicationContextAware{
	 public static ApplicationContext ctx;
	   
	 @Override
	   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		 SpringProvider.ctx = applicationContext;
	   }
}
