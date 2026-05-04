package org.fundaciobit.pluginsib.userinformation.soffid.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.fundaciobit.pluginsib.core.v3.utils.PluginsManager;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.soffid.SoffidUserInformationPlugin;
import org.jboss.logging.Logger;

/**
 * Unit test for simple App.
 * 
 * @author anadal (u80067)
 */
public class SoffidTest {

    protected static final String BASEPACKAGE = "org.fundaciobit.sample.";

    protected static final String NONEXISTENTUSERNAME = "nonexistentusername";

    protected final Logger log = Logger.getLogger(getClass());

    public static void main(String[] args) {

        try {
            SoffidTest tester = new SoffidTest();

            long start = System.currentTimeMillis();

            tester.testErrorInRestQuery();

            tester.testErrorRestAuthentication();

            tester.testCountUsers();

            tester.testGetUserInfoByAdminID();

            tester.testGetUserInfoByUserName();

            tester.testSearchByPartialMultipleValuesOr();

            tester.testSearchByPartialMultipleValuesAnd();

            tester.testSearchByPartialEmail();

            tester.testGetUsersByPartialNameOrPartialSurnames();

            tester.testSearchByPartialAdministrationID();

            tester.testSearchByPartialUsername();

            tester.testGetRolesByUsername();

            tester.testGetUsernamesByRol();

            tester.testGetUserInfoByRol();

            System.out.println("Count usernames:" + tester.getInstance().countAllUsers());

            // NO EXECUTAR Massa Consum
            //System.out.println("All usernames:" + tester.getInstance().getAllUsernames().length);

            tester.testAuthenticate();

            System.out.println((System.currentTimeMillis() - start) + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testErrorInRestQuery() throws Exception {

        IUserInformationPlugin plugin = this.getInstance();
        String usr = "12345677D\"&sortBy=lastName2&sortOrder=nicapamunt&hola=\"sdfasdf";
        try {
            plugin.getUserInfoByUserName(usr);
            throw new Exception("S'esperava un error controlat però la cridada ha finalitzat sens ellançar excepció");

        } catch (Exception e) {
            // OK
            if (e.getMessage().equals("Wrong value for parameter sortOrder")) {
                // OK
            } else {
                e.printStackTrace();
                throw new Exception("S'esperava un error amb missatge \"Wrong value for parameter sortOrder\" però"
                        + " l'error capturat és " + e.getMessage());
            }
        }
    }

    public void testErrorRestAuthentication() throws Exception {

        Properties prop = getSoffidProperties();

        for (Object key : prop.keySet()) {
            System.out.println(key + "=" + prop.get(key));
        }

        prop.setProperty(BASEPACKAGE + SoffidUserInformationPlugin.PASSWORD_PROPERTY, "incorrect_password");

        IUserInformationPlugin plugin = this.getInstance(prop);
        long start = System.currentTimeMillis();
        String nif = getTestProperties().getProperty("dni");

        try {
            UserInfo user = plugin.getUserInfoByAdministrationID(nif);

            if (user == null) {
                log.error("No s'ha trobat l'usuari amb NIF " + nif);
            } else {
                log.error(user.toString());
            }

            throw new Exception("Hauria de fallar amb error 401 Unauthorized");
        } catch (Exception e) {

            String expectedMsg = "HTTP error code 401: Unauthorized";

            if (!e.getMessage().contains(expectedMsg)) {
                e.printStackTrace();
                throw new Exception("Hauria de fallar amb un error amb missatge '" + expectedMsg + "'");
            }
        }

        System.out.println(" Ha tardat: " + (System.currentTimeMillis() - start));
    }

    public void testCountUsers() throws Exception {

        IUserInformationPlugin plugin = this.getInstance();
        long start = System.currentTimeMillis();

        long count = plugin.countAllUsers();
        System.out.println("El número d'usuaris en el sistema és " + count);

        System.out.println(" Ha tardat: " + (System.currentTimeMillis() - start));
    }

    public void testGetAllUsernames() throws Exception {

        IUserInformationPlugin plugin = this.getInstance();
        long start = System.currentTimeMillis();

        try {
            plugin.getAllUsernames();
        } catch (Exception e) {
            if (e.getMessage().equals("getAllUsernames() no està implementat en aquest plugin.")) {
                log.info("El mètode getAllUsernames() no està implementat. OK");
                return;
            } else {
                log.error("S'esperava un error de mètode no implementat però s'ha rebut una excepció diferent: "
                        + e.getMessage());
                throw e;
            }
        }

        System.out.println(" Ha tardat: " + (System.currentTimeMillis() - start));
    }

    public UserInfo testGetUserInfoByAdminID() throws Exception {

        IUserInformationPlugin plugin = this.getInstance();
        long start = System.currentTimeMillis();
        String nif = getTestProperties().getProperty("dni");

        UserInfo ui = plugin.getUserInfoByAdministrationID(nif);

        if (ui == null) {
            System.err.println(" No es troba l'usuari amb NIF " + nif);
        } else {
            System.out.println(" Usuari amb NIF " + nif + ": Nom " + ui.getName() + " | Llinatge  " + ui.getSurname1()
                    + " | nif: " + ui.getAdministrationID() + "| Username: " + ui.getUsername() + " | Email: " + ui.getEmail());
            System.out.println(ui.toFullInfo(""));
        }

        System.out.println(" Ha tardat: " + (System.currentTimeMillis() - start));
        
        return ui;
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

        IUserInformationPlugin soffid = getInstance();

        Map<String, UserInfo> partialValues = new TreeMap<String, UserInfo>();
        /// toUserInfo( username, firstName, lastName, email, administrationID)

        String nif = getTestProperties().getProperty("dni");
        nif = nif.trim();
        UserInfo ui2 = soffid.getUserInfoByAdministrationID(nif);

        int filterPlus = isAnd ? 1 : 2;

        String username = ui2.getUsername();
        String usernamePartial = username.substring(0, username.length() / 2 + filterPlus);
        String partialNif = nif.substring(0, nif.length() / 2 + filterPlus);
        String partialSurname;
        if (isAnd) {
            partialSurname = ui2.getSurname1().substring(0, ui2.getSurname1().length() / 2 + filterPlus);
        } else {
            partialSurname = null;
        }

        String partialEmail = ui2.getEmail().substring(0, (ui2.getEmail().length() / 2) + 2 + filterPlus);

        partialValues.put(
                "(a) Cerca usuari " + username + " amb nif parcial " + partialNif
                        + (isAnd ? (" i llinatge parcial " + partialSurname) : ""),
                toUserInfo(null, null, partialSurname, null, partialNif));

        partialValues.put("(b) Cerca usuari " + username + " amb username parcial " + usernamePartial
                + " i llinatge parcial " + partialSurname,
                toUserInfo(usernamePartial, null, partialSurname, null, null));

        partialValues.put(
                "(c) Cerca usuari " + username + " amb email parcial " + partialEmail
                        + (isAnd ? (" i llinatge parcial " + partialSurname) : ""),
                toUserInfo(usernamePartial, null, partialSurname, null, null));

        log.info(" ========= CERCA AMB MULTIPLES VALORS::getUsersByPartialValues" + (isAnd ? "And" : "Or")
                + "() ============");

        int count = 0;
        for (Entry<String, UserInfo> entry : partialValues.entrySet()) {
            log.info("------------------------" + count + "-------------------------------");
            count++;
            log.info("   - " + entry.getKey());
            UserInfo ui = entry.getValue();
            SearchUsersResult sur;
            if (isAnd) {
                sur = soffid.getUsersByPartialValuesAnd(ui.getUsername(), ui.getName(), ui.getSurname1(), ui.getEmail(),
                        ui.getAdministrationID());
            } else {
                sur = soffid.getUsersByPartialValuesOr(ui.getUsername(), ui.getName(), ui.getSurname1(), ui.getEmail(),
                        ui.getAdministrationID());
            }

            checkSearchUsersResult(sur, username);
            log.info("   - La cerca ha retornat " + sur.getUsers().size() + " usuaris");
            log.info("   - OK");
        }
    }

    /**
     * 
     * @param plugin
     * @throws Exception
     */
    protected List<UserInfo> testSearchByPartialAdministrationID() throws Exception {

        IUserInformationPlugin plugin = getInstance();

        String partialNif = getTestProperties().getProperty("partialdni");

        
        if (partialNif == null || partialNif.isEmpty() ) {
            throw new Exception("No s'ha definit la propietat 'partialdni' en el fitxer test.properties");
        }

        log.info(" ========= CERCA AMB NIF PARCIAL " + partialNif + " ===========");

        SearchUsersResult sur = plugin.getUsersByPartialAdministrationID(partialNif);
        
        if (sur.getStatus().getResultCode() != SearchStatus.RESULT_OK) {
            throw new Exception(" ERROR [testSearchByPartialAdministrationID] = " + sur.getStatus().getResultMessage());
        };
        
        System.out.println("Trobats " + sur.getUsers().size() + " usuaris amb NIF parcial " + partialNif);
        
        for (UserInfo u : sur.getUsers()) {
            if (u.getAdministrationID() == null) {
                throw new Exception(" ERROR [testSearchByPartialAdministrationID] = usuari " + u.getUsername() + " te NIF null i no s'ajusta al partialdni " + partialNif);
            }
            if (u.getAdministrationID().indexOf(partialNif) == -1) {
                throw new Exception(" ERROR [testSearchByPartialAdministrationID] = usuari " + u.getUsername() + " te NIF " + u.getAdministrationID() + " i no s'ajusta al partialdni " + partialNif); 
            }
            // OK
        }

        log.info("      OK. Usuari s'ajusten al partialNif.");
        
        return sur.getUsers();

    }

    protected void checkSearchUsersResult(SearchUsersResult sur, String usernameInResults) throws Exception {

        if (sur.getStatus().getResultCode() != SearchStatus.RESULT_OK) {
            throw new Exception(" ERROR = " + sur.getStatus().getResultMessage());
        }

        List<UserInfo> users = sur.getUsers();

        if (users == null || users.size() == 0) {

            if (usernameInResults == null) {
                // OK
                return;
            } else {
                throw new Exception(" La consulta ha tornat 0 resultats però s'esperava com a mínim 1 resultat amb '"
                        + usernameInResults + "'");
            }

        }

        if (usernameInResults == null) {
            throw new Exception("La consulta ha tornat " + users.size() + " resultats però NO s'esperaven resultats");
        }

        //log.info(base + " USUARIS TROBATS " + users.size());
        for (UserInfo userInfo : users) {

            if (userInfo.getUsername().equals(usernameInResults)) {
                // Trobat l'usuari esperat
                return;
            }
        }

        throw new Exception(" La consulta ha tornat " + users.size() + " resultats però s'esperava"
                + " que entre aquests resultats aparegues '" + usernameInResults + "' però no ha estat així.");

    }

    protected void printSearchUsersResult(final String titol, String textCerca, SearchUsersResult sur) {

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
                    log.info("      - " + userInfo.getAdministrationID() + "\t" + userInfo.getUsername() + "\t"
                            + userInfo.getEmail() + "\t" + userInfo.getFullName());
                }
            }
        }
    }

    protected void testSearchByPartialUsername() throws Exception {

        IUserInformationPlugin soffid = getInstance();

        String[] usrs = getTestProperties().getProperty("usernames").split(",");

        Map<String, String> partialUsernames = new HashMap<String, String>();

        for (int i = 0; i < usrs.length; i++) {
            if (usrs[i].equals(NONEXISTENTUSERNAME)) {
                continue;
            }
            partialUsernames.put(usrs[i], usrs[i].substring(0, usrs[i].length() / 2 + 1));
        }

        for (String username : partialUsernames.keySet()) {

            String partialUsername = partialUsernames.get(username);

            log.info("---------------- Cerca per username parcial " + partialUsername + "  (" + username
                    + ") ---------------------");

            SearchUsersResult sur = soffid.getUsersByPartialUserName(partialUsername);
            checkSearchUsersResult(sur, username);

            log.info("      Resultat Cerca OK.");

        }

    }

    protected void testSearchByPartialEmail() throws Exception {

        IUserInformationPlugin plugin = getInstance();

        String[] usrs = getTestProperties().getProperty("usernames").split(",");

        for (String username : usrs) {

            if (username.equals(NONEXISTENTUSERNAME)) {
                continue;
            }

            UserInfo ui = plugin.getUserInfoByUserName(username);

            String email = ui.getEmail();

            String partialEmail = email.substring(1, email.length() - 2);

            log.info("---------------- Cerca per email parcial " + partialEmail + "  (" + ui.getUsername()
                    + ") ---------------------");

            try {

                SearchUsersResult sur = plugin.getUsersByPartialEmail(partialEmail);

                checkSearchUsersResult(sur, username);

                log.info("      Resultat Cerca OK.");

                throw new Exception("El mètode getUsersByPartialEmail hauria d'estar no implemnetat.");

            } catch (Exception e) {
                if (!e.getMessage().contains("getUsersByPartialEmail(partialEmail) no implementat")) {
                    e.printStackTrace();
                    throw new Exception(
                            "S'esperava una excepció de mètode no implementat però s'ha rebut una excepcio: "
                                    + e.getMessage());
                }
            }

        }

    }

    protected void testGetUsersByPartialNameOrPartialSurnames() throws Exception {

        IUserInformationPlugin plugin = getInstance();
        String[] surnames = getTestProperties().getProperty("searchbypartialsurname").split(",");

        for (String surname : surnames) {
            /*
            if (usr.equals(NONEXISTENTUSERNAME)) {
                continue;
            }
            /*
            UserInfo ui = plugin.getUserInfoByUserName(usr);
            
            if (ui == null) {
                throw new Exception("No s'ha trobat usuari amb username " + usr);
            }
            
            final String partialNameOrPartialSurnames;
            
            if (ui.getSurname2() != null) {
                partialNameOrPartialSurnames = ui.getSurname2();
            } else {
                partialNameOrPartialSurnames = ui.getSurname1();
            }
            */
            log.info(" ========= CERCA AMB NOM/LLINATGE PARCIAL [" + surname + "]");

            SearchUsersResult sur = plugin.getUsersByPartialNameOrPartialSurnames(surname);

            System.out.println(" Resultats => " + sur.getUsers().size());
            
            for(UserInfo users :  sur.getUsers()) {
                System.out.println("   - "  + users.getAdministrationID() + " | " + users.getUsername() + " | " + users.getName() + " | " + users.getSurname1() + " | " + users.getSurname2());
            }
            

            //checkSearchUsersResult(sur, ui.getUsername());

        }

    }

    protected void testGetRolesByUsername() throws Exception {

        IUserInformationPlugin plugin = getInstance();
        String[] usrs = getTestProperties().getProperty("usernames").split(",");
        for (String usr : usrs) {
            if (usr.equals(NONEXISTENTUSERNAME)) {
                continue;
            }
            RolesInfo ri = plugin.getRolesByUsername(usr);
            if (ri == null) {
                System.err.println("Rols de " + usr + ": L'usuari no existeix !!!");
            } else {
                System.out.println("Rols de " + usr + ": " + Arrays.toString(ri.getRoles()));
            }
        }
    }

    protected void testAuthenticate() throws Exception {

        IUserInformationPlugin plugin = getInstance();

        if (plugin.isImplementedAuthenticationByUsernamePasword()) {

            Properties prop = getTestProperties();

            String username = prop.getProperty("auth.username");
            String password = prop.getProperty("auth.password");

            if (!plugin.authenticate(username, password)) {
                throw new Exception("No s'ha pogut autenticar usuari " + username + ".");
            }

            // Autenticació errònia
            if (plugin.authenticate(username, password + "222222")) {
                throw new Exception("Error greu ja que s'ha pogut autenticar l'usuari " + username
                        + " amb una contrasenya incorrecta.");
            }

        } else {
            log.warn("Autenticació Usr-Pwd no està implementada");
        }
    }

    protected void testGetUsernamesByRol() throws Exception {

        IUserInformationPlugin plugin = getInstance();

        String rol = getTestProperties().getProperty("rol");

        String[] users = plugin.getUsernamesByRol(rol);
        System.out.println("Usuaris amb ROL '" + rol + "': " + Arrays.toString(users));
    }

    protected void testGetUserInfoByRol() throws Exception {

        IUserInformationPlugin plugin = getInstance();

        String rol = getTestProperties().getProperty("rol");

        UserInfo[] users = plugin.getUserInfoByRol(rol);
        System.out.print("Usuaris amb ROL '" + rol + "'(" + users.length + "):");
        for (UserInfo userInfo : users) {
            System.out.println("\t" + userInfo.getName() + " " + userInfo.getSurname1() + " " + userInfo.getSurname2()
                    + " - " + userInfo.getUsername() + "(" + userInfo.getAdministrationID() + ")");
        }
        System.out.println();
    }

    
    public List<UserInfo> testGetUserInfoByUserName() throws Exception {

        IUserInformationPlugin kcui = this.getInstance();
        
        
        List<UserInfo> users = new ArrayList<>();

        String usernamesStr = getTestProperties().getProperty("usernames");

        String[] usernames = usernamesStr.split(",");
        for (int i = 0; i < usernames.length; i++) {

            final String username = usernames[i];

            if (username.equals(NONEXISTENTUSERNAME)) {
                continue;
            }

            System.out.println("\n====================]" + username + "[==========================");

            UserInfo ui = kcui.getUserInfoByUserName(username);

            if (ui != null) {

                users.add(ui);
                
                if (username.equals(NONEXISTENTUSERNAME)) {
                    throw new Exception("Error ja que l'usuari " + username + " no existeix i ha retornat valors");
                } else {

                    System.out.println(ui.toFullInfo("\t"));
                }
            } else {
                if (!username.equals(NONEXISTENTUSERNAME)) {
                    System.err.println("L'username [" + usernames[i] + "] no existeix !!!!");
                } else {
                    System.out.println("OK. L'usuari " + username + " no s'ha trobat com era d'esperar.");
                }
            }
        }
        
        
        return users;
    }

    protected IUserInformationPlugin getInstance() throws Exception {
        return getInstance(getSoffidProperties());
    }

    protected IUserInformationPlugin getInstance(Properties prop) throws Exception {

        // final String propertyPlugin = BASEPACKAGE + "userinformationplugin";

        IUserInformationPlugin kcui;

        kcui = (IUserInformationPlugin) PluginsManager.instancePluginByClass(SoffidUserInformationPlugin.class,
                BASEPACKAGE, prop);
        return kcui;
    }

    protected Properties getSoffidProperties() throws Exception, IOException, FileNotFoundException {
        Properties prop = new Properties();

        File f = new File("soffid.properties");

        if (!f.exists()) {
            throw new Exception("Aquest test necessita el fitxer " + f.getAbsolutePath());
        }

        prop.load(new FileInputStream(f));
        return prop;
    }

    protected Properties getTestProperties() throws Exception {
        /*
        Properties prop = new Properties();

        File f = new File("test.properties");

        if (!f.exists()) {
            throw new Exception("Aquest test necessita un fitxer " + f.getAbsolutePath());
        }

        prop.load(new FileInputStream(f));

        return prop;
        */
        
        Properties prop = new Properties();

        File f = new File("test.properties");

        if (!f.exists()) {
            throw new Exception("Aquest test necessita un fitxer " + f.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(f);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8")) {
            prop.load(isr);
        }

        return prop;

    }

    protected UserInfo toUserInfo(String usernamePartial, String firstNamePartial, String lastNamePartial,
            String emailPartial, String administrationIDPartial) throws Exception {
        UserInfo ui = new UserInfo();

        ui.setUsername(usernamePartial);
        ui.setName(firstNamePartial);
        ui.setSurname1(lastNamePartial);
        ui.setEmail(emailPartial);
        ui.setAdministrationID(administrationIDPartial);

        return ui;

    }

}
