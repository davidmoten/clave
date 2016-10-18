package com.github.davidmoten.clave;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.github.davidmoten.clave.Tokens.Info;

public class TokensTest {

	@Test
	public void testRoundTrip() {
		Clock clock = new Clock() {

			@Override
			public long now() {
				return 12345;
			}
		};
		String token = Tokens.createToken("fred","password", Store.instance().cipherKey(), clock);
		System.out.println(token);
		Info info = Tokens.parseToken(token, Store.instance().cipherKey());
		assertEquals("fred", info.username);
		assertEquals("password", info.password);
		assertEquals(12345, info.createTime);

		// use same info for token input but token will be different because of
		// salt
		String token2 = Tokens.createToken("fred", "password", Store.instance().cipherKey(), clock);
		Info info2 = Tokens.parseToken(token, Store.instance().cipherKey());
		assertEquals("fred", info2.username);
		assertEquals("password", info2.password);
		assertEquals(12345, info2.createTime);
	}

}
