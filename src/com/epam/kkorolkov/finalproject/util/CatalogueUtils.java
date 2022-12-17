package com.epam.kkorolkov.finalproject.util;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.*;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DbException;
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
import java.util.Optional;

/**
 * Class {@code CatalogueUtils} contains static utility methods to
 * work with catalogue data.
 */
public class CatalogueUtils {
    /** Logger */
    protected static final Logger LOGGER = LogManager.getLogger("UTILS");

    /** Replacement parameters */
    private static final String REGEX_TAG = "[^a-zA-Z0-9]+";
    private static final String REPLACE_TAG = "-";

    /** Keys of request parameters */
    private static final String PARAM_ID = "id";
    private static final String PARAM_ISBN = "isbn";
    private static final String PARAM_TAG = "tag";
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_PUBLISHER = "publisher";
    private static final String PARAM_DATE = "date";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_USER = "user";
    private static final String PARAM_SUM = "sum";
    private static final String PARAM_STATUS = "status";
    private static final String PARAM_DESCRIPTION = "description";
    private static final String PARAM_PRICE_MIN = "price_min";
    private static final String PARAM_PRICE_MAX = "price_max";
    private static final String PARAM_SORT_BY = "sort_by";
    private static final String PARAM_SORT_TYPE = "sort_type";

    /** Logger messages */
    private static final String MESSAGE_ERROR_ID = "ID id not an integer.";
    private static final String MESSAGE_ERROR_ISBN = "Incorrect ISBN.";
    private static final String MESSAGE_ERROR_TAG = "Entity tag must not be empty.";
    private static final String MESSAGE_ERROR_UNIQUE = "Entity tag must be unique.";
    private static final String MESSAGE_ERROR_NAME = "Entity name must not be empty.";
    private static final String MESSAGE_ERROR_TAG_FORMATTED = "Entity with tag %s already exists.";

    /**
     * {@code validate(Book book)} method checks whether {@code Book} instance
     * is valid e.i. all necessary fields are present, ISBN is valid, and there is
     * no record in the database with the {@code tag} field.
     *
     * @param book - instance of {@code Book} to validate.
     *
     * @throws DbException if {@code SQLException} was thrown.
     * @throws DaoException if DAO cannot be instantiated.
     * @throws ValidationException if validation failed.
     */
    public static void validate(Book book) throws DbException, DaoException, ValidationException {
        validate((CatalogueEntity) book);
        if (!validateIsbn(book.getIsbn())) {
            LOGGER.info(MESSAGE_ERROR_ISBN);
            throw new ValidationException(MESSAGE_ERROR_ISBN);
        }
    }

    /**
     * {@code validate(CatalogueEntity entity)} method checks whether
     * {@code CatalogueEntity} instance is valid e.i. all necessary fields are present
     * and there is no record in the database with the {@code tag} field.
     *
     * @param entity - instance of {@code CatalogueEntity} to validate.
     *
     * @throws DbException if {@code SQLException} was thrown.
     * @throws DaoException if DAO cannot be instantiated.
     * @throws ValidationException if validation failed.
     */
    @SuppressWarnings("rawtypes")
    public static void validate(CatalogueEntity entity) throws DbException, DaoException, ValidationException {
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

    /**
     * {@code validateIsbn} method checks whether ISBN is valid.
     *
     * @param isbn - ISBN to validate
     *
     * @return {@code true} if ISBN is valid, {@code false} otherwise.
     */
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

    /**
     * Method {@code setBookParameters} is an utility method which
     * retrieves necessary parameters from GET request and puts them
     * to an instance of {@code Map} which later will be processed
     * by SQL SELECT query.
     *
     * @param request - instance of {@code HttpServletRequest} to process.
     *
     * @return {@code Map} with necessary entries.
     */
    public static Map<String, String> setBookParameters(HttpServletRequest request) {
        String categoryId = request.getParameter(PARAM_CATEGORY);
        String publisherId = request.getParameter(PARAM_PUBLISHER);
        String isbn = request.getParameter(PARAM_ISBN);
        String tag = request.getParameter(PARAM_TAG);
        String sortBy = request.getParameter(PARAM_SORT_BY);
        String sortType = request.getParameter(PARAM_SORT_TYPE);
        String pricesMin = request.getParameter(PARAM_PRICE_MIN);
        String pricesMax = request.getParameter(PARAM_PRICE_MAX);
        Map<String, String> parameters = new HashMap<>();
        if (categoryId != null && !"".equals(categoryId)) {
            parameters.put(PARAM_CATEGORY, categoryId);
        }
        if (pricesMin != null && !"".equals(pricesMin) && pricesMax != null && !"".equals(pricesMax)) {
            parameters.put(PARAM_PRICE_MIN, pricesMin);
            parameters.put(PARAM_PRICE_MAX, pricesMax);
        }
        if (publisherId != null && !"".equals(publisherId)) {
            parameters.put(PARAM_PUBLISHER, publisherId);
        }
        if (sortType != null) {
            parameters.put(PARAM_SORT_TYPE, sortType);
        }
        if (isbn != null && !"".equals(isbn)) {
            parameters.put(PARAM_ISBN, isbn);
        }
        if (tag != null && !"".equals(tag)) {
            parameters.put(PARAM_TAG, tag);
        }
        if (sortBy != null) {
            parameters.put(PARAM_SORT_BY, sortBy);
        }
        return parameters;
    }

    /**
     * Method {@code setOrderParameters} is an utility method which
     * retrieves necessary parameters from GET request and puts them
     * to an instance of {@code Map} which later will be processed
     * by SQL SELECT query.
     *
     * @param request - instance of {@code HttpServletRequest} to process.
     *
     * @return {@code Map} with necessary entries.
     */
    public static Map<String, String> setOrderParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        String userName = request.getParameter(PARAM_USER);
        String sum = request.getParameter(PARAM_SUM);
        String date = request.getParameter(PARAM_DATE);
        String status = request.getParameter(PARAM_STATUS);
        if (userName != null && !"".equals(userName)) {
            parameters.put(PARAM_USER, userName);
        }
        if (sum != null && !"".equals(sum)) {
            parameters.put(PARAM_SUM, sum);
        }
        if (date != null && !"".equals(date)) {
            parameters.put(PARAM_DATE, date);
        }
        if (status != null && !"".equals(status)) {
            parameters.put(PARAM_STATUS, status);
        }
        return parameters;
    }

    /**
     * Method {@code setDetailsFromRequest} is an utility method which
     * retrieves necessary parameters from GET request and puts them
     * to an instance of {@code Map} which later will be processed
     * by SQL SELECT query.
     *
     * @param request - instance of {@code HttpServletRequest} to process.
     * @param catalogueEntity - instance of {@code catalogueEntity} to process.
     * @param languages - {@code Map} of languages.
     *
     * @throws BadRequestException is thrown if some request parameters are invalid.
     */
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

    /**
     * Method {@code setDetailsFromEntity} is an utility method which
     * sets {@code PreparedStatement} parameters from an instance
     * of {@code CatalogueEntity}.
     *
     * @param preparedStatement - {@code PreparedStatement} to be set.
     * @param catalogueEntity - instance of {@code CatalogueEntity} where necessary data is contained.
     *
     * @throws SQLException is thrown if {@code PreparedStatement} cannot be set or executed.
     */
    public static void setDetailsFromEntity(PreparedStatement preparedStatement, CatalogueEntity catalogueEntity) throws SQLException {
        for (int languageId : catalogueEntity.getDescriptions().keySet()) {
            preparedStatement.setInt(3, catalogueEntity.getId());
            preparedStatement.setInt(4, languageId);
            preparedStatement.setString(1, catalogueEntity.getNames().get(languageId));
            preparedStatement.setString(2, catalogueEntity.getDescriptions().get(languageId));
            preparedStatement.execute();
        }
    }

    /**
     * MySQL specific method which updates or inserts depending on the {@code query}
     * {@code names} and {@code descriptions} to one of tables <i>publisher_descriptions</i>,
     * <i>category_descriptions</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param entity - an instance of {@link CatalogueEntity} containing necessary data.
     * @param query - SQL query to be executed.
     *
     * @throws SQLException is thrown if {@link SQLException} is thrown while processing the query.
     */
    public static void processCatalogueEntityDetails(Connection connection, CatalogueEntity entity, String query) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(query);
            setDetailsFromEntity(preparedStatement, entity);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            DBUtils.release(preparedStatement);
            DBUtils.release(connection);
        }
    }

}
