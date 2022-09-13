package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.*;

public class MysqlDaoFactory extends AbstractDaoFactory {
    @Override
    public BookDao getBookDao() {
        return new MysqlBookDaoImpl();
    }

    @Override
    public CategoryDao getCategoryDao() {
        return new MysqlCategoryDaoImpl();
    }

    @Override
    public LanguageDao getLanguageDao() {
        return new MysqlLanguageDaoImpl();
    }

    @Override
    public OrderDao getOrderDao() {
        return new MysqlOrderDaoImpl();
    }

    @Override
    public PublisherDao getPublisherDao() {
        return new MysqlPublisherDaoImpl();
    }

    @Override
    public StatusDao getStatusDao() {
        return new MysqlStatusDaoImpl();
    }

    @Override
    public UserDao getUserDao() {
        return new MysqlUserDaoImpl();
    }
}
