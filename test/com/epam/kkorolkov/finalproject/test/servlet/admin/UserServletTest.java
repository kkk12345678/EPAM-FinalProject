package com.epam.kkorolkov.finalproject.test.servlet.admin;

import com.epam.kkorolkov.finalproject.admin.publisher.GetPublishersServlet;
import com.epam.kkorolkov.finalproject.admin.user.*;
import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.datasource.OneConnectionDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.util.UserUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserServletTest extends Mockito {
    private static final String REDIRECT_ERROR_ID =
            "/error?code=400&message=ID parameter is incorrect. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_SUCCESS = "/admin/users";
    private static final String REDIRECT_ERROR_VALIDATION =
            "/error?code=400&message=Email is invalid.";

    private static final String JSP_EDIT = "../jsp/admin/publishers/edit-publisher.jsp";
    private static final String INCLUDE_JSP = "../jsp/admin/users/users.jsp";

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
    private final ServletContext context = mock(ServletContext.class);

    UserDao userDao;
    DataSource dataSource;
    {
        try {
            userDao = AbstractDaoFactory.getInstance().getUserDao();
            dataSource = OneConnectionDataSourceFactory.getInstance().getDataSource();
        } catch (DaoException | DBConnectionException e) {
            e.printStackTrace();
        }
        when(context.getContextPath()).thenReturn("");
        when(request.getServletContext()).thenReturn(context);
    }

    private static final String PARAM_ID = "id";
    private static final String PARAM_FIRST_NAME = "firstname";
    private static final String PARAM_LAST_NAME = "lastname";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD = "password";

    static class Users extends ArrayList<User> {}

    @Test
    void userServletTest() throws ServletException, SQLException, IOException {
        getUsersServletTest();
        addUserServletTest();
        blockUserServletTest();
        unblockUserServletTest();
        deleteUserServletTest();
    }

    void blockUserServletTest() throws IOException, SQLException {
        Connection connection = dataSource.getConnection();
        int id = userDao.get(connection,"some_new_email@example.org").get().getId();
        when(request.getParameter(PARAM_ID)).thenReturn(String.valueOf(id));
        new BlockUserServlet().doPost(request, response);
        verify(response, times(2)).sendRedirect(REDIRECT_SUCCESS);
        assertTrue(userDao.get(connection, id).get().getIsBlocked());
        connection.close();
    }

    void unblockUserServletTest() throws IOException, SQLException {
        Connection connection = dataSource.getConnection();
        int id = userDao.get(connection,"some_new_email@example.org").get().getId();
        when(request.getParameter(PARAM_ID)).thenReturn(String.valueOf(id));
        new UnblockUserServlet().doPost(request, response);
        verify(response, times(3)).sendRedirect(REDIRECT_SUCCESS);
        assertFalse(userDao.get(connection, id).get().getIsBlocked());
        connection.close();
    }

    void deleteUserServletTest() throws IOException, SQLException {
        Connection connection = dataSource.getConnection();
        int n = userDao.count(connection);
        int id = userDao.get(connection,"some_new_email@example.org").get().getId();
        when(request.getParameter(PARAM_ID)).thenReturn(String.valueOf(id));
        new DeleteUserServlet().doPost(request, response);
        assertEquals(n - 1, userDao.count(connection));
        verify(response, times(4)).sendRedirect(REDIRECT_SUCCESS);
        connection.close();
    }

    void addUserServletTest() throws IOException, SQLException {
        when(request.getParameter(PARAM_EMAIL)).thenReturn("some_new_email@example.org");
        when(request.getParameter(PARAM_PASSWORD)).thenReturn("password");
        when(request.getParameter(PARAM_FIRST_NAME)).thenReturn("Donald");
        when(request.getParameter(PARAM_LAST_NAME)).thenReturn("Reagan");
        Connection connection = dataSource.getConnection();
        int n = userDao.count(connection);
        new AddUserServlet().doPost(request, response);
        assertEquals(n + 1, userDao.count(connection));
        verify(response, times(1)).sendRedirect(REDIRECT_SUCCESS);
        new AddUserServlet().doPost(request, response);
        assertEquals(n + 1, userDao.count(connection));
        verify(response, times(1)).sendRedirect(REDIRECT_ERROR_DB);
        connection.close();
    }

    private void getUsersServletTest() throws ServletException, IOException, DBException {
        when(request.getRequestDispatcher(INCLUDE_JSP)).thenReturn(dispatcher);
        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        new GetUsersServlet().doGet(request, response);
        verify(request).setAttribute(eq("users"), captor.capture());
        Connection connection = dataSource.getConnection();
        assertEquals(userDao.getAll(connection), captor.getValue());
        verify(request, times(1)).getRequestDispatcher(INCLUDE_JSP);
        dataSource.release(connection);
    }
}
