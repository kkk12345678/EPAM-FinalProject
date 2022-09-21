package com.epam.kkorolkov.finalproject.db.datasource;

public class TomcatDataSourceFactory extends AbstractDataSourceFactory {
    @Override
    public DataSource getDataSource() {
        return new TomcatDataSource();
    }
}
