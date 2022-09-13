package com.epam.kkorolkov.finalproject.db.datasource;

public class DBCPDataSourceFactory extends AbstractDataSourceFactory {
    @Override
    public DataSource getDataSource() {
        return null;
        //return new DBCPDataSource(dbDriver, dbUrl, dbUser, dbPassword);
    }
}
