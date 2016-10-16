package com.github.davidmoten.clave;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.davidmoten.clave.Tokens.Info;
import com.github.davidmoten.guavamini.Preconditions;

@WebServlet(name = "List", urlPatterns = "list")
public class ListServlet extends HttpServlet {

    private static final long serialVersionUID = 5888168251126988053L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        Preconditions.checkNotNull(token, "token parameter must not be null");
        Info info = Tokens.parseToken(token, Store.instance().cipherKey());
        resp.getWriter().write("return list for " + info.username + " here as JSON");
    }

}
