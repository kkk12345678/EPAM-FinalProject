package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;

public class DBCPDataSource implements DataSource {
    @Override
    public Connection getConnection() throws DBException {
        return null;
    }

    @Override
    public void release(Connection connection) {

    }
    /*
    private static final BasicDataSource ds = new BasicDataSource();

    private



    public Connection getConnection() throws DBException {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException(e.getMessage(), e);
        }
    }

    @Override
    public void release(Connection connection) {

    }


    protected DBCPDataSource(String dbDriver, String dbUrl, String dbUser, String dbPassword){
        ds.setDriverClassName(dbDriver);
        ds.setUrl(dbUrl);
        ds.setUsername(dbUser);
        ds.setPassword(dbPassword);
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
    }

    @Override
    public DataSource getDataSource() {
        return ds;
    }

     */
}
