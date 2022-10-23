package com.epam.kkorolkov.finalproject.test.user;

import com.epam.kkorolkov.finalproject.auth.LoginServlet;
import com.epam.kkorolkov.finalproject.db.entity.User;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.http.HttpResponse;

public class AuthServletTest extends Mockito {
    private static final String CONTEXT = "http://localhost:8080/web_war_exploded";
    private static final String PATH = "./jsp/auth/login.jsp";

    @Test
    public void loginGetTest() throws ServletException, IOException {
        final LoginServlet loginServlet = mock(LoginServlet.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        final HttpSession httpSession = mock(HttpSession.class);

        httpSession.setAttribute("user", null);
        when(request.getRequestDispatcher(PATH)).thenReturn(requestDispatcher);
        loginServlet.doGet(request, response);
        verify(request, times(1)).getSession();
        verify(request, times(1)).getRequestDispatcher(PATH);
        verify(requestDispatcher).include(request, response);
    }
}
