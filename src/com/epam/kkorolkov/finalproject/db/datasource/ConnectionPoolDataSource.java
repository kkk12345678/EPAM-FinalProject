package com.epam.kkorolkov.finalproject.db.datasource;

public class ConnectionPoolDataSource extends AbstractDataSourceFactory {
    /*
    private static final int INITIAL_POOL_SIZE = 1;
    private static final Queue<Connection> availableConnections = new ConcurrentLinkedQueue<>();
    private static final Queue<Connection> usedConnections = new ConcurrentLinkedQueue<>();

    static {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            try {
                availableConnections.add(DriverManager.getConnection(url));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    private ConnectionPoolDataSource() {}

     */

    @Override
    public DataSource getDataSource() {
        return null;
    }
/*
    //TODO finish implementation
    public static synchronized Connection getConnection() throws DBException {
        Connection connection;
        if (availableConnections.isEmpty()) {
            try {
                connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DBException(e.getMessage(), e);
            }
        } else {
            connection = availableConnections.poll();
        }
        usedConnections.add(connection);
        return connection;
    }

    public static void releaseConnection(Connection connection) {
        availableConnections.add(connection);
        usedConnections.remove(connection);
    }

 */
}

