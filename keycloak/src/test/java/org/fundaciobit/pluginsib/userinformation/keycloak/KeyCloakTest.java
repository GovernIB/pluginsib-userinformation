package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Iterator;
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
 * 
 * @author anadal (u80067)
 */
public class KeyCloakTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
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

        try {
            KeyCloakTest tester = new KeyCloakTest("HOLA");
            IUserInformationPlugin plugin = tester.getInstance();

            tester.testGetUserInfoByUserName(plugin);

            tester.testGetUserInfoByAdminID(plugin);

            tester.testGetUsernamesByRol(plugin);

            tester.testGetRolesByUsername(plugin);

            tester.testAuthenticate(plugin);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected void testGetRolesByUsername(IUserInformationPlugin plugin) throws Exception {
        String[] usrs = new String[] { "u80067", "anadal" };
        for (String usr : usrs) {
            RolesInfo ri = plugin.getRolesByUsername(usr);
            if (ri == null) {
                System.err.println("Rols de " + usr + ": L'usuari no existeix !!!");
            } else {
                System.out.println("Rols de " + usr + ": " + Arrays.toString(ri.getRoles()));
            }
        }
    }

    protected void testAuthenticate(IUserInformationPlugin plugin) throws Exception {
        System.out.println("Authenticate: " + plugin.authenticate("anadal", "anadal"));

        System.out.println("Authenticate: " + plugin.authenticate("anadal", "anadal1234"));

        System.out.println("Authenticate: " + plugin.authenticate("u999000", "u999000"));

        System.out.println("Authenticate: " + plugin.authenticate("u999000", "u999000z"));
    }

    protected void testGetUsernamesByRol(IUserInformationPlugin plugin) throws Exception {
        String[] users = plugin.getUsernamesByRol("CAR_ADMIN");
        System.out.println("Usuaris amb ROL 'CAR_ADMIN': " + Arrays.toString(users));
    }

    protected void testGetUserInfoByAdminID(IUserInformationPlugin plugin) throws Exception {
        long start = System.currentTimeMillis();
        String nif = "43096845C";

        UserInfo ui = plugin.getUserInfoByAdministrationID(nif);

        if (ui == null) {
            System.err.println(" No es troba l'usuari amb NIF " + nif);
        } else {
            System.out.println(
                    " Usuari amb NIF " + nif + ": Nom " + ui.getName() + " | Llinatge  "
                            + ui.getSurname1() + " | nif: " + ui.getAdministrationID());
        }

        System.out.println(" Ha tardat: " + (System.currentTimeMillis() - start));
    }

    protected void testGetUserInfoByUserName(IUserInformationPlugin kcui) throws Exception {
        String[] usernames = { "anadal", "carpeta", "admin", "jpernia", "desconegut" };
        for (int i = 0; i < usernames.length; i++) {
            UserInfo ui = kcui.getUserInfoByUserName(usernames[i]);

            if (ui != null) {
                System.out.println(" - Nom: " + ui.getName() + " | Llinatge  "
                        + ui.getSurname1() + " | nif: " + ui.getAdministrationID());
            } else {
                System.err.println(" EL username [" + usernames[i] + "] no existeix !!!!");
            }
        }
    }

    public IUserInformationPlugin getInstance() throws Exception {
        final String basepackage = "org.fundaciobit.sample.";

        // final String propertyPlugin = basepackage + "userinformationplugin";

        Properties prop = new Properties();

        File f = new File("keycloak.properties");

        if (!f.exists()) {
            throw new Exception("Aquest test necessita un fitxer " + f.getAbsolutePath());
        }

        prop.load(new FileInputStream(f));

        IUserInformationPlugin kcui;

        // kcui = new KeyCloakUserInformationPlugin(basepackage, prop);
        kcui = (IUserInformationPlugin) PluginsManager
                .instancePluginByClass(KeyCloakUserInformationPlugin.class, basepackage, prop);
        return kcui;
    }
}
