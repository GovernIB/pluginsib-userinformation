package org.fundaciobit.plugins.userinformation.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.UserInfo;

/**
 * 
 * @author anadal
 *
 */
public class TestUserInfoLdapPlugin {

  
  public static void main(String[] args) {
    try {

     

      File f = new File("connection.properties");

      Properties ldapProperties = new Properties();
      ldapProperties.load(new FileInputStream(f));
      
      String username, password;
      username = ldapProperties.getProperty("test.username");
      password = ldapProperties.getProperty("test.password");
      
      System.out.println("username: " + username);
     
      // Si no es defineix res llavors obté la configuració de les Propietats de Sistema
      LdapUserInformationPlugin ldap = new LdapUserInformationPlugin("es.caib.example.", ldapProperties);
      
      UserInfo userInfo = ldap.getUserInfoByUserName(username);
      if (userInfo != null) {
        System.out.println( " ------- getUserInfoByUserName ------- ");
        System.out.println(userInfo.toString());
        System.out.println();
      }
      
      
      UserInfo ui = ldap.getUserInfoByAdministrationID(userInfo.getAdministrationID());
      
      if (ui != null) {
        System.out.println( " ------- getUserInfoByAdministrationID ------- ");
        System.out.println(ui.toString());
        System.out.println();
      }
      
      
      org.fundaciobit.pluginsib.userinformation.RolesInfo rolesInfo = ldap.getRolesByUsername(username);
      if (rolesInfo != null) {
        System.out.println( " ------- rolesInfo(" + username + ") ------- ");
        String[] roles = rolesInfo.getRoles();
        
        for (String rol : roles) {
          System.out.println("    - " + rol );
        }
        System.out.println();
      }
      
      
      String[] roles = new String[] { "PFI_ADMIN", "PFI_USER" };

      for (int i = 0; i < roles.length; i++) {
        System.out.println();
        System.out.println( " ------- Users with role " + roles[i] + ") ------- ");
        String[] users = ldap.getUsernamesByRol(roles[i]);
        System.out.println(Arrays.toString(users));        
      }
      
      // 1.- Mètode per autenticar amb usuari contrasenya
      System.out.println();
      System.out.println("------------- Authenticate: " + ldap.authenticate(username, password));
      System.out.println();
      System.out.println("------------- Authenticate amb contrasenya erronia: " + ldap.authenticate(username, password + "22"));


      // 2.- LLista de Tots els Usuaris
      String[] all = ldap.getAllUsernames();
      System.out.println();
      System.out.println(" ------------------ ALL USERNAMES (" + all.length + ")");
      for (int i = 0; i < all.length ; i++) {
        System.out.println((i++) + ".- " + all[i]);
        if (i > 10) {
          System.out.println("...");
          break;
        }
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
      u = ldap.getUserInfoByAdministrationID(nif);
      System.out.println();
      System.out.println(" ------------------ getUserInfoByAdministrationID -----------");
      System.out.println("    - Info usuari [ nif ="  + nif + "]: ");
      System.out.println("          + Nom: " + u.getName() + " " + u.getSurname1() + " " + u.getSurname2());
      System.out.println("          + Nom Complet : " + u.getFullName());
      System.out.println("          + NIF: " + u.getAdministrationID());
      System.out.println("          + Email: " + u.getEmail());
       

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
