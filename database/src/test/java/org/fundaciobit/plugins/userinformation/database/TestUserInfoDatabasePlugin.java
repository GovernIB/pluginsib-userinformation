package org.fundaciobit.plugins.userinformation.database;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.apache.log4j.Logger;
import org.fundaciobit.pluginsib.core.utils.PluginsManager;

/**
 * 
 * @author anadal
 *
 */
public class TestUserInfoDatabasePlugin {

    protected final Logger log = Logger.getLogger(getClass());

    protected final Properties testerProperties;

    protected final IUserInformationPlugin plugin;

    public TestUserInfoDatabasePlugin(Properties testerProperties, IUserInformationPlugin plugin) {
        super();
        this.testerProperties = testerProperties;
        this.plugin = plugin;
    }

    public static void main(String[] args) {

        try {

            File f = new File("test.properties");

            if (!f.exists()) {
                throw new Exception(
                        "You must define test.properties. Copy from test.properties.sample");
            }

            Properties testProperties = new Properties();
            testProperties.load(new FileInputStream(f));

            String pluginProperties = testProperties.getProperty("plugin.propeties");

            Properties databaseProperties = new Properties();
            databaseProperties.load(new FileInputStream(new File(pluginProperties)));

            String keybase = testProperties.getProperty("plugin.keybase");

            // Passam pro
            IUserInformationPlugin databasePlugin;
            databasePlugin = (IUserInformationPlugin) PluginsManager.instancePluginByClass(
                    DataBaseUserInformationPlugin.class, keybase, databaseProperties);

            TestUserInfoDatabasePlugin tester = new TestUserInfoDatabasePlugin(testProperties,
                    databasePlugin);

            List<UserInfo> users = tester.testGetUserInfoByUsername();

            tester.testGetUserInfoByAdministrationID(users);

            Set<String> rols = tester.testGetRolesByUsername(users);

            tester.testGetUsernamesByRol(rols);

            tester.testAutenticateByUsernamePassword();

            tester.testGetAllUsernames();

            tester.testCountAllUsers();

            tester.testSearchByPartialAdministrationID();

            tester.testSearchByPartialEmail();

            tester.testSearchByPartialNameOrPartialSurnames();

            tester.testSearchByPartialUsername();

            tester.testSearchByPartialMultipleValuesAnd();

            tester.testSearchByPartialMultipleValuesOr();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void testGetAllUsernames() throws Exception {

        log.info("\n\n============ testGetAllUsernames ===============");

        String[] all = plugin.getAllUsernames();

        StringBuilder str = new StringBuilder("-------- ALL USERNAMES (" + all.length + "): \n");
        int count = 0;
        for (int i = 0; i < all.length; i++) {

            String u = all[i];

            count = count + u.length();

            str.append(u).append(", ");

            if (count > 80) {
                str.append('\n');
                count = 0;
            }
        }

        log.info(str.toString());
    }

    protected void testAutenticateByUsernamePassword() throws Exception {

        log.info("\n\n============ testAutenticateByUsernamePassword ===============");

        if (this.plugin.isImplementedAuthenticationByUsernamePasword()) {

            int i = 0;
            do {
                String username = this.testerProperties.getProperty("auth." + i + ".username");
                String password = this.testerProperties.getProperty("auth." + i + ".password");
                if (username == null || password == null) {
                    break;
                }

                log.info("- Authenticate[" + i + "]: (" + username + "," + password + ") => "
                        + plugin.authenticate(username, password));
                i++;

            } while (true);

        } else {
            log.warn(
                    "L'autenticació emprant username-password està deshabilitada per aquest plugin.");
        }

    }

    public void testGetUsernamesByRol(Set<String> rols) throws Exception {

        log.info("\n\n============ testGetUsernamesByRol ===============");

        for (String r : rols) {

            String[] users = plugin.getUsernamesByRol(r);
            log.info(" ------- Users with role " + r + ": " + Arrays.toString(users));
        }
    }

    protected Set<String> testGetRolesByUsername(List<UserInfo> users) throws Exception {

        log.info("\n\n============ testGetRolesByUsername ===============");

        Set<String> roles = new HashSet<String>();

        for (UserInfo ui : users) {

            RolesInfo rolesInfo = plugin.getRolesByUsername(ui.getUsername());
            if (rolesInfo == null) {
                log.error(" * L'usuari (" + ui.getUsername() + ") no té rols associats !!!!!");
            } else {
                String[] rols = rolesInfo.getRoles();

                log.error(" * L'usuari (" + ui.getUsername() + ") té els rols: "
                        + Arrays.toString(rols));

                for (String r : rols) {
                    roles.add(r);
                }

            }
        }

        return roles;

    }

    protected List<UserInfo> testGetUserInfoByUsername() throws Exception {

        String usernamesStr = testerProperties.getProperty("usernames");

        log.info("\n\n============ testGetUserInfoByUsername ===============");

        String[] usernames = usernamesStr.split(",");

        List<UserInfo> users = new ArrayList<UserInfo>();

        for (int i = 0; i < usernames.length; i++) {
            UserInfo userInfo = plugin.getUserInfoByUserName(usernames[i]);
            if (userInfo == null) {
                log.info("* No es troba l'usuari amb username '" + usernames[i] + "'");
            } else {
                log.info("* " + userInfo.toString());
                users.add(userInfo);
            }
        }

        return users;

    }

    protected void testGetUserInfoByAdministrationID(List<UserInfo> users) throws Exception {

        log.info("\n\n============ testGetUserInfoByAdministrationID ===============");

        for (UserInfo ui : users) {

            if (ui.getAdministrationID() != null) {

                UserInfo ui2 = plugin.getUserInfoByAdministrationID(ui.getAdministrationID());
                if (ui2 == null) {
                    String msg = "La cerca per NIF no troba l'usuari amb Username "
                            + ui.getUsername() + " i NIF " + ui.getAdministrationID();
                    throw new Exception(msg);
                } else {
                    log.info(ui.toString());
                }
            }
        }

    }

    protected void testSearchByPartialAdministrationID() throws Exception {

        String[] partialNifs = this.testerProperties.getProperty("partial.nif").split(",");

        final String titol = " ========= CERCA AMB NIF PARCIAL ";

        for (String partialNif : partialNifs) {
            log.info("");
            SearchUsersResult sur = plugin.getUsersByPartialAdministrationID(partialNif);
            printSearchUsersResult(titol, partialNif, sur);
            log.info("");
        }
    }

    protected void testSearchByPartialEmail() throws Exception {

        final String titol = " ========= CERCA AMB EMAIL PARCIAL ";

        final String[] partialEmails = this.testerProperties.getProperty("partial.email")
                .split(",");

        for (String partialUsername : partialEmails) {
            log.info("-------------------------------------");
            SearchUsersResult sur = plugin.getUsersByPartialEmail(partialUsername);
            printSearchUsersResult(titol, partialUsername, sur);
            log.info("");
        }

    }

    protected void testSearchByPartialNameOrPartialSurnames() throws Exception {

        final String titol = " ========= CERCA AMB NOM/LLINATGE PARCIAL ";

        final String[] partialNameOrPartialSurnames = this.testerProperties
                .getProperty("partial.namesurname").split(",");

        for (String p : partialNameOrPartialSurnames) {
            SearchUsersResult sur = plugin.getUsersByPartialNameOrPartialSurnames(p);

            printSearchUsersResult(titol, p, sur);
        }

    }

    protected void testSearchByPartialUsername() throws Exception {

        final String titol = " ========= CERCA AMB USERNAME PARCIAL ";

        final String[] partialUsernames = this.testerProperties.getProperty("partial.username")
                .split(",");

        for (String partialUsername : partialUsernames) {
            log.info("-------------------------------------");
            SearchUsersResult sur = plugin.getUsersByPartialUserName(partialUsername);
            printSearchUsersResult(titol, partialUsername, sur);
            log.info("");
        }

    }

    protected void testCountAllUsers() throws Exception {

        log.info("\n\nNumero Total d'usuaris => " + this.plugin.countAllUsers() + "\n\n");

    }

    protected void testSearchByPartialMultipleValuesOr() throws Exception {

        final boolean isAnd = false;

        testSearchByPartialMultipleValuesAndOr(isAnd);

    }

    protected void testSearchByPartialMultipleValuesAnd() throws Exception {

        final boolean isAnd = true;

        testSearchByPartialMultipleValuesAndOr(isAnd);

    }

    protected void testSearchByPartialMultipleValuesAndOr(boolean isAnd) throws Exception {

        Map<String, UserInfo> partialValues = new HashMap<String, UserInfo>(); ///
        // toUserInfo( username, firstName, lastName, email, administrationID)
        partialValues.put("Username {ana}", toUserInfo("ana", null, null, null, null));
        partialValues.put("NIF {430}", toUserInfo(null, null, null, null, "430"));
        partialValues.put("User {an} & NIF '430'", toUserInfo("an", null, null, null, "430"));
        partialValues.put("Email {dgtic}", toUserInfo(null, null, null, "dgtic", null));
        partialValues.put("Email {l@fundaciobit}",
                toUserInfo(null, null, null, "l@fundaciobit", null));
        partialValues.put("Llinatge {er} & Email {dgtic}",
                toUserInfo(null, null, "er", "dgtic", null));

        final String titol = " ========= CERCA AMB MULTIPLES VALORS[" + (isAnd ? "AND" : "OR")
                + "]: ";

        for (Entry<String, UserInfo> entry : partialValues.entrySet()) {
            log.info("");
            UserInfo ui = entry.getValue();
            SearchUsersResult sur;
            if (isAnd) {
                sur = plugin.getUsersByPartialValuesAnd(ui.getUsername(), ui.getName(),
                        ui.getSurname1(), ui.getEmail(), ui.getAdministrationID());
            } else {
                sur = plugin.getUsersByPartialValuesOr(ui.getUsername(), ui.getName(),
                        ui.getSurname1(), ui.getEmail(), ui.getAdministrationID());
            }
            printSearchUsersResult(titol, entry.getKey(), sur);
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
