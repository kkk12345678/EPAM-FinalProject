package com.epam.kkorolkov.finalproject.db.datasource;

public class OneConnectionDataSourceFactory extends AbstractDataSourceFactory {
    @Override
    public DataSource getDataSource() {
        return new OneConnectionDataSource(dbDriver, dbUrl, dbUser, dbPassword);
    }
}
