package org.fundaciobit.pluginsib.userinformation.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author anadal
 *
 */

public class TestUserInfoLdapPlugin {

    @Test
    public void testLdapJUnit() {
        main(null);
    }

    public static void main(String[] args) {
        try {

            File f = new File("connection.properties");

            Properties ldapProperties = new Properties();
            ldapProperties.load(new FileInputStream(f));

            String username, password;
            username = ldapProperties.getProperty("test.username");
            password = ldapProperties.getProperty("test.password");

            System.out.println("Test usant username '" + username + "' i contrasenya " + password + "'...");

            // Si no es defineix res llavors obté la configuració de les Propietats de Sistema
            LdapUserInformationPlugin ldap = new LdapUserInformationPlugin("es.caib.example.", ldapProperties);

            //testRequestUserInfo(username,  ldap);

            //testCountAllusers(ldap);

            //testPartialMethods(ldap, username);

            //testAuthenticate(username, password, ldap);

            //getAllUsernamesTest(ldap);

            //testRoles(ldap, username);

            //testGetRolesOfUsername(ldap, username);
            
            testGetUsersByDepartment(ldap, username);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void testPartialMethods(LdapUserInformationPlugin ldap, String username) throws Exception {

        testGetusersByPartialUsername(ldap, username);

        testGetUsersByPartialNIF(ldap, username);

        testGetUsersByPartialEmail(ldap, username);

        testGetUsersByPartialNameOrSurname(ldap, username);

        

    }

    public static void testGetUsersByDepartment(LdapUserInformationPlugin ldap, String username) throws Exception {
        UserInfo ui = ldap.getUserInfoByUserName(username);

        String camp = "Departament";
        String department = ui.getCompanyDepartment();
        Assert.assertNotNull("L´usuari " + username + " ha retornat departament buit o null", department);

        SearchUsersResult sr = ldap.getUsersByDepartment(department);

        if (sr.getStatus().getResultCode() == SearchStatus.RESULT_OK) {
            System.out
                    .println("S'han trobat " + sr.getUsers().size() + " usuaris amb " + camp + " '" + department + "'");
            for (UserInfo u : sr.getUsers()) {
                System.out.println(
                        u.getUsername() + "[" + u.getName() + " " + u.getSurname1() + " " + u.getSurname2() + "],");
            }
        } else {
            Assert.fail("Error al cercar usuaris per " + camp + " " + department + " ("
                    + SearchStatus.toString(sr.getStatus()) + ")");
        }
    }

    public static void testGetUsersByPartialNameOrSurname(LdapUserInformationPlugin ldap, String username)
            throws Exception {
        UserInfo ui = ldap.getUserInfoByUserName(username);

        String camp = "nom/llinatges";
        String surname2 = ui.getSurname2();
        Assert.assertNotNull("L´usuari " + username + " ha retornat llinatge2 buit o null", surname2);
        String parcial = surname2.substring(0, surname2.length()) + "*";

        SearchUsersResult sr = ldap.getUsersByPartialNameOrPartialSurnames(parcial);

        if (sr.getStatus().getResultCode() == SearchStatus.RESULT_OK) {
            System.out.println("S'han trobat " + sr.getUsers().size() + " usuaris amb " + camp + " parcial " + parcial);
            for (UserInfo u : sr.getUsers()) {
                System.out.println(
                        u.getUsername() + "[" + u.getName() + " " + u.getSurname1() + " " + u.getSurname2() + "],");
            }
        } else {
            Assert.fail("Error al cercar usuaris per " + camp + " parcial " + parcial + " ("
                    + SearchStatus.toString(sr.getStatus()) + ")");
        }
    }

    public static void testGetUsersByPartialEmail(LdapUserInformationPlugin ldap, String username) throws Exception {
        UserInfo ui = ldap.getUserInfoByUserName(username);

        String email = ui.getEmail();
        Assert.assertNotNull("L´usuari " + username + " ha retornat email buit o null", email);
        String parcial = email.substring(0, email.length() / 4) + "*";

        SearchUsersResult sr = ldap.getUsersByPartialEmail(parcial);

        if (sr.getStatus().getResultCode() == SearchStatus.RESULT_OK) {
            System.out.println("S'han trobat " + sr.getUsers().size() + " usuaris amb EMAIL parcial " + parcial);
            for (UserInfo u : sr.getUsers()) {
                System.out.println(u.getUsername() + "[" + u.getEmail() + "],");
            }
        } else {
            Assert.fail("Error al cercar usuaris per EMAIL parcial (" + SearchStatus.toString(sr.getStatus()) + ")");
        }
    }

    public static void testGetUsersByPartialNIF(LdapUserInformationPlugin ldap, String username) throws Exception {
        UserInfo ui = ldap.getUserInfoByUserName(username);

        String nif = ui.getAdministrationID();
        Assert.assertNotNull("L´usuari " + username + " ha retornat nif buit o null", nif);
        String parcial = nif.substring(0, nif.length() / 2 + 2) + "*";

        SearchUsersResult sr = ldap.getUsersByPartialAdministrationID(parcial);

        if (sr.getStatus().getResultCode() == SearchStatus.RESULT_OK) {
            System.out.println("S'han trobat " + sr.getUsers().size() + " usuaris amb NIF parcial " + parcial);
            for (UserInfo u : sr.getUsers()) {
                System.out.println(u.getUsername() + "[" + u.getAdministrationID() + "],");
            }
        } else {
            Assert.fail("Error al cercar usuaris per NIF parcial " + parcial + "("
                    + SearchStatus.toString(sr.getStatus()) + ")");
        }
    }

    public static void testGetusersByPartialUsername(LdapUserInformationPlugin ldap, String username) throws Exception {
        String parcial = username.substring(0, username.length() / 2) + "*";

        SearchUsersResult sr = ldap.getUsersByPartialUserName(parcial);

        if (sr.getStatus().getResultCode() == SearchStatus.RESULT_OK) {
            System.out.println("S'han trobat " + sr.getUsers().size() + " usuaris amb username parcial " + parcial);
            for (UserInfo u : sr.getUsers()) {
                System.out.println(u.getUsername() + ",");
            }
        } else {
            Assert.fail("Error al cercar usuaris per nom parcial " + parcial + ": ("
                    + SearchStatus.toString(sr.getStatus()) + ")");
        }
    }

    public static void testCountAllusers(LdapUserInformationPlugin ldap) throws Exception {

        System.out.println("#AllUsers => " + ldap.countAllUsers());
    }

    public static void testRoles(LdapUserInformationPlugin ldap, String username) throws Exception {

        {
            System.out.println("\n\n======= Cridant a getUsernamesByRol ...");
            String[] roles = new String[] { "PFI_ADMIN", /* "PFI_USER"  */ };

            for (int i = 0; i < roles.length; i++) {
                System.out.println();
                long start = System.currentTimeMillis();
                System.out.println(" ------- Users with role " + roles[i] + " ------- ");
                String[] users = ldap.getUsernamesByRol(roles[i]);

                System.out.println(Arrays.toString(users));
                System.out.flush();
                System.out.println("\nTime: " + (System.currentTimeMillis() - start) + " ms");
            }
        }

        {
            System.out.println("\n\n======= Cridant a getUserInfoByRol ...");
            String[] roles = new String[] { "PFI_ADMIN", "PFI_USER" };

            for (int i = 0; i < roles.length; i++) {
                System.out.println();
                System.out.println(" ------- Users with role " + roles[i] + " ------- ");
                UserInfo[] users = ldap.getUserInfoByRol(roles[i]);
                for (UserInfo u : users) {
                    System.out.print(u.getUsername() + "(" + u.getAdministrationID() + "), ");
                }
                System.out.println();
            }
        }

    }

    public static void testGetRolesOfUsername(LdapUserInformationPlugin ldap, String username) throws Exception {
        System.out.println("\n\n======= Cridant a getRolesByUsername");
        org.fundaciobit.pluginsib.userinformation.RolesInfo rolesInfo = ldap.getRolesByUsername(username);
        if (rolesInfo != null) {
            System.out.println(" ------- getRolesByUsername(" + username + ") ------- ");
            String[] roles = rolesInfo.getRoles();

            for (String rol : roles) {
                System.out.println("    - " + rol);
            }
            System.out.println();
        }
    }

    public static void getAllUsernamesTest(LdapUserInformationPlugin ldap) throws Exception, InterruptedException {
        // METODE GET ALL USERNAMES es el més critic ja que dóna un "error code 4 - Sizelimit Exceeded"

        // 1.- LLista de Tots els Usuaris

        System.out.println();
        System.out.println(" ------------------ ALL USERNAMES -----------");
        String[] all = ldap.getAllUsernames();
        System.out.println(" returned: " + all.length);
        for (int i = 0; i < all.length; i++) {
            System.out.println((i + 1) + ".- " + all[i]);
            if (i > 50) {
                System.out.println("...");
                break;
            }
        }
        System.out.flush();
        Thread.sleep(250);
    }

    public static void testRequestUserInfo(String username, LdapUserInformationPlugin ldap) throws Exception {
        UserInfo userInfo = ldap.getUserInfoByUserName(username);
        System.out.println(" ------- getUserInfoByUserName ------- ");
        if (userInfo == null) {
            System.err.println("No s'ha trobat l'usuari |" + username + "|");
            return;
        } else {

            System.out.println(toString(userInfo));

        }
        System.out.println();

        UserInfo ui = ldap.getUserInfoByAdministrationID(userInfo.getAdministrationID());

        if (ui != null) {
            System.out.println(" ------- getUserInfoByAdministrationID ------- ");
            System.out.println(toString(ui));
            System.out.println();
        }

        // ========= Altres mètodes ========

        // 3.- Obtenir Usuari

        // 3.1.- Obtenir usuari per Nom
        System.out.println();
        System.out.println(" ------------------ getUserInfoByUserName -----------");
        UserInfo u = ldap.getUserInfoByUserName(username);
        System.out.println("    - Info usuari [ username = " + username + "]: ");
        System.out.println("          + Nom: " + u.getName() + " " + u.getSurname1() + " " + u.getSurname2());
        System.out.println("          + Nom Complet : " + u.getFullName());
        System.out.println("          + NIF: " + u.getAdministrationID());
        System.out.println("          + Email: " + u.getEmail());

        // 3.2.- Obtenir usuari per NIF
        String nif = u.getAdministrationID();
        if (nif != null) {
            u = ldap.getUserInfoByAdministrationID(nif);
            System.out.println();
            System.out.println(" ------------------ getUserInfoByAdministrationID -----------");
            if (u == null) {
                System.out.println("  No es troba l'usuari " + username + " per nif " + nif);
            } else {
                System.out.println("    - Info usuari [ nif =" + nif + "]: ");
                System.out.println("          + Nom: " + u.getName() + " " + u.getSurname1() + " " + u.getSurname2());
                System.out.println("          + Nom Complet : " + u.getFullName());
                System.out.println("          + NIF: " + u.getAdministrationID());
                System.out.println("          + Email: " + u.getEmail());
            }
        }
    }

    public static void testAuthenticate(String username, String password, LdapUserInformationPlugin ldap) {
        // 1.- Mètode per autenticar amb usuari contrasenya
        System.out.println();
        Assert.assertTrue("L'usuari no s'ha pogut autenticar amb LDAP", ldap.authenticate(username, password));
        System.out.println("------------- Authenticate: OK");
        System.out.println();
        Assert.assertFalse("L'usuari s'ha pogut autenticar contrasenya invàlida",
                ldap.authenticate(username, password + "22"));
        System.out.println("------------- Authenticate amb contrasenya erronia: OK ");

    }

    public static String toString(UserInfo userInfo) {

        return "{" + "\n\tusername: " + userInfo.getUsername() + "\n\tadministrationID: "
                + userInfo.getAdministrationID() + "\n\tgetFullName: " + userInfo.getFullName() + "\n\temail: "
                + userInfo.getEmail() + "\n}";

    }

}
