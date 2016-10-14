package com.github.davidmoten.clave;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = -4082495983860831417L;

    private final Map<String, CipherKey> userCipherKeys = new ConcurrentHashMap<String, CipherKey>();

    private static final long ROOT_EXPIRY_MS = TimeUnit.DAYS.toMillis(1);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        CipherKey cipherKey = userCipherKeys.compute(username, (name, c) -> computeRoot(name, c));
        computeToken(username, cipherKey);
    }

    private static String computeToken(String username, CipherKey cipherKey) {
        try {
            Key aesKey = new SecretKeySpec(cipherKey.value, "AES");
            System.out.println("cipherKey.value size=" + cipherKey.value.length);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            String info = username + "\t" + cipherKey.expiryTime;
            byte[] encrypted = cipher.doFinal(info.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private static CipherKey computeRoot(String name, CipherKey c) {
        if (c == null || c.expiryTime < System.currentTimeMillis()) {
            return new CipherKey(nextValue(), System.currentTimeMillis() + ROOT_EXPIRY_MS);
        } else {
            return c;
        }
    }

    private static byte[] nextValue() {
        return UUID.randomUUID().toString().replace("-", "").getBytes(StandardCharsets.UTF_8);
    }

    private static final class CipherKey {
        final byte[] value;
        final long expiryTime;

        CipherKey(byte[] value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        UUID u = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(u.getMostSignificantBits());
        bb.putLong(u.getLeastSignificantBits());
        byte[] value = bb.array();
        CipherKey c = new CipherKey(value, System.currentTimeMillis());
        String token = computeToken("fred", c);
        byte[] bytes = Base64.getDecoder().decode(token);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Key aesKey = new SecretKeySpec(value, "AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decoded = cipher.doFinal(bytes);
        String s = new String(decoded, StandardCharsets.UTF_8);
        System.out.println(s);
    }

}
