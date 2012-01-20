package jenkem.client;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JenkemTestSuite extends TestCase {
	public void testSuite() throws Exception {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(EngineTester.class);
	}
}
