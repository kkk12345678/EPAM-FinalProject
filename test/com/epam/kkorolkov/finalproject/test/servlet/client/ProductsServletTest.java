package com.epam.kkorolkov.finalproject.test.servlet.client;

import com.epam.kkorolkov.finalproject.client.ShopFrontServlet;
import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.datasource.MyDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.test.servlet.admin.CategoryServletTest;
import com.epam.kkorolkov.finalproject.test.servlet.admin.PublisherServletTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductsServletTest extends Mockito {
    private static final String REDIRECT_ADMIN = "/admin";

    /** JSP page to include */
    private static final String INCLUDE_PAGE = "jsp/client/products.jsp";

    /** Request parameters */
    private static final String PARAM_PAGE = "page";

    /** Request attributes */
    private static final String ATTR_USER = "user";
    private static final String ATTR_BOOKS = "books";
    private static final String ATTR_CATEGORIES = "categories";
    private static final String ATTR_PUBLISHERS = "publishers";
    private static final String ATTR_MAX_PRICE = "maxPrice";
    private static final String ATTR_MIN_PRICE = "minPrice";

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
    private final ServletContext context = mock(ServletContext.class);
    private final HttpSession session = mock(HttpSession.class);

    private static final int LIMIT = 20;


    DataSource dataSource;
    PublisherDao publisherDao;
    CategoryDao categoryDao;
    BookDao bookDao;

    UserDao userDao;

    {
        try {
            userDao = AbstractDaoFactory.getInstance().getUserDao();
            publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            bookDao = AbstractDaoFactory.getInstance().getBookDao();
            dataSource = MyDataSourceFactory.getInstance().getDataSource();
        } catch (DaoException | DbConnectionException e) {
            e.printStackTrace();
        }
        when(context.getContextPath()).thenReturn("");
        when(request.getServletContext()).thenReturn(context);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void productsServletWhenAdminTest() throws ServletException, IOException, SQLException {
        Connection connection = dataSource.getConnection();
        when(session.getAttribute(ATTR_USER)).thenReturn(userDao.get(connection, 1).get());
        new ShopFrontServlet().doGet(request, response);
        verify(response).sendRedirect(REDIRECT_ADMIN);
        dataSource.release(connection);
    }

    public static class Books extends ArrayList<Book> {}
    @Test
    void productsServletWhenNotAdminTest() throws ServletException, IOException, SQLException, DaoException, BadRequestException {
        when(request.getRequestDispatcher(INCLUDE_PAGE)).thenReturn(dispatcher);
        Connection connection = dataSource.getConnection();
        when(session.getAttribute(ATTR_USER)).thenReturn(userDao.get(connection, 2).get());
        when(request.getParameter(PARAM_PAGE)).thenReturn("1");
        ArgumentCaptor<PublisherServletTest.Publishers> publishersCaptor = ArgumentCaptor.forClass(PublisherServletTest.Publishers.class);
        ArgumentCaptor<CategoryServletTest.Categories> categoriesCaptor = ArgumentCaptor.forClass(CategoryServletTest.Categories.class);
        ArgumentCaptor<Double> minPriceCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> maxPriceCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Books> booksCaptor = ArgumentCaptor.forClass(Books.class);
        new ShopFrontServlet().doGet(request, response);
        verify(request).setAttribute(eq(ATTR_PUBLISHERS), publishersCaptor.capture());
        verify(request).setAttribute(eq(ATTR_CATEGORIES), categoriesCaptor.capture());
        verify(request).setAttribute(eq(ATTR_MIN_PRICE), minPriceCaptor.capture());
        verify(request).setAttribute(eq(ATTR_MAX_PRICE), maxPriceCaptor.capture());
        verify(request).setAttribute(eq(ATTR_BOOKS), booksCaptor.capture());
        assertEquals(publisherDao.getAll(connection), publishersCaptor.getValue());
        assertEquals(categoryDao.getAll(connection), categoriesCaptor.getValue());
        assertEquals(bookDao.getAll(connection, LIMIT, 0, null), booksCaptor.getValue());
        assertEquals(bookDao.getMaxPrice(connection, null), maxPriceCaptor.getValue());
        assertEquals(bookDao.getMinPrice(connection, null), minPriceCaptor.getValue());
        verify(request, times(1)).getRequestDispatcher(INCLUDE_PAGE);
        dataSource.release(connection);
    }
}
