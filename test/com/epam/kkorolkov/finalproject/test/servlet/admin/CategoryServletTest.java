package com.epam.kkorolkov.finalproject.test.servlet.admin;

import com.epam.kkorolkov.finalproject.admin.category.DeleteCategoryServlet;
import com.epam.kkorolkov.finalproject.admin.category.EditCategoryServlet;
import com.epam.kkorolkov.finalproject.admin.category.GetCategoriesServlet;
import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.datasource.OneConnectionDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryServletTest extends Mockito {
    private static final String REDIRECT_ERROR_ID =
            "/error?code=400&message=ID parameter is incorrect. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_SUCCESS = "/admin/categories";

    private static final String JSP_EDIT = "../jsp/admin/categories/edit-category.jsp";
    private static final String INCLUDE_JSP = "../jsp/admin/categories/categories.jsp";

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
    private final ServletContext context = mock(ServletContext.class);

    CategoryDao categoryDao;
    DataSource dataSource;
    {
        try {
            categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            dataSource = OneConnectionDataSourceFactory.getInstance().getDataSource();
        } catch (DaoException | DBConnectionException e) {
            e.printStackTrace();
        }
        when(context.getContextPath()).thenReturn("");
        when(request.getServletContext()).thenReturn(context);
    }


    @Test
    void categoryServletTest() throws ServletException, IOException, DBException {
        getCategoriesServletDoGetTest();
        editCategoryServletDoGetSuccessWithZeroIdTest();
        editCategoryServletDoGetInvalidIdTest();
        editCategoryServletDoGetWithNotExistedIdTest();
        editCategoryServletDoPostCreateSuccessTest();
        editCategoryServletDoPostUpdateSuccessTest();
        editCategoryServletDoPostCreateExistingTest();
        deleteCategoryServletDoPostSuccessTest();
    }

    public static class Categories extends ArrayList<Category> {}

    void getCategoriesServletDoGetTest() throws ServletException, IOException, DBException {
        when(request.getRequestDispatcher(INCLUDE_JSP)).thenReturn(dispatcher);
        ArgumentCaptor<Categories> captor = ArgumentCaptor.forClass(Categories.class);
        new GetCategoriesServlet().doGet(request, response);
        verify(request).setAttribute(eq("categories"), captor.capture());
        Connection connection = dataSource.getConnection();
        assertEquals(categoryDao.getAll(connection), captor.getValue());
        verify(request, times(1)).getRequestDispatcher(INCLUDE_JSP);
        dataSource.release(connection);
    }

    void editCategoryServletDoGetSuccessWithZeroIdTest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("0");
        when(request.getRequestDispatcher(JSP_EDIT)).thenReturn(dispatcher);
        new EditCategoryServlet().doGet(request, response);
        verify(request, times(1)).getRequestDispatcher(JSP_EDIT);
        verify(request, never()).getSession();
        verify(dispatcher, times(2)).include(request, response);
    }


    void editCategoryServletDoGetInvalidIdTest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("k");
        new EditCategoryServlet().doGet(request, response);
        verify(response, times(1)).sendRedirect(REDIRECT_ERROR_ID);
    }

    void editCategoryServletDoGetWithNotExistedIdTest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("-1");
        new EditCategoryServlet().doGet(request, response);
        verify(response, times(2)).sendRedirect(REDIRECT_ERROR_ID);
    }

    void editCategoryServletDoPostCreateSuccessTest() throws IOException, DBException {
        Connection connection = dataSource.getConnection();
        when(request.getParameter("id")).thenReturn("0");
        when(request.getParameter("tag")).thenReturn("some-tag");
        when(request.getParameter("name1")).thenReturn("name1");
        when(request.getParameter("name2")).thenReturn("name2");
        when(request.getParameter("description1")).thenReturn("description1");
        when(request.getParameter("description2")).thenReturn("description2");
        int n = categoryDao.count(connection);
        new EditCategoryServlet().doPost(request, response);
        verify(response).sendRedirect(REDIRECT_SUCCESS);

        assertEquals(n + 1, categoryDao.count(connection));
        Optional<Category> optional = categoryDao.get(connection, "some-tag");
        assertTrue(optional.isPresent());
        Category category = optional.get();
        assertTrue(category.getNames().containsValue("name1"));
        assertTrue(category.getNames().containsValue("name2"));
        assertTrue(category.getDescriptions().containsValue("description1"));
        assertTrue(category.getDescriptions().containsValue("description2"));
        dataSource.release(connection);
    }

    void editCategoryServletDoPostUpdateSuccessTest() throws IOException, DBException {
        Connection connection = dataSource.getConnection();
        Category category = categoryDao.get(connection, "some-tag").orElseThrow();
        when(request.getParameter("id")).thenReturn(String.valueOf(category.getId()));
        when(request.getParameter("tag")).thenReturn("some-tag");
        when(request.getParameter("name1")).thenReturn("new-name1");
        when(request.getParameter("name2")).thenReturn("name2");
        when(request.getParameter("description1")).thenReturn("description1");
        when(request.getParameter("description2")).thenReturn("description2");
        new EditCategoryServlet().doPost(request, response);
        verify(response, times(2)).sendRedirect(REDIRECT_SUCCESS);
        category = categoryDao.get(connection, "some-tag").orElseThrow();
        assertTrue(category.getNames().containsValue("new-name1"));
        dataSource.release(connection);
    }

    void editCategoryServletDoPostCreateExistingTest() throws IOException, DBException {
        when(request.getParameter("id")).thenReturn("0");
        when(request.getParameter("tag")).thenReturn("some-tag");
        when(request.getParameter("name1")).thenReturn("new-name1");
        when(request.getParameter("name2")).thenReturn("name2");
        when(request.getParameter("description1")).thenReturn("description1");
        when(request.getParameter("description2")).thenReturn("description2");
        new EditCategoryServlet().doPost(request, response);
        verify(response).sendRedirect(REDIRECT_ERROR_DB);
    }

    void deleteCategoryServletDoPostSuccessTest() throws DBException, IOException {
        Connection connection = dataSource.getConnection();
        int n = categoryDao.count(connection);
        Category category = categoryDao.get(connection, "some-tag").orElseThrow();
        when(request.getParameter("id")).thenReturn(String.valueOf(category.getId()));
        new DeleteCategoryServlet().doPost(request, response);
        verify(response, times(3)).sendRedirect(REDIRECT_SUCCESS);
        assertEquals(n - 1, categoryDao.count(connection));
        dataSource.release(connection);
    }
}