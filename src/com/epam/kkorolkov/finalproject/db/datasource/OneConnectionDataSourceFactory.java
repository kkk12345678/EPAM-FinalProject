package com.epam.kkorolkov.finalproject.db.datasource;

/**
 * Provides an instance of {@link OneConnectionDataSource}.
 */
public class OneConnectionDataSourceFactory extends AbstractDataSourceFactory {
    /**
     * @return an instance of {@link OneConnectionDataSource}
     */
    @Override
    public DataSource getDataSource() {
        return new OneConnectionDataSource(dbDriver, dbUrl, dbUser, dbPassword);
    }
}
