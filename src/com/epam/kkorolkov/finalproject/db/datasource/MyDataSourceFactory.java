package com.epam.kkorolkov.finalproject.db.datasource;

/**
 * Provides an instance of {@link MyDataSource}.
 */
public class MyDataSourceFactory extends AbstractDataSourceFactory {
    /**
     * @return an instance of {@link MyDataSource}.
     */
    @Override
    public DataSource getDataSource() {
        return new MyDataSource(dbDriver, dbUrl, dbUser, dbPassword);
    }
}
