package com.github.davidmoten.clave;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.davidmoten.guavamini.Preconditions;

@WebServlet(name = "Login", urlPatterns = "login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = -4082495983860831417L;

    private final Data data = Data.instance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Preconditions.checkNotNull(username, "username cannot be null");
        Preconditions.checkNotNull(password, "password cannot be null");
        CipherKey cipherKey = data.getOrComputeCipherKey(username);
        String token = Tokens.computeToken(username, cipherKey);
        resp.getWriter().write(token);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        UUID u = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(u.getMostSignificantBits());
        bb.putLong(u.getLeastSignificantBits());
        byte[] value = bb.array();
        CipherKey c = new CipherKey(value, System.currentTimeMillis());
        String token = Tokens.computeToken("fred", c);
        byte[] bytes = Base64.getDecoder().decode(token);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Key aesKey = new SecretKeySpec(value, "AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decoded = cipher.doFinal(bytes);
        String s = new String(decoded, StandardCharsets.UTF_8);
        System.out.println(s);
    }

}
