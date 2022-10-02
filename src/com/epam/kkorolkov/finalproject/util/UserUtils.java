package com.epam.kkorolkov.finalproject.util;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Optional;
import java.util.Properties;

public class UserUtils {
    private static final Logger LOGGER = LogManager.getLogger("UTILS");
    private static final String UTILS_PROPERTIES_FILE = "utils.properties";
    private static String salt;

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream utilsStream = classLoader.getResourceAsStream(UTILS_PROPERTIES_FILE)) {
            Properties utilsProperties = new Properties();
            utilsProperties.load(utilsStream);
            salt = utilsProperties.getProperty("salt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA3-256");
            byte[] bytes = md.digest((salt + password).getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public static Optional<User> validate(String email, String password) throws DBException {
        DataSource dataSource = null;
        Connection connection = null;
        Optional<User> optional = Optional.empty();
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            if (connection != null) {
                optional = userDao.get(connection, email);
                if (optional.isPresent() && !optional.get().getPassword().equals(hash(password))) {
                    LOGGER.info("Credentials are incorrect.");
                    return Optional.empty();
                }
            }
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        return optional;
    }
}
