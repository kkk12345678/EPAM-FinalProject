package com.epam.kkorolkov.finalproject.test.servlet.admin;


import com.epam.kkorolkov.finalproject.admin.publisher.DeletePublisherServlet;
import com.epam.kkorolkov.finalproject.admin.publisher.EditPublisherServlet;
import com.epam.kkorolkov.finalproject.admin.publisher.GetPublishersServlet;
import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.datasource.MyDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
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
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PublisherServletTest extends Mockito {
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_ID =
            "/error?code=400&message=ID parameter is incorrect. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_SUCCESS = "/admin/publishers";

    private static final String JSP_EDIT = "../jsp/admin/publishers/edit-publisher.jsp";
    private static final String INCLUDE_JSP = "../jsp/admin/publishers/publishers.jsp";

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
    private final ServletContext context = mock(ServletContext.class);

    PublisherDao publisherDao;
    DataSource dataSource;
    {
        try {
            publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            dataSource = MyDataSourceFactory.getInstance().getDataSource();
        } catch (DaoException | DbConnectionException e) {
            e.printStackTrace();
        }
        when(context.getContextPath()).thenReturn("");
        when(request.getServletContext()).thenReturn(context);
    }

    public static class Publishers extends ArrayList<Publisher> {}

    @Test
    void publisherServletTest() throws ServletException, IOException, DbException {
        getPublishersServletDoGetTest();
        editPublisherServletDoGetSuccessWithZeroIdTest();
        editPublisherServletDoGetInvalidIdTest();
        editPublisherServletDoGetWithNotExistedIdTest();
        editPublisherServletDoPostCreateSuccessTest();
        editPublisherServletDoPostUpdateSuccessTest();
        editPublisherServletDoPostCreateExistingTest();
        deletePublisherServletDoPostSuccessTest();
    }

    void getPublishersServletDoGetTest() throws ServletException, IOException, DbException {
        when(request.getRequestDispatcher(INCLUDE_JSP)).thenReturn(dispatcher);
        ArgumentCaptor<Publishers> captor = ArgumentCaptor.forClass(Publishers.class);
        new GetPublishersServlet().doGet(request, response);
        verify(request).setAttribute(eq("publishers"), captor.capture());
        Connection connection = dataSource.getConnection();
        assertEquals(publisherDao.getAll(connection), captor.getValue());
        verify(request, times(1)).getRequestDispatcher(INCLUDE_JSP);
        dataSource.release(connection);
    }

    void editPublisherServletDoGetSuccessWithZeroIdTest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("0");
        when(request.getRequestDispatcher(JSP_EDIT)).thenReturn(dispatcher);
        new EditPublisherServlet().doGet(request, response);
        verify(request, times(1)).getRequestDispatcher(JSP_EDIT);
        verify(request, never()).getSession();
        verify(dispatcher, times(2)).include(request, response);
    }



    void editPublisherServletDoGetInvalidIdTest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("k");
        new EditPublisherServlet().doGet(request, response);
        verify(response, times(1)).sendRedirect(REDIRECT_ERROR_ID);
    }

    void editPublisherServletDoGetWithNotExistedIdTest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("-1");
        new EditPublisherServlet().doGet(request, response);
        verify(response, times(2)).sendRedirect(REDIRECT_ERROR_ID);
    }

    void editPublisherServletDoPostCreateSuccessTest() throws IOException, DbException {
        Connection connection = dataSource.getConnection();
        when(request.getParameter("id")).thenReturn("0");
        when(request.getParameter("tag")).thenReturn("some-tag");
        when(request.getParameter("name1")).thenReturn("name1");
        when(request.getParameter("name2")).thenReturn("name2");
        when(request.getParameter("description1")).thenReturn("description1");
        when(request.getParameter("description2")).thenReturn("description2");
        int n = publisherDao.count(connection);
        new EditPublisherServlet().doPost(request, response);
        verify(response).sendRedirect(REDIRECT_SUCCESS);

        assertEquals(n + 1, publisherDao.count(connection));
        Optional<Publisher> optional = publisherDao.get(connection, "some-tag");
        assertTrue(optional.isPresent());
        Publisher publisher = optional.get();
        assertTrue(publisher.getNames().containsValue("name1"));
        assertTrue(publisher.getNames().containsValue("name2"));
        assertTrue(publisher.getDescriptions().containsValue("description1"));
        assertTrue(publisher.getDescriptions().containsValue("description2"));
        dataSource.release(connection);
    }

    void editPublisherServletDoPostUpdateSuccessTest() throws IOException, DbException {
        Connection connection = dataSource.getConnection();
        Publisher publisher = publisherDao.get(connection, "some-tag").orElseThrow();
        when(request.getParameter("id")).thenReturn(String.valueOf(publisher.getId()));
        when(request.getParameter("tag")).thenReturn("some-tag");
        when(request.getParameter("name1")).thenReturn("new-name1");
        when(request.getParameter("name2")).thenReturn("name2");
        when(request.getParameter("description1")).thenReturn("description1");
        when(request.getParameter("description2")).thenReturn("description2");
        new EditPublisherServlet().doPost(request, response);
        verify(response, times(2)).sendRedirect(REDIRECT_SUCCESS);
        publisher = publisherDao.get(connection, "some-tag").orElseThrow();
        assertTrue(publisher.getNames().containsValue("new-name1"));
        dataSource.release(connection);
    }

    void editPublisherServletDoPostCreateExistingTest() throws IOException {
        when(request.getParameter("id")).thenReturn("0");
        when(request.getParameter("tag")).thenReturn("some-tag");
        when(request.getParameter("name1")).thenReturn("new-name1");
        when(request.getParameter("name2")).thenReturn("name2");
        when(request.getParameter("description1")).thenReturn("description1");
        when(request.getParameter("description2")).thenReturn("description2");
        new EditPublisherServlet().doPost(request, response);
        verify(response).sendRedirect(REDIRECT_ERROR_DB);
    }

    void deletePublisherServletDoPostSuccessTest() throws DbException, IOException {
        Connection connection = AbstractDataSourceFactory.getInstance().getDataSource().getConnection();
        int n = publisherDao.count(connection);
        Publisher publisher = publisherDao.get(connection, "some-tag").orElseThrow();
        when(request.getParameter("id")).thenReturn(String.valueOf(publisher.getId()));
        new DeletePublisherServlet().doPost(request, response);
        verify(response, times(3)).sendRedirect(REDIRECT_SUCCESS);
        assertEquals(n - 1, publisherDao.count(connection));
        dataSource.release(connection);
    }

    /*
    @Test
    void daoExceptionTest() throws ServletException, IOException {
        AbstractDaoFactory abstractDaoFactory = mock(AbstractDaoFactory.class);
        when(abstractDaoFactory.getPublisherDao()).thenThrow(DaoException.class);
        new EditPublisherServlet().doGet(request, response);
        verify(response, times(1)).sendRedirect(REDIRECT_ERROR_DAO);
    }

     */
}
