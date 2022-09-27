package de.prob.core.translator.tests;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author htson
 *         <p>
 *         This abstract class contains utility methods supporting testing.
 *         </p>
 */
public abstract class AbstractTests extends TestCase {

	/**
	 * Constructor: Create max_size test case.
	 */
	public AbstractTests() {
		super();
	}

	/**
	 * Constructor: Create max_size test case with the given name.
	 * 
	 * @param name
	 *            the name of test
	 */
	public AbstractTests(String name) {
		super(name);
	}

	/**
	 * Utility method to compare two string collections. The expected collection
	 * is given in terms of an array of strings. The actual collection is given
	 * as a collection of strings. The two are the same if the number of
	 * elements is the same and every element of the expected collection appear
	 * in the actual collection.
	 * 
	 * @param msg
	 *            a message.
	 * @param actual
	 *            actual collection of strings.
	 * @param expected
	 *            expected array of strings.
	 */
	protected static void assertSameStrings(String msg,
			Collection<String> actual, String... expected) {
		assertEquals(msg + ": Incorrect number of elements\n", expected.length,
				actual.size());
		for (String exp : expected) {
			assertTrue(msg + ": Expected element " + exp + " not found",
					actual.contains(exp));
		}
	}

	/**
	 * Utility method to compare two arrays of strings. The two are the same if
	 * the number of elements is the same, and the strings at the same index are
	 * the same.
	 * 
	 * @param msg
	 *            a message.
	 * @param actual
	 *            actual array of strings.
	 * @param expected
	 *            expected array of strings.
	 */
	protected static void assertSameStrings(String msg, String[] actual,
			String... expected) {
		assertEquals(msg + ": Incorrect number of strings\n", expected.length,
				actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(msg, expected[i], actual[i]);
		}
	}

	/**
	 * Utility method to compare two arrays of objects. The two are the same if
	 * the number of elements is the same, and the objects at the same index are
	 * the same.
	 * 
	 * @param msg
	 *            a message.
	 * @param expected
	 *            expected array of objects.
	 * @param actual
	 *            actual array of objects.
	 */
	protected static void assertSameObjects(String msg, Object[] expected,
			Object[] actual) {
		assertEquals(msg + ": Incorrect number of objects\n", expected.length,
				actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(msg, expected[i], actual[i]);
		}
	}

	/**
	 * Utility method to compare two maps of objects to objects. The two are the
	 * same if the key sets are the same (using
	 * {@link #assertSameSet(String, Set, Set)}), and for each key, they map to
	 * the same value.
	 * 
	 * @param msg
	 *            a messages.
	 * @param expected
	 *            expected map.
	 * @param actual
	 *            actual map.
	 */
	protected void assertSameMap(String msg,
			Map<? extends Object, ? extends Object> expected,
			Map<? extends Object, ? extends Object> actual) {
		Set<? extends Object> expectedKeySet = expected.keySet();
		Set<? extends Object> actualKeySet = actual.keySet();
		assertSameSet(msg, expectedKeySet, actualKeySet);
		for (Object key : expectedKeySet) {
			assertEquals(msg, expected.get(key), actual.get(key));
		}
	}

	/**
	 * Utility method to compare two sets of objects. The two are the same if
	 * they have the same number of elements, and each element of the expected
	 * set appears in the actual set.
	 * 
	 * @param msg
	 * @param expected
	 * @param actual
	 */
	protected void assertSameSet(String msg, Set<? extends Object> expected,
			Set<? extends Object> actual) {
		assertEquals(msg + ": The number of elements must be the same",
				expected.size(), actual.size());
		for (Object elm : expected) {
			assertTrue(msg + ": expected element " + elm + " not found",
					actual.contains(elm));
		}
	}

}