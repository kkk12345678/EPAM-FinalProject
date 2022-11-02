package com.epam.kkorolkov.finalproject.util;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.*;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CatalogueUtils {
    protected static final Logger LOGGER = LogManager.getLogger("UTILS");

    private static final String REGEX_TAG = "[^a-zA-Z0-9]+";
    private static final String REPLACE_TAG = "-";

    /* Keys of request parameters */
    private static final String PARAM_ID = "id";
    private static final String PARAM_ISBN = "isbn";
    private static final String PARAM_TAG = "tag";
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_PUBLISHER = "publisher";
    private static final String PARAM_DATE = "date";
    private static final String PARAM_QUANTITY = "quantity";
    private static final String PARAM_PRICE = "price";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_DESCRIPTION = "description";

    /* Logger messages */
    private static final String MESSAGE_ERROR_ID = "ID id not an integer.";
    private static final String MESSAGE_ERROR_ISBN = "Incorrect ISBN.";
    private static final String MESSAGE_ERROR_TAG = "Entity tag must not be empty.";
    private static final String MESSAGE_ERROR_UNIQUE = "Entity tag must be unique.";
    private static final String MESSAGE_ERROR_NAME = "Entity name must not be empty.";
    private static final String MESSAGE_ERROR_TAG_FORMATTED = "Entity with tag %s already exists.";


    public static void validate(Book book) throws DBException, DaoException, ValidationException {
        validate((CatalogueEntity) book);
        if (!validateIsbn(book.getIsbn())) {
            LOGGER.info(MESSAGE_ERROR_ISBN);
            throw new ValidationException(MESSAGE_ERROR_ISBN);
        }
    }

    public static void validate(CatalogueEntity entity) throws DBException, DaoException, ValidationException {
        if (entity.getTag() == null || entity.getTag().isEmpty()) {
            LOGGER.info(MESSAGE_ERROR_TAG);
            throw new ValidationException(MESSAGE_ERROR_TAG);
        }
        for (int languageId : entity.getNames().keySet()) {
            String name = entity.getNames().get(languageId);
            if (name == null || name.isEmpty()) {
                LOGGER.info(MESSAGE_ERROR_NAME);
                throw new ValidationException(MESSAGE_ERROR_NAME);
            }
        }
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Optional optional = Optional.empty();
            if (entity instanceof Category) {
                CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
                optional = categoryDao.get(connection, entity.getTag());
            }
            if (entity instanceof Publisher) {
                PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
                optional = publisherDao.get(connection, entity.getTag());
            }
            if (entity instanceof Book) {
                BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
                optional = bookDao.get(connection, entity.getTag());
            }
            if (optional.isPresent()) {
                CatalogueEntity dbEntity = (CatalogueEntity) optional.get();
                if (!dbEntity.getTag().equals(entity.getTag())) {
                    LOGGER.info(String.format(MESSAGE_ERROR_TAG_FORMATTED, entity.getTag()));
                    throw new ValidationException(MESSAGE_ERROR_UNIQUE);
                }
            }
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    public static boolean validateIsbn(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }
        int s = 0;
        for (int i = 0; i < 13; i++) {
            char ch = isbn.charAt(i);
            if (ch > '9' || ch < '0') {
                return false;
            }
            int digit = ch - '0';
            if (i % 2 != 0) {
                digit *= 3;
            }
            s += digit;
        }
        return s % 10 == 0;
    }

    public static Map<String, String> setBookParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        String categoryId = request.getParameter(PARAM_CATEGORY);
        String publisherId = request.getParameter(PARAM_PUBLISHER);
        String isbn = request.getParameter(PARAM_ISBN);
        String tag = request.getParameter(PARAM_TAG);
        String sortBy = request.getParameter("sort_by");
        String sortType = request.getParameter("sort_type");
        String pricesMin = request.getParameter("price_min");
        String pricesMax = request.getParameter("price_max");
        if (categoryId != null && !"".equals(categoryId)) {
            parameters.put(PARAM_CATEGORY, categoryId);
        }
        if (publisherId != null && !"".equals(publisherId)) {
            parameters.put(PARAM_PUBLISHER, publisherId);
        }
        if (isbn != null && !"".equals(isbn)) {
            parameters.put(PARAM_ISBN, isbn);
        }
        if (tag != null && !"".equals(tag)) {
            parameters.put(PARAM_TAG, tag);
        }
        if (sortBy != null) {
            parameters.put("sort_by", sortBy);
        }
        if (sortType != null) {
            parameters.put("sort_type", sortType);
        }
        if (pricesMin != null && !"".equals(pricesMin) && pricesMax != null && !"".equals(pricesMax)) {
            parameters.put("price_min", pricesMin);
            parameters.put("price_max", pricesMax);
        }
        return parameters;
    }

    public static Map<String, String> setOrderParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        String userName = request.getParameter("user");
        String sum = request.getParameter("sum");
        String date = request.getParameter(PARAM_DATE);
        String status = request.getParameter("status");
        if (userName != null && !"".equals(userName)) {
            parameters.put("user", userName);
        }
        if (sum != null && !"".equals(sum)) {
            parameters.put("sum", sum);
        }
        if (date != null && !"".equals(date)) {
            parameters.put(PARAM_DATE, date);
        }
        if (status != null && !"".equals(status)) {
            parameters.put("status", status);
        }
        return parameters;
    }

    public static void setDetailsFromRequest(HttpServletRequest request, CatalogueEntity catalogueEntity, Map<Integer, Language> languages) throws BadRequestException {
        Map<Integer, String> names = new HashMap<>();
        Map<Integer, String> descriptions = new HashMap<>();
        String tag = request.getParameter(PARAM_TAG);
        try {
            int id = Integer.parseInt(request.getParameter(PARAM_ID));
            for (int languageId : languages.keySet()) {
                descriptions.put(languageId, request.getParameter(PARAM_DESCRIPTION + languageId));
                names.put(languageId, request.getParameter(PARAM_NAME + languageId));
            }
            catalogueEntity.setId(id);
            catalogueEntity.setTag(tag.toLowerCase().replaceAll(REGEX_TAG, REPLACE_TAG));
            catalogueEntity.setNames(names);
            catalogueEntity.setDescriptions(descriptions);
        } catch (NumberFormatException e) {
            LOGGER.info(MESSAGE_ERROR_ID);
            LOGGER.error(e.getMessage());
            throw new BadRequestException();
        }

    }

    public static void setDetailsFromEntity(PreparedStatement preparedStatement, CatalogueEntity catalogueEntity) throws SQLException {
        for (int languageId : catalogueEntity.getDescriptions().keySet()) {
            preparedStatement.setInt(3, catalogueEntity.getId());
            preparedStatement.setInt(4, languageId);
            preparedStatement.setString(1, catalogueEntity.getNames().get(languageId));
            preparedStatement.setString(2, catalogueEntity.getDescriptions().get(languageId));
            preparedStatement.execute();
        }
    }
}
