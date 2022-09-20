package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "WelcomeFilter")
public class WelcomeFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpSession session = ((HttpServletRequest) request).getSession();
        User user = (User) session.getAttribute("user");
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        httpResponse.sendRedirect(
                user == null || !user.getIsAdmin()
                ? httpRequest.getContextPath() + "/admin"
                : httpRequest.getContextPath() + "/product"
        );
    }

    @Override
    public void destroy() {

    }

}
