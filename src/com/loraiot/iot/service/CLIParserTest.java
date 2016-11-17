package com.loraiot.iot.service;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CLIParserTest {
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncodeBase64() {
		String user = "this is a time to evaluate "
				+ " the life of long term, we will "
				+ "calculate the time and address of "
				+ "the live long. who are you? where did you "
				+ "come from and you think you a clever or idiot "
				+ "To be frank, i do not care this whatever "
				+ "mum always told us to be patient."
				+ "cartoon network will be broadcasted ";
		try {
			byte[] input = user.getBytes("UTF-8");
			String pk = CLIParser.encodeBase64(input);
			System.out.println(pk);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fail("Not yet implemented");
	}

}
