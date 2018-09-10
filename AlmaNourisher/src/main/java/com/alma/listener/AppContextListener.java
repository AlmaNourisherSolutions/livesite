package com.alma.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@WebListener
public class AppContextListener implements ServletContextListener {
	private Logger log;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private Environment env;

	public AppContextListener() {
		this.log = Logger.getLogger(AppContextListener.class);
	}

	public void contextInitialized(ServletContextEvent sce) {
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}
}
