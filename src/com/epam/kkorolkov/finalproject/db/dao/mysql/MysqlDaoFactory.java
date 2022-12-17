package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.*;

/**
 * A factory class which provides instances of all necessary
 * DAOs to communicate with the MySQL database.
 */
public class MysqlDaoFactory extends AbstractDaoFactory {
    /**
     * @return MySQL specific instance {@link BookDao}.
     */
    @Override
    public BookDao getBookDao() {
        return new MysqlBookDaoImpl();
    }

    /**
     * @return MySQL specific instance {@link CategoryDao}.
     */
    @Override
    public CategoryDao getCategoryDao() {
        return new MysqlCategoryDaoImpl();
    }

    /**
     * @return MySQL specific instance {@link LanguageDao}.
     */
    @Override
    public LanguageDao getLanguageDao() {
        return new MysqlLanguageDaoImpl();
    }

    /**
     * @return MySQL specific instance {@link OrderDao}.
     */
    @Override
    public OrderDao getOrderDao() {
        return new MysqlOrderDaoImpl();
    }

    /**
     * @return MySQL specific instance {@link PublisherDao}.
     */
    @Override
    public PublisherDao getPublisherDao() {
        return new MysqlPublisherDaoImpl();
    }

    /**
     * @return MySQL specific instance {@link StatusDao}.
     */
    @Override
    public StatusDao getStatusDao() {
        return new MysqlStatusDaoImpl();
    }

    /**
     * @return MySQL specific instance {@link UserDao}.
     */
    @Override
    public UserDao getUserDao() {
        return new MysqlUserDaoImpl();
    }
}
