package jenkem.client;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class JenkemTestSuite extends TestCase {
    public final void testSuite() throws Exception {
        final TestSuite suite = new TestSuite();
        suite.addTestSuite(EngineTester.class);
        suite.addTestSuite(AsciiSchemeTester.class);
        suite.addTestSuite(ColorUtilTester.class);
        suite.addTestSuite(CubeTester.class);

        int testCounter = 0;
        int successCounter = 0;
        int failCounter = 0;
        final Enumeration<Test> tests = suite.tests();
        while (tests.hasMoreElements()) {
            final TestResult result = new TestResult();
            final Test test = tests.nextElement();
            test.run(result);

            testCounter++;
            if (result.wasSuccessful()) {
                System.out.println("OK: " + test.toString());
                successCounter++;
            } else {
                System.out.println("Fail: " + test.toString());
                failCounter++;
            }
        }

        System.out.println("Total Tests: " + testCounter);
        System.out.println("Total Wins:  " + successCounter);
        System.out.println("Total Fails: " + failCounter);

        assertEquals(successCounter, testCounter);
        assertEquals(failCounter, 0);
    }
}
