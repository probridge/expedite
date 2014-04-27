package com.probridge.expedite.webapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpediteContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(ExpediteContextListener.class);
	private static ServletContext context = null;
	
	static {
		Thread.currentThread().setName("Expedite WebApp Bootstrap Thread");
	}
	
	public static ServletContext getContext() {
		return context;
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("Expedite Starting");
		context = arg0.getServletContext();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("Expedite Stoping");
	}
}
