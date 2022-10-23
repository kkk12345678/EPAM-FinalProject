package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.exception.BadRequestException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/edit-cart")
public class EditCartServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if (action == null) {
                throw new BadRequestException();
            }
            HttpSession session = request.getSession();
            Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                switch (action) {
                    case "delete":
                        cart.remove(id);
                        session.setAttribute("cart", cart);
                        break;
                    default:
                }
            } catch (Exception e) {
                throw new BadRequestException(e);
            }

        } catch (BadRequestException e) {
            //TODO handle BadRequestException
        } finally {
            response.sendRedirect("/cart");
        }
    }
}
