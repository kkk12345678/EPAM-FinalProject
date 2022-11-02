package com.epam.kkorolkov.finalproject.util;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserUtils {
    private static final Logger LOGGER = LogManager.getLogger("UTILS");

    /** Logger messages */
    private static final String MESSAGE_EMAIL_INVALID = "Email is invalid.";

    /** Hashing parameters */
    private static final String ALGORITHM = "SHA3-256";
    private static final String SALT = "someSecretString";
    private static final String FORMAT = "%02x";

    /** Regular expressions */
    private static final String REGEX_EMAIL =
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static String hash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        byte[] bytes = md.digest((SALT + password).getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format(FORMAT, b));
        }
        return sb.toString();
    }

    public static Optional<User> validate(String email, String password) throws DBException, DaoException, NoSuchAlgorithmException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            Optional<User> optional = userDao.get(connection, email);
            if (optional.isPresent() && !optional.get().getPassword().equals(hash(password))) {
                return Optional.empty();
            }
            return optional;
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        if (!Pattern.matches(REGEX_EMAIL, email)) {
            LOGGER.info(MESSAGE_EMAIL_INVALID + ": " + email);
            throw new ValidationException(MESSAGE_EMAIL_INVALID);
        }
    }
}
