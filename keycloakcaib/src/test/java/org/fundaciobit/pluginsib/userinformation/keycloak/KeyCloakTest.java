package org.fundaciobit.pluginsib.userinformation.keycloak;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class KeyCloakTest extends TestCase {
  /**
   * Create the test case
   *
   * @param testName
   *          name of the test case
   */
  public KeyCloakTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(KeyCloakTest.class);
  }

  public static void main(String[] args) {
    new KeyCloakTest("HOLA").testApp();
  }

  /**
   * Rigourous Test :-)
   */
  public void testApp() {

  }
}
