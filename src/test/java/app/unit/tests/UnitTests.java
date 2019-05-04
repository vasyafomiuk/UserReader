package app.unit.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import app.resources.utils.Utils;

public class UnitTests {
	Utils utils = new Utils();

	// testing of email validator method
	@Test
	public void test1() {
		assertTrue(utils.isValidEmailAddress("testuser@gmail.com"));
		assertTrue(utils.isValidEmailAddress("firstname_lastname@gmail.com"));
		assertTrue(utils.isValidEmailAddress("firstname.lastname@hotmail.com"));
		assertFalse(utils.isValidEmailAddress("%$#hotmail.com"));
		assertFalse(utils.isValidEmailAddress("mail@hotmail.$"));
		assertFalse(utils.isValidEmailAddress(""));
	}

	// testing of name validator method
	@Test
	public void test2() {
		assertTrue(utils.isValidName("Sahar"));
		assertTrue(utils.isValidName("Bond"));
		assertTrue(utils.isValidName("Bruce"));
		assertTrue(utils.isValidName("Name-Name"));
		assertTrue(utils.isValidName("van Gogh"));
		assertTrue(utils.isValidName("Smith-Jones"));
		assertFalse(utils.isValidName("--"));
		assertFalse(utils.isValidName("$Walter"));
		assertFalse(utils.isValidName("Mülle"));
		
	}

}
