package jenkem;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JenkemTestSuite extends TestSuite {
    public static Test suite() {
        final TestSuite suite = new TestSuite("All Tests for Jenkem.");
        suite.addTestSuite(EngineTest.class);
        suite.addTestSuite(AsciiSchemeTest.class);
        suite.addTestSuite(ColorUtilTest.class);
        suite.addTestSuite(CubeTest.class);
        return suite;
    }
}