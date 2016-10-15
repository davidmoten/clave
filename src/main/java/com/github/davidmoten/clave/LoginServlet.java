package com.github.davidmoten.clave;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.davidmoten.clave.Tokens.Info;
import com.github.davidmoten.guavamini.Preconditions;

@WebServlet(name = "Login", urlPatterns = "login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = -4082495983860831417L;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Preconditions.checkNotNull(username, "username cannot be null");
        Preconditions.checkNotNull(password, "password cannot be null");
        byte[] cipherKey = Data.instance().cipherKey();
        String token = Tokens.createToken(username, cipherKey);
        resp.getWriter().write(token);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String token = Tokens.createToken("fred", Data.instance().cipherKey());
        Info info = Tokens.parseToken(token, Data.instance().cipherKey());
        System.out.println("username=" + info.username);
        System.out.println();
    }

}
