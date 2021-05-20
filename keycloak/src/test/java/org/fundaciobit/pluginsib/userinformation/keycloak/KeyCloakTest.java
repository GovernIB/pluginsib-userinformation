package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import org.fundaciobit.pluginsib.core.utils.PluginsManager;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;

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

    try {

      final String basepackage = "org.fundaciobit.sample.";
      
      final String propertyPlugin = basepackage + "userinformationplugin";

      
                 

      Properties prop = new Properties();
      
      File f = new File("keycloak.properties");
      
      if (!f.exists()) {
         System.err.println("Aquest test necessita un fitxer " + f.getAbsolutePath());
         return;
      }

      prop.load(new FileInputStream(f));

      IUserInformationPlugin kcui;
      
      //kcui = new KeyCloakUserInformationPlugin(basepackage, prop);
      kcui = (IUserInformationPlugin) PluginsManager.instancePluginByClass(KeyCloakUserInformationPlugin.class, basepackage, prop);
      
      
      String[] usernames = {  "carpeta", "admin", "anadal" };
      for (int i = 0; i < usernames.length; i++) {
        UserInfo ui = kcui.getUserInfoByUserName(usernames[i]);

        System.out.println(" - Nom: " + ui.getName() + " | Llinatge  " + ui.getSurname1() + " | nif: " + ui.getAdministrationID());
      }


      {
        long start = System.currentTimeMillis();
        String nif =  "43096845C";
        
        UserInfo ui = kcui.getUserInfoByAdministrationID(nif);
        
        if (ui == null) {
          System.err.println(" No es troba l'usuari amb NIF " + nif);
        } else {
          System.out.println(" Usuari amb NIF " + nif + ": Nom " + ui.getName() + " | Llinatge  " + ui.getSurname1() + " | nif: " + ui.getAdministrationID());
        }
        
        System.out.println(" Ha tardat: " + (System.currentTimeMillis() - start));
      }
      



      String[] users = kcui.getUsernamesByRol("CAR_ADMIN");
      System.out.println("Usuaris amb ROL 'CAR_ADMIN': " + Arrays.toString(users));

      String usr = "anadal";
      RolesInfo ri = kcui.getRolesByUsername(usr);
      System.out.println("Rols de " + usr + ": " + Arrays.toString(ri.getRoles()));
      
      System.out.println("Authenticate: " + kcui.authenticate("anadal", "anadal"));

      System.out.println("Authenticate: " + kcui.authenticate("u999000", "u999000"));
      
      System.out.println("Authenticate: " + kcui.authenticate("u999000", "u999000b"));

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
