package com.epam.kkorolkov.finalproject.db.datasource;

/**
 * Provides an instance of {@link TomcatDataSource}.
 */
public class TomcatDataSourceFactory extends AbstractDataSourceFactory {
    /**
     * @return an instance of {@link TomcatDataSource}.
     */
    @Override
    public DataSource getDataSource() {
        return new TomcatDataSource();
    }
}
