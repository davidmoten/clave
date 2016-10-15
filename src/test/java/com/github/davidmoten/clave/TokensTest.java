package com.github.davidmoten.clave;

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
		String token = Tokens.createToken("fred", Data.instance().cipherKey(), clock);
		System.out.println(token);
		Info info = Tokens.parseToken(token, Data.instance().cipherKey());
		Assert.assertEquals("fred", info.username);
		Assert.assertEquals(12345, info.createTime);

		// use same info for token input but token will be different because of
		// salt
		String token2 = Tokens.createToken("fred", Data.instance().cipherKey(), clock);
		Info info2 = Tokens.parseToken(token, Data.instance().cipherKey());
		Assert.assertEquals("fred", info2.username);
		Assert.assertEquals(12345, info2.createTime);
	}

}
