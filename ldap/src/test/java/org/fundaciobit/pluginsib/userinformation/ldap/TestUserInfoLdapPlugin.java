package org.fundaciobit.pluginsib.userinformation.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.UserInfo;
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

            System.out.println("Test usant username '" + username + "' i contrasenya "+ password+ "'...");

            // Si no es defineix res llavors obté la configuració de les Propietats de Sistema
            LdapUserInformationPlugin ldap = new LdapUserInformationPlugin("es.caib.example.", ldapProperties);

            //fullTests(username, password, ldap);

            //getAllUsernamesTest(ldap);
            
            executeRolesTests(ldap, username);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public static void executeRolesTests(LdapUserInformationPlugin ldap, String username) throws Exception {
        
        
        
        {
            System.out.println("\n\n======= Cridant a getUsernamesByRol ...");
            String[] roles = new String[] { "PFI_ADMIN", "PFI_USER" };

            for (int i = 0; i < roles.length; i++) {
                System.out.println();
                System.out.println(" ------- Users with role " + roles[i] + " ------- ");
                String[] users = ldap.getUsernamesByRol(roles[i]);
                System.out.println(Arrays.toString(users));
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
                    System.out.print(u.getUsername() + "(" + u.getAdministrationID()+ "), ");
                }
                System.out.println();
            }
        }

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
        System.out.println(" returnaed: " + all.length);
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

    public static void fullTests(String username, String password, LdapUserInformationPlugin ldap) throws Exception {
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

       

        // 1.- Mètode per autenticar amb usuari contrasenya
        System.out.println();
        System.out.println("------------- Authenticate: " + ldap.authenticate(username, password));
        System.out.println();
        System.out.println("------------- Authenticate amb contrasenya erronia: "
                + ldap.authenticate(username, password + "22"));

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
                System.out
                        .println("          + Nom: " + u.getName() + " " + u.getSurname1() + " " + u.getSurname2());
                System.out.println("          + Nom Complet : " + u.getFullName());
                System.out.println("          + NIF: " + u.getAdministrationID());
                System.out.println("          + Email: " + u.getEmail());
            }
        }
    }

    public static String toString(UserInfo userInfo) {

        return "{" + "\n\tusername: " + userInfo.getUsername() + "\n\tadministrationID: "
                + userInfo.getAdministrationID() + "\n\tgetFullName: " + userInfo.getFullName() + "\n\temail: "
                + userInfo.getEmail() + "\n}";

    }

}
