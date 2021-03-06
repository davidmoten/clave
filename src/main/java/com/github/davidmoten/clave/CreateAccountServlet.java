package com.github.davidmoten.clave;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.davidmoten.guavamini.Preconditions;

public class CreateAccountServlet extends HttpServlet{

	private static final long serialVersionUID = -125343200849317424L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
        String password = req.getParameter("password");
        Preconditions.checkNotNull(username, "username cannot be null");
        Preconditions.checkNotNull(password, "password cannot be null");
       	Store.instance().createAccount(username, password);
	}

	
	
}
