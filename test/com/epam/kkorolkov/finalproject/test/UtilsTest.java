package com.epam.kkorolkov.finalproject.test;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.UserUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.sql.Connection;
import java.util.Optional;

public class UtilsTest {
    @Test
    void hashTest() throws DBException {
        assertEquals(UserUtils.hash("1"), "15d09b0846d4753f15c0d6487e332530b6797a3a001845440916f77ba1433f7c");
    }
}
