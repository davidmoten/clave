package com.github.davidmoten.clave;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.davidmoten.security.PPK;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = -4082495983860831417L;

    private final Map<String, Root> userEncryptionRoots = new ConcurrentHashMap<String, Root>();

    private static final long ROOT_EXPIRY_MS = TimeUnit.DAYS.toMillis(1);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Root root = userEncryptionRoots.compute(username, (name, r) -> computeRoot(name, r));
    }

    private static Root computeRoot(String name, Root root) {
        if (root == null || root.expiryTime < System.currentTimeMillis()) {
            return new Root(nextRootValue(), System.currentTimeMillis() + ROOT_EXPIRY_MS);
        } else {
            return root;
        }
    }

    private static String nextRootValue() {
        return PPK.createKeyPair();
    }

    private static final class Root {
        final String value;
        final long expiryTime;

        Root(String value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

    }

}
