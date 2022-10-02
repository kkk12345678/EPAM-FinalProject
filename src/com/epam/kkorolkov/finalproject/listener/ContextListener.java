package com.epam.kkorolkov.finalproject.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String path = sce.getServletContext().getRealPath("/logs");
        System.setProperty("logFolder", path);
        final Logger LOGGER = LogManager.getLogger("INIT");
        LOGGER.info("Log folder is " + path);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}
}
