package com.github.davidmoten.clave;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetPasswordServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//TODO normally use request header?
		String token = req.getParameter("token");
		String key = req.getParameter("key");
		resp.getWriter().write(Store.instance().getPassword(token, key));
	}

	
	
}
