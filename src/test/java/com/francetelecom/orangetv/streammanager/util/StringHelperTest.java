package com.francetelecom.orangetv.streammanager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.util.StringHelper;

public class StringHelperTest {

	@Test
	public void testgetLastSplit() throws Exception {

		String value = StringHelper.class.getName();
		String result = StringHelper.getLastSplit(value, '.');
		assertNotNull("result cannot be null", result);
		assertEquals("wrong value!", "ValueHelperTest", result);

		value = "toto";
		result = StringHelper.getLastSplit(value, '.');
		assertNotNull("result cannot be null", result);
		assertEquals("wrong value!", "toto", result);
	}

	@Test
	public void testfixedLengthString() {

		String value = "toto";
		String result = StringHelper.fixedLengthString(value, 10);
		assertNotNull("result cannot be null", result);
		assertEquals("wrong value!", "      toto", result);

		value = "1234567890ABCD";
		result = StringHelper.fixedLengthString(value, 10);
		assertNotNull("result cannot be null", result);
		assertEquals("wrong value!", "567890ABCD", result);

	}

}
