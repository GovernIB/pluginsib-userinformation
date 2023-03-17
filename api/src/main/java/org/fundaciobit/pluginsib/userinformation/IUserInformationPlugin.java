package org.fundaciobit.pluginsib.userinformation;

import java.security.cert.X509Certificate;

import org.fundaciobit.pluginsib.core.IPlugin;

/**
 * 
 * @author anadal
 * 
 */
public interface IUserInformationPlugin extends IPlugin {

    public static final String USERINFORMATION_BASE_PROPERTY = IPLUGINSIB_BASE_PROPERTIES
            + "userinformation.";

    // =====================================================================
    // =====================================================================
    // ============ I N F O R M A C I Ó - D ' U S U A R I S ===============
    // =====================================================================
    // =====================================================================
    
    
    public boolean isImplementedUserInfoByAdministrationID();
    

    /**
     * Mètode que retorna informació de l'usuari amb nif igual al paràmetre.
     * 
     * @param nif AdministrationID
     * @return Si torna null significa que l'usuari no existeix. Si l'usuari
     *         existeix però no es vol retornar informació del mateix , llavors
     *         només es requereix que es retorni una instància de UserInfo emplenant
     *         com a mínim username i nif.
     */
    public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception;

    /**
     * Mètode que retorna informació de l'usuari amb username igual al paràmetre.
     * 
     * @param nif
     * @return Si torna null significa que l'usuari no existeix. Si l'usuari
     *         existeix però no es vol retornar informació del mateix , llavors
     *         només es requereix que es retorni una instància de UserInfo emplenant
     *         com a mínim username i nif.
     */
    public UserInfo getUserInfoByUserName(String username) throws Exception;

    /**
     * Retorna tots els usernames
     * 
     * @return
     * @throws Exception
     */
    String[] getAllUsernames() throws Exception;

    /**
     * 
     * @return El número total d'usuaris registrats en el sistema
     *         d'autenticació/autorització
     * @throws Exception
     */
    long countAllUsers() throws Exception;

    // =====================================================================
    // =====================================================================
    // =============== C E R Q U E S - P A R C I A L S =====================
    // =====================================================================
    // =====================================================================

    public SearchUsersResult getUsersByPartialUserName(String partialUsername) throws Exception;

    public SearchUsersResult getUsersByPartialNameOrPartialSurnames(String partialNameOrSurname)
            throws Exception;

    public SearchUsersResult getUsersByPartialEmail(String partialEmail) throws Exception;

    public SearchUsersResult getUsersByPartialAdministrationID(String partialAdministratorID)
            throws Exception;

    /**
     * 
     * @return true si el Plugin te implementat codi per poder executar el mètode
     *         {@link #getUsersByPartialValuesAnd(String, String, String, String, String)}
     */
    boolean isImplementedUsersByPartialValuesAnd();

    /**
     * Només es pot cridar a aquest mètode si la cridada a
     * {@link #isImplementedUsersByPartialValuesAnd()} retorna true Executa una
     * cerca en els usuaris a partir dels valors parcials de username, nom,
     * llinatge, email i nif. Es realitza una intersecció del resultat de totes les
     * subcerques (AND). Si algun valor val null, llavors s'ignora la cerca per
     * aquell camp.
     * 
     * @param usernamePartial
     * @param firstNamePartial
     * @param lastNamePartial
     * @param emailPartial
     * @param administrationIDPartial
     * @return
     * @throws Exception
     */

    SearchUsersResult getUsersByPartialValuesAnd(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial)
            throws Exception;

    /**
     * 
     * @return true si el Plugin te implementat codi per poder executar el mètode
     *         {@link #getUsersByPartialValuesOr(String, String, String, String, String)}
     */
    boolean isImplementedUsersByPartialValuesOr();

    /**
     * Només es pot cridar a aquest mètode si la cridada a
     * {@link #isImplementedUsersByPartialValuesOr()} retorna true Executa una cerca
     * en els usuaris a partir dels valors parcials de username, nom, llinatge,
     * email i nif. Es realitza una unió del resultat de totes les subcerques (AND).
     * El resultat no inclou usuaris repetits. Si algun valor val null, llavors
     * s'ignora la cerca per aquell camp.
     * 
     * @param usernamePartial
     * @param firstNamePartial
     * @param lastNamePartial
     * @param emailPartial
     * @param administrationIDPartial
     * @return
     * @throws Exception
     */
    public SearchUsersResult getUsersByPartialValuesOr(String usernamePartial,
            String firstNamePartial, String lastNamePartial, String emailPartial,
            String administrationIDPartial) throws Exception;

    // =====================================================================
    // =====================================================================
    // =================== A U T E N T I C A C I O =========================
    // =====================================================================
    // =====================================================================

    /**
     * 
     * @return true si l'autenticació per username-password està activa
     */
    boolean isImplementedAuthenticationByUsernamePasword();

    /**
     * Aquest mètode només es pot cridar si
     * {@link #isImplementedAuthenticationByUsernamePasword()} retorna true
     * 
     * @param username
     * @param password
     * @return true si l'autenticació emprant username i password és correcta
     * @throws Exception
     */
    boolean authenticate(String username, String password) throws Exception;

    /**
     * 
     * @return true si l'autenticació emprant certificat està activa
     */
    boolean isImplementedAuthenticationByCertificate();

    /**
     * Aquest mètode només es pot cridar si
     * {@link #isImplementedAuthenticationByCertificate()} retorna true
     * 
     * @param username
     * @param password
     * @return true si l'autenticació emprant username i password és correcta
     * @throws Exception
     */
    boolean authenticate(X509Certificate certificate) throws Exception;

    // =====================================================================
    // =====================================================================
    // ============= I N F O R M A C I O - D E - R O L S ===============
    // =====================================================================
    // =====================================================================
    
    boolean isImplementedRolesQueries();

    /**
     * Mètode que retorna els roles associats a l'usuari username per l'aplicatiu
     * 
     * @param username
     * @return
     */
    RolesInfo getRolesByUsername(String username) throws Exception;

    /**
     * Retorna els usernames dels usuris que tenen rol <param>rol</param>
     * 
     * @return Una llista, buida o no, dels usuaris que tenen aquest rol.
     * @throws Exception Si aquesta operació no esta disponible
     */
    String[] getUsernamesByRol(String rol) throws Exception;

}