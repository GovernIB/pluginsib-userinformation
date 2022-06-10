package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.fundaciobit.pluginsib.core.utils.PluginsManager;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;


/**
 * Unit test for simple App.
 * 
 * @author anadal (u80067)
 */
public class KeyCloakTest {

    protected final Logger log = Logger.getLogger(getClass());

    public static void main(String[] args) {

        try {
            KeyCloakTest tester = new KeyCloakTest();
            IUserInformationPlugin plugin = tester.getInstance();

            // test getAllUsernames
            long start = System.currentTimeMillis();
            System.out.println("All usernames:" + plugin.getAllUsernames().length);
            System.out.println((System.currentTimeMillis() - start) + " ms");
            /*
            // XYZ ZZZ falta test EMAIL

            tester.testSearchByPartialMultipleValuesOr(plugin);

            tester.testSearchByPartialMultipleValuesAnd(plugin);

            tester.testSearchByPartialAdministrationID(plugin);

            tester.testSearchByPartialUsername(plugin);

            tester.testGetUsersByPartialNameOrPartialSurnames(plugin);
            

            /*
            tester.testGetUserInfoByUserName(plugin);

            tester.testGetUserInfoByAdminID(plugin);
            */
            
            
            //tester.testGetRolesByUsername(plugin);

            tester.testGetUsernamesByRol(plugin);

            //tester.testAuthenticate(plugin);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void testSearchByPartialMultipleValuesOr(IUserInformationPlugin plugin)
            throws Exception {

        final boolean isAnd = false;

        testSearchByPartialMultipleValuesAndOr(plugin, isAnd);

    }

    protected void testSearchByPartialMultipleValuesAnd(IUserInformationPlugin plugin)
            throws Exception {

        final boolean isAnd = true;

        testSearchByPartialMultipleValuesAndOr(plugin, isAnd);

    }

    protected void testSearchByPartialMultipleValuesAndOr(IUserInformationPlugin plugin,
            boolean isAnd) throws Exception {

        KeyCloakUserInformationPlugin keycloak = (KeyCloakUserInformationPlugin) plugin;

        Map<String, UserInfo> partialValues = new HashMap<String, UserInfo>();
        /// toUserInfo( username, firstName, lastName, email, administrationID)
        partialValues.put("Username {ana}", toUserInfo("ana", null, null, null, null));
        partialValues.put("NIF {430}", toUserInfo(null, null, null, null, "430"));
        partialValues.put("User {ana} & NIF '430'", toUserInfo("an", null, null, null, "430"));
        partialValues.put("Email {dgtic}", toUserInfo(null, null, null, "dgtic", null));
        partialValues.put("Llinatge {er} & Email {dgtic}", toUserInfo(null, null, "er", "dgtic", null));

        final String titol = " ========= CERCA AMB MULTIPLES VALORS:: ";

        for (Entry<String, UserInfo> entry : partialValues.entrySet()) {
            log.info("");
            UserInfo ui = entry.getValue();
            SearchUsersResult sur;
            if (isAnd) {
                sur = keycloak.getUsersByPartialValuesAnd(ui.getUsername(), ui.getName(),
                        ui.getSurname1(), ui.getEmail(), ui.getAdministrationID());
            } else {
                sur = keycloak.getUsersByPartialValuesOr(ui.getUsername(), ui.getName(),
                        ui.getSurname1(), ui.getEmail(), ui.getAdministrationID());
            }
            printSearchUsersResult(titol, entry.getKey(), sur);
            log.info("");
        }
    }

    /**
     * 
     * @param plugin
     * @throws Exception
     */
    protected void testSearchByPartialAdministrationID(IUserInformationPlugin plugin)
            throws Exception {

        KeyCloakUserInformationPlugin keycloak = (KeyCloakUserInformationPlugin) plugin;

        String[] partialNifs = { "4308", "430" };

        final String titol = " ========= CERCA AMB NIF PARCIAL ";

        for (String partialNif : partialNifs) {
            log.info("");
            SearchUsersResult sur = keycloak.getUsersByPartialAdministrationID(partialNif);
            printSearchUsersResult(titol, partialNif, sur);
            log.info("");
        }
    }

    protected void printSearchUsersResult(final String titol, String textCerca,
            SearchUsersResult sur) {

        final String base = titol + "'" + textCerca + "' => ";

        if (sur.getStatus().getResultCode() != SearchStatus.RESULT_OK) {
            log.error(base + " ERROR = " + sur.getStatus().getResultMessage());
        } else {

            List<UserInfo> users = sur.getUsers();

            if (users == null || users.size() == 0) {
                log.info(base + " No s'han trobat usuaris ... ");
            } else {
                log.info(base + " USUARIS TROBATS " + users.size());
                for (UserInfo userInfo : users) {
                    log.info("      - " + userInfo.getAdministrationID() + "\t"
                            + userInfo.getUsername() + "\t" + userInfo.getEmail() + "\t"
                            + userInfo.getFullName());
                }
            }
        }
    }

    protected void testSearchByPartialUsername(IUserInformationPlugin plugin) throws Exception {

        KeyCloakUserInformationPlugin keycloak = (KeyCloakUserInformationPlugin) plugin;

        final String titol = " ========= CERCA AMB USERNAME PARCIAL ";

        final String[] partialUsernames = { "ana", "spa", "cal", "caib" };

        for (String partialUsername : partialUsernames) {
            log.info("-------------------------------------");
            SearchUsersResult sur = keycloak.getUsersByPartialUserName(partialUsername);
            printSearchUsersResult(titol, partialUsername, sur);
            log.info("");
        }

    }

    protected void testGetUsersByPartialNameOrPartialSurnames(IUserInformationPlugin plugin) throws Exception {

        KeyCloakUserInformationPlugin keycloak = (KeyCloakUserInformationPlugin) plugin;

        final String titol = " ========= CERCA AMB NOM/LLINATGE PARCIAL ";

        final String partialNameOrPartialSurnames = "nad";

        SearchUsersResult sur = keycloak
                .getUsersByPartialNameOrPartialSurnames(partialNameOrPartialSurnames);

        printSearchUsersResult(titol, partialNameOrPartialSurnames, sur);

    }

    protected void testGetRolesByUsername(IUserInformationPlugin plugin) throws Exception {

        String[] usrs = new String[] { "fbosch" };        
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
        
        //String rol = "IGE_USER";
        //String rol = "DEM_USER";
        String rol = "IGE_ADMIN";
        //String rol = "PFI_ADMIN";
        //String rol = "CAR_SUPER";
        //String rol = "CAR_ADMIN";
        
        String[] users = plugin.getUsernamesByRol(rol);
        System.out.println("Usuaris amb ROL '" + rol + "': " + Arrays.toString(users));
    }

    protected void testGetUserInfoByAdminID(IUserInformationPlugin plugin) throws Exception {
        long start = System.currentTimeMillis();
        String nif = "43096845C";

        UserInfo ui = plugin.getUserInfoByAdministrationID(nif);

        if (ui == null) {
            System.err.println(" No es troba l'usuari amb NIF " + nif);
        } else {
            System.out.println(" Usuari amb NIF " + nif + ": Nom " + ui.getName() + " | Llinatge  "
                    + ui.getSurname1() + " | nif: " + ui.getAdministrationID());
        }

        System.out.println(" Ha tardat: " + (System.currentTimeMillis() - start));
    }

    protected void testGetUserInfoByUserName(IUserInformationPlugin kcui) throws Exception {
        String[] usernames = { "anadal", "carpeta", "admin", "jpernia", "desconegut" };
        for (int i = 0; i < usernames.length; i++) {
            UserInfo ui = kcui.getUserInfoByUserName(usernames[i]);

            if (ui != null) {
                System.out.println(" - Nom: " + ui.getName() + " | Llinatge  " + ui.getSurname1()
                        + " | nif: " + ui.getAdministrationID());
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

    protected UserInfo toUserInfo(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial)
            throws Exception {
        UserInfo ui = new UserInfo();

        ui.setUsername(usernamePartial);
        ui.setName(firstNamePartial);
        ui.setSurname1(lastNamePartial);
        ui.setEmail(emailPartial);
        ui.setAdministrationID(administrationIDPartial);

        return ui;

    }

}
