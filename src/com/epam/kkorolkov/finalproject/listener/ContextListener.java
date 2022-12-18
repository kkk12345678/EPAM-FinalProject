package com.epam.kkorolkov.finalproject.listener;

import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Class {@code ContextListener} implements {@code EventListener} and
 * sets initial system parameter - folder where logs are stored.
 */
@WebListener
public class ContextListener implements ServletContextListener {
    private static final String LOGGER_MESSAGE = "Log folder is ";
    private static final String LOGGER_FOLDER = "/logs";
    private static final String PARAM_FOLDER = "logFolder";
    private static final String LOGGER_NAME = "INIT";

    /**
     * {@code contextInitialized} gets real context path from {@link ServletContextEvent}
     * and sets system property used to get logs folder.
     *
     * @param sce information about the {@link ServletContext} that was initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String path = sce.getServletContext().getRealPath(LOGGER_FOLDER);
        System.setProperty(PARAM_FOLDER, path);
        LogManager.getLogger(LOGGER_NAME).info(LOGGER_MESSAGE + path);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}
}
