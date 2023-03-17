package org.fundaciobit.plugins.userinformation.database;

import java.lang.reflect.Field;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.fundaciobit.pluginsib.userinformation.AbstractUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo.Gender;
import org.fundaciobit.pluginsib.core.utils.CertificateUtils;

/**
 * 
 * @author anadal
 *
 */
public class DataBaseUserInformationPlugin extends AbstractUserInformationPlugin {

    private static final String DB_BASE_PROPERTIES = USERINFORMATION_BASE_PROPERTY + "database.";

    private static final String DATABASE_JNDI = DB_BASE_PROPERTIES + "jndi";

    private static final String DATABASE_CONNECTION_URL = DB_BASE_PROPERTIES + "connection.url";
    private static final String DATABASE_CONNECTION_USERNAME = DB_BASE_PROPERTIES
            + "connection.username";
    private static final String DATABASE_CONNECTION_PASSWORD = DB_BASE_PROPERTIES
            + "connection.password";

    private static final String DEBUG_PROPERTY = DB_BASE_PROPERTIES + "debug";

    private static final String USERS_TABLE = DB_BASE_PROPERTIES + "users_table";

    private static final String USERS_USERNAME_COLUMN = DB_BASE_PROPERTIES + "username_column";

    private static final String USERS_PASSWORD_COLUMN = DB_BASE_PROPERTIES + "password_column";

    private static final String USERS_EMAIL_COLUMN = DB_BASE_PROPERTIES + "email_column";

    private static final String USERS_NAME_COLUMN = DB_BASE_PROPERTIES + "name_column";
    private static final String USERS_SURNAME1_COLUMN = DB_BASE_PROPERTIES + "name_surname1";
    private static final String USERS_SURNAME2_COLUMN = DB_BASE_PROPERTIES + "name_surname2";

    private static final String USERS_ADMINISTRATIONID_COLUMN = DB_BASE_PROPERTIES
            + "administrationID_column";

    public static final String USERROLES_TABLE = DB_BASE_PROPERTIES + "userroles_table";

    public static final String USERROLES_ROLENAME_COLUMN = DB_BASE_PROPERTIES
            + "userroles_rolename_column";

    public static final String USERROLES_USERNAME_COLUMN = DB_BASE_PROPERTIES
            + "userroles_username_column";

    public static final String MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY = DB_BASE_PROPERTIES
            + "minimumcharacterstosearch";

    public static final String MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES = DB_BASE_PROPERTIES
            + "maxallowednumberofresultsinpartialsearches";

    /**
     * 
     */
    public DataBaseUserInformationPlugin() {
        super();
    }

    /**
     * @param propertyKeyBase
     */
    public DataBaseUserInformationPlugin(String propertyKeyBase) {
        super(propertyKeyBase);
    }

    /**
     * @param propertyKeyBase
     * @param properties
     */
    public DataBaseUserInformationPlugin(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

    // =====================================================================
    // =====================================================================
    // ============ I N F O R M A C I Ó - D ' U S U A R I S ===============
    // =====================================================================
    // =====================================================================

    @Override
    public long countAllUsers() throws Exception {

        final String where = null;
        final Map<Integer, String> whereParams = null;
        return executeCount(where, whereParams);
    }
    

    @Override
    public boolean isImplementedUserInfoByAdministrationID() {
        return true;
    }


    @Override
    public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception {

        if (administrationID == null) {
            return null;
        }

        String nifColumn = getPropertyRequired(USERS_ADMINISTRATIONID_COLUMN);
        String where = nifColumn + "=?";
        Map<Integer, String> whereParams = new HashMap<Integer, String>();
        whereParams.put(1, administrationID);

        List<UserInfo> list = executeQuery(where, whereParams);

        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }

    }

    @Override
    public UserInfo getUserInfoByUserName(String username) throws Exception {

        if (username == null) {
            return null;
        }

        String usernamecolumn = getPropertyRequired(USERS_USERNAME_COLUMN);
        String where = usernamecolumn + "=?";
        Map<Integer, String> whereParams = new HashMap<Integer, String>();
        whereParams.put(1, username);

        List<UserInfo> list = executeQuery(where, whereParams);

        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }

    }

    @Override
    public String[] getAllUsernames() throws Exception {

        String usersTable = getPropertyRequired(USERS_TABLE);
        String userColumn = getPropertyRequired(USERS_USERNAME_COLUMN);
        final String query = "select " + userColumn + " from " + usersTable;

        Connection c = getConnection();
        List<String> usuaris = new ArrayList<String>();

        try {
            PreparedStatement ps = c.prepareStatement(query);
            try {
                ResultSet rs = ps.executeQuery();
                try {
                    while (rs.next()) {
                        usuaris.add(rs.getString(1));
                    }
                } finally {
                    closeResultSet(rs);
                }
            } finally {
                closePreparedStatement(ps);
            }

        } finally {
            closeConnection(c);
        }
        return usuaris.toArray(new String[usuaris.size()]);

    }

    // =====================================================================
    // =====================================================================
    // =============== C E R Q U E S - P A R C I A L S =====================
    // =====================================================================
    // =====================================================================

    @Override
    public SearchUsersResult getUsersByPartialAdministrationID(String partialAdministratorID)
            throws Exception {

        WhereInfo wi = getAdministrationIDWhereInfo(partialAdministratorID);

        return executePartialCommon(wi.where, wi.whereParams, "partialAdministratorID",
                partialAdministratorID);
    }

    protected WhereInfo getAdministrationIDWhereInfo(String partialAdministratorID)
            throws Exception {
        WhereInfo wi;
        {
            final String column = getPropertyRequired(USERS_ADMINISTRATIONID_COLUMN);
            final String where = "LOWER(" + column + ") LIKE ?";
            final Map<Integer, String> whereParams = new HashMap<Integer, String>();
            whereParams.put(1, "%" + partialAdministratorID.toLowerCase() + "%");
            wi = new WhereInfo(where, whereParams);
        }
        return wi;
    }

    @Override
    public SearchUsersResult getUsersByPartialEmail(String partialEmail) throws Exception {

        WhereInfo wi;
        wi = getEmailWhereInfo(partialEmail);

        return executePartialCommon(wi.where, wi.whereParams, "partialEmail", partialEmail);
    }

    protected WhereInfo getEmailWhereInfo(String partialEmail) throws Exception {
        WhereInfo wi;
        {
            final String column = getPropertyRequired(USERS_EMAIL_COLUMN);
            final String where = "LOWER(" + column + ") LIKE ?";
            final Map<Integer, String> whereParams = new HashMap<Integer, String>();
            whereParams.put(1, "%" + partialEmail.toLowerCase() + "%");
            wi = new WhereInfo(where, whereParams);
        }
        return wi;
    }

    @Override
    public SearchUsersResult getUsersByPartialNameOrPartialSurnames(String partialNameOrSurname)
            throws Exception {

        final String operation = "OR";
        WhereInfo wi = getNameSurnameWhereInfo(partialNameOrSurname, partialNameOrSurname,
                operation);

        return executePartialCommon(wi.where, wi.whereParams, "partialNameOrSurname",
                partialNameOrSurname);

    }

    protected WhereInfo getNameSurnameWhereInfo(String partialName, String partialSurname,
            String operation) throws Exception {

        final String columnName = getPropertyRequired(USERS_NAME_COLUMN);

        final Map<Integer, String> whereParams = new HashMap<Integer, String>();

        final StringBuilder where = new StringBuilder();

        if (!empty(partialName)) {
            where.append("LOWER(" + columnName + ") LIKE ?");
            whereParams.put(whereParams.size() + 1, "%" + partialName.toLowerCase() + "%");
        }

        final String columnSurname1 = getProperty(USERS_SURNAME1_COLUMN);

        if (empty(columnSurname1)) {
            if (!empty(partialSurname) && !partialSurname.equals(partialName)) {
                if (where.length() != 0) {
                    where.append(" ").append(operation).append(" ");
                }
                where.append("LOWER(" + columnName + ") LIKE ?");
                whereParams.put(whereParams.size() + 1, "%" + partialSurname.toLowerCase() + "%");
            }

        } else {

            if (where.length() != 0) {
                where.append(" ").append(operation).append(" ");
            }

            where.append("(");
            where.append("LOWER(" + columnSurname1 + ") LIKE ?");
            whereParams.put(whereParams.size() + 1, "%" + partialSurname.toLowerCase() + "%");

            final String columnSurname2 = getProperty(USERS_SURNAME2_COLUMN);

            if (!empty(columnSurname2)) {
                where.append("OR LOWER(" + columnSurname2 + ") LIKE ?");
                whereParams.put(whereParams.size() + 1, "%" + partialSurname.toLowerCase() + "%");
            }
            where.append(")");
        }

        WhereInfo wi = new WhereInfo(where.toString(), whereParams);

        return wi;
    }

    @Override
    public SearchUsersResult getUsersByPartialUserName(String partialUsername) throws Exception {
        WhereInfo wi;
        wi = getUsernameWhereInfo(partialUsername);

        return executePartialCommon(wi.where, wi.whereParams, "partialUsername", partialUsername);
    }

    protected WhereInfo getUsernameWhereInfo(String partialUsername) throws Exception {
        WhereInfo wi;
        {
            final String column = getPropertyRequired(USERS_USERNAME_COLUMN);
            final String where = "LOWER(" + column + ") LIKE ?";
            final Map<Integer, String> whereParams = new HashMap<Integer, String>();
            whereParams.put(1, "%" + partialUsername.toLowerCase() + "%");
            wi = new WhereInfo(where.toString(), whereParams);
        }
        return wi;
    }

    @Override
    public boolean isImplementedUsersByPartialValuesAnd() {

        return true;
    }

    /**
     * Executa una cerca en els usuaris a partir dels valors parcials de username,
     * nom, llinatge, email i nif. Es realitza una intersecció del resultat de totes
     * les subcerques (AND). Si algun valor val null, llavors s'ignora la cerca per
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
    @Override
    public SearchUsersResult getUsersByPartialValuesAnd(String usernamePartial,
            String firstNamePartial, String lastNamePartial, String emailPartial,
            String administrationIDPartial) throws Exception {

        final String operation = " AND ";

        return getUsersByPartialValues(usernamePartial, firstNamePartial, lastNamePartial,
                emailPartial, administrationIDPartial, operation);
    }

    @Override
    public boolean isImplementedUsersByPartialValuesOr() {

        return true;
    }

    /**
     * Executa una cerca en els usuaris a partir dels valors parcials de username,
     * nom, llinatge, email i nif. Es realitza una unió del resultat de totes les
     * subcerques (AND). El resultat no inclou usuaris repetits. Si algun valor val
     * null, llavors s'ignora la cerca per aquell camp.
     * 
     * @param usernamePartial
     * @param firstNamePartial
     * @param lastNamePartial
     * @param emailPartial
     * @param administrationIDPartial
     * @return
     * @throws Exception
     */
    @Override
    public SearchUsersResult getUsersByPartialValuesOr(String usernamePartial,
            String firstNamePartial, String lastNamePartial, String emailPartial,
            String administrationIDPartial) throws Exception {
        final String operation = "OR";
        return getUsersByPartialValues(usernamePartial, firstNamePartial, lastNamePartial,
                emailPartial, administrationIDPartial, operation);
    }

    protected SearchUsersResult getUsersByPartialValues(String usernamePartial,
            String firstNamePartial, String lastNamePartial, String emailPartial,
            String administrationIDPartial, String operation) throws Exception, SQLException {
        WhereInfo whereInfo = new WhereInfo("", new HashMap<Integer, String>());

        if (!empty(usernamePartial)) {
            WhereInfo uwi = getUsernameWhereInfo(usernamePartial);
            whereInfo.append(uwi, operation);
        }

        if (!empty(firstNamePartial) || !empty(lastNamePartial)) {
            WhereInfo uwi = getNameSurnameWhereInfo(firstNamePartial, lastNamePartial, operation);
            whereInfo.append(uwi, operation);
        }

        if (!empty(emailPartial)) {
            WhereInfo uwi = getEmailWhereInfo(emailPartial);
            whereInfo.append(uwi, operation);
        }

        if (!empty(administrationIDPartial)) {
            WhereInfo uwi = getAdministrationIDWhereInfo(administrationIDPartial);
            whereInfo.append(uwi, operation);
        }

        if (whereInfo.where.isEmpty()) {
            return new SearchUsersResult(errorIntern("No s'ha definit cap element de cerca"));
        }

        return executePartialCommon(whereInfo.where, whereInfo.whereParams, null, null);
    }

    // =====================================================================
    // =====================================================================
    // =================== A U T E N T I C A C I O =========================
    // =====================================================================
    // =====================================================================

    @Override
    public boolean isImplementedAuthenticationByUsernamePasword() {
        return true;
    }

    @Override
    public boolean authenticate(String username, String password) throws Exception {

        if (username == null || password == null) {
            return false;
        }

        String passwordColumn = getPropertyRequired(USERS_PASSWORD_COLUMN);

        String usersTable = getPropertyRequired(USERS_TABLE);

        String userColumn = getPropertyRequired(USERS_USERNAME_COLUMN);

        String where = userColumn + " = ? AND " + passwordColumn + " = ? ";

        final String query = "select " + userColumn + " from " + usersTable + " where " + where;

        Connection c = getConnection();

        try {
            PreparedStatement ps = c.prepareStatement(query);
            try {
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        return true;
                    } else {
                        return false;
                    }
                } finally {
                    closeResultSet(rs);
                }
            } finally {
                closePreparedStatement(ps);
            }

        } finally {
            closeConnection(c);
        }

    }

    @Override
    public boolean isImplementedAuthenticationByCertificate() {
        return true;
    }

    @Override
    public boolean authenticate(X509Certificate certificate) throws Exception {

        if (certificate == null) {
            return false;
        }

        String nif = CertificateUtils.getDNI(certificate);
        if (nif == null) {
            throw new Exception("No puc extreure el NIF del Certificat " + certificate.toString());
        }

        String usersTable = getPropertyRequired(USERS_TABLE);
        String nifColumn = getPropertyRequired(USERS_ADMINISTRATIONID_COLUMN);

        String where = nifColumn + " = ? OR " + nifColumn + " = ? OR " + nifColumn + " = ?";
        final String query = "select " + nifColumn + " from " + usersTable + " where " + where;

        Connection c = getConnection();

        try {
            PreparedStatement ps = c.prepareStatement(query);
            try {
                ps.setString(1, nif);
                ps.setString(2, nif.toUpperCase());
                ps.setString(3, nif.toLowerCase());

                ResultSet rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        return true;
                    } else {
                        return false;
                    }
                } finally {
                    closeResultSet(rs);
                }
            } finally {
                closePreparedStatement(ps);
            }

        } finally {
            closeConnection(c);
        }

    }

    // =====================================================================
    // =====================================================================
    // ============= I N F O R M A C I O - D E - R O L S ===============
    // =====================================================================
    // =====================================================================
    

    @Override
    public boolean isImplementedRolesQueries() {
        return true;
    }

    @Override
    public String[] getUsernamesByRol(String rol) throws Exception {

        if (rol == null) {
            return new String[] {};
        }

        String userRolesTable = getPropertyRequired(USERROLES_TABLE);
        String rolename = getPropertyRequired(USERROLES_ROLENAME_COLUMN);

        String usercolumn = getPropertyRequired(USERROLES_USERNAME_COLUMN);

        final String where = rolename + " = ? ";
        final String query = "select " + usercolumn + " from " + userRolesTable + " where " + where;

        Connection c = getConnection();
        List<String> usuaris = new ArrayList<String>();

        try {
            PreparedStatement ps = c.prepareStatement(query);
            try {
                ps.setString(1, rol);
                ResultSet rs = ps.executeQuery();
                try {
                    while (rs.next()) {
                        usuaris.add(rs.getString(1));
                    }
                } finally {
                    closeResultSet(rs);
                }
            } finally {
                closePreparedStatement(ps);
            }

        } finally {
            closeConnection(c);
        }
        return usuaris.toArray(new String[usuaris.size()]);

    }

    @Override
    public RolesInfo getRolesByUsername(String username) throws Exception {

        if (username == null) {
            throw new NullPointerException("Parameter UserName is NULL");
        }

        String rolename = getPropertyRequired(USERROLES_ROLENAME_COLUMN);
        String roletable = getPropertyRequired(USERROLES_TABLE);
        String usercolumn = getPropertyRequired(USERROLES_USERNAME_COLUMN);

        final String query = "select " + rolename + " from " + roletable + " where " + usercolumn
                + " = ?";

        List<String> roles = new ArrayList<String>();
        Connection c = getConnection();
        try {
            PreparedStatement ps = c.prepareStatement(query);
            try {

                ps.setString(1, username);

                ResultSet rs = ps.executeQuery();
                try {
                    while (rs.next()) {
                        roles.add(rs.getString(1));
                    }
                } finally {
                    closeResultSet(rs);
                }
            } finally {
                closePreparedStatement(ps);
            }

        } finally {
            closeConnection(c);
        }

        RolesInfo info = new RolesInfo(username, roles.toArray(new String[roles.size()]));

        return info;
    }

    // =====================================================================
    // =====================================================================
    // ========================= U T I L I T A T S =========================
    // =====================================================================
    // =====================================================================

    protected class WhereInfo {

        String where;
        final Map<Integer, String> whereParams;

        public WhereInfo(String where, Map<Integer, String> whereParams) {
            super();
            this.where = where;
            this.whereParams = whereParams;
        }

        public void append(WhereInfo wi, String operation) {

            if (where.length() != 0) {
                where = where + " " + operation + " ";
            }
            where = where + wi.where;

            int pos = 1;
            while (true) {

                String param = wi.whereParams.get(pos);

                if (param == null) {
                    break;
                }

                this.whereParams.put(this.whereParams.size() + 1, param);

                pos++;
            }

        }

    }

    protected class QueryInfo {

        private final String select;

        private final Map<String, Field> selectFields;

        private final Map<String, String> columnDataBaseNames;

        private QueryInfo(String select, Map<String, Field> selectFields,
                Map<String, String> columnDataBaseNames) {
            super();
            this.select = select;
            this.selectFields = selectFields;
            this.columnDataBaseNames = columnDataBaseNames;
        }

        public String getSelect() {
            return select;
        }

        public Map<String, Field> getSelectFields() {
            return selectFields;
        }

        public Map<String, String> getColumnDataBaseNames() {
            return columnDataBaseNames;
        }

    }

    private static QueryInfo queryInfoInstance = null;

    protected QueryInfo getQueryInfo() {

        if (queryInfoInstance == null) {

            Map<String, Field> fields = this.getAvailableUserInfoFields();

            /**
             * Camps de UserInfo que excluim de la consulta de BBDD Excluim "socialNetworks"
             * i "attributes" ja que són Maps.
             */
            fields.remove("socialNetworks");
            fields.remove("attributes");

            StringBuilder str = new StringBuilder();

            Map<String, String> columnDataBaseNames = new HashMap<String, String>();

            for (String field : fields.keySet()) {

                String columnName = getProperty(DB_BASE_PROPERTIES + field + "_column");
                if (!empty(columnName)) {

                    // Per crear select
                    if (str.length() != 0) {
                        str.append(", ");
                    }
                    str.append(columnName + " AS " + field);

                    // Nom BBDD
                    columnDataBaseNames.put(field, columnName);
                }
            }
            queryInfoInstance = new QueryInfo(str.toString(), fields, columnDataBaseNames);
        }

        return queryInfoInstance;

    }

    /**
     * 
     * @param where
     * @param whereParams
     * @return
     * @throws Exception
     */
    private List<UserInfo> executeQuery(String where, Map<Integer, String> whereParams)
            throws Exception {

        QueryInfo queryInfo = getQueryInfo();

        final String userstable = getPropertyRequired(USERS_TABLE);

        final String query = "select " + queryInfo.getSelect() + " from " + userstable
                + ((where == null) ? "" : (" where " + where));

        // final boolean debug = isDebug();
        // if (debug) {
        // log.info("SELECT => " + query);
        // }

        Connection c = getConnection();
        List<UserInfo> users = new ArrayList<UserInfo>();
        try {
            PreparedStatement ps = c.prepareStatement(query);
            try {

                if (where != null && whereParams != null) {
                    for (Entry<Integer, String> e : whereParams.entrySet()) {
                        ps.setString(e.getKey(), e.getValue());
                        // if (debug) {
                        // log.info("Param[" + e.getKey() + "] => " + e.getValue());
                        // }
                    }
                }

                ResultSet rs = ps.executeQuery();
                try {
                    while (rs.next()) {

                        UserInfo info = new UserInfo();

                        for (String alias : queryInfo.getColumnDataBaseNames().keySet()) {

                            Field userInfoField = queryInfo.getSelectFields().get(alias);

                            if (alias.equals("gender")) {
                                String val = rs.getString(alias);
                                if (val == null) {
                                    info.setGender(null);
                                } else {
                                    val = val.toLowerCase();
                                    if (val.equals("true") || val.equals("1")
                                            || val.equals("male")) {
                                        info.setGender(Gender.MALE);
                                    } else {
                                        if (val.equals("false") || val.equals("0")
                                                || val.equals("female")) {
                                            info.setGender(Gender.FEMALE);
                                        } else {
                                            log.warn("Cannot process gender value: ]" + val + "[");
                                            info.setGender(Gender.UNKNOWN);
                                        }
                                    }
                                }

                            } else if (alias.equals("birthDate") || alias.equals("creationDate")) {
                                Date value = rs.getDate(alias);
                                userInfoField.set(info, value);
                            } else {
                                // Es un String
                                String value = rs.getString(alias);
                                userInfoField.setAccessible(true);
                                userInfoField.set(info, value);
                            }
                        }

                        users.add(info);
                    }
                } finally {
                    closeResultSet(rs);
                }
            } finally {
                closePreparedStatement(ps);
            }

        } finally {
            closeConnection(c);
        }

        return users;

    }

    protected boolean isDebug() {
        String debug = getProperty(DEBUG_PROPERTY, "false");
        return "true".equals(debug);
    }

    private static DataSource datasource = null;

    private static Boolean useJndi = null;

    protected Connection getConnection() throws Exception {

        if (useJndi == null) {

            String jndi = getProperty(DATABASE_JNDI);

            if (empty(jndi)) {
                // Usar comunicació JDBC

                getPropertyRequired(DATABASE_CONNECTION_URL);
                getPropertyRequired(DATABASE_CONNECTION_USERNAME);
                getPropertyRequired(DATABASE_CONNECTION_PASSWORD);

                useJndi = false;
            } else {
                // Usar Comunicacio JNDI
                useJndi = true;
            }

        }

        // es.caib.portafib.plugins.userinformation.database.

        Connection c;
        if (useJndi) {
            if (datasource == null) {
                String jndi = getPropertyRequired(DATABASE_JNDI);
                Context ctx = new InitialContext();
                datasource = (DataSource) ctx.lookup(jndi);
            }
            c = datasource.getConnection();
        } else {

            String url = getPropertyRequired(DATABASE_CONNECTION_URL);
            String username = getPropertyRequired(DATABASE_CONNECTION_USERNAME);
            String password = getPropertyRequired(DATABASE_CONNECTION_PASSWORD);

            c = DriverManager.getConnection(url, username, password);

        }

        return c;

    }

    protected void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (Exception e) {
        }
    }

    protected void closePreparedStatement(PreparedStatement ps) {
        try {
            ps.close();
        } catch (Exception e) {
        }
    }

    protected void closeConnection(Connection c) {
        try {
            c.close();
        } catch (Exception e) {
        }
    }

    @Override
    protected int getMinimumCharactersToSearch() {
        final int defaultValue = 3;
        String minStr = getProperty(MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY);
        try {
            if (minStr != null && minStr.trim().length() != 0) {
                return Integer.parseInt(minStr);
            }
        } catch (NumberFormatException e) {
            log.warn(
                    "Propietat " + this.getPropertyKeyBase() + MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY
                            + " ha de definir un sencer: " + e.getMessage(),
                    e);
        }
        return defaultValue;
    }

    protected int getMaxAllowedNumberOfResults() {
        final int defaultValue = 30;
        String minStr = getProperty(MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES);
        try {
            if (minStr != null && minStr.trim().length() != 0) {
                return Integer.parseInt(minStr);
            }
        } catch (NumberFormatException e) {
            log.warn("Propietat " + this.getPropertyKeyBase()
                    + MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES
                    + " ha de definir un sencer: " + e.getMessage(), e);
        }
        return defaultValue;
    }

    protected long executeCount(String where, Map<Integer, String> whereParams)
            throws Exception, SQLException {
        Connection c = null;
        try {
            String userstable = getPropertyRequired(USERS_TABLE);

            final String query = "select count(*) as total from " + userstable
                    + ((where == null) ? "" : (" where " + where));

            // TODO Llevar
            // log.info("QUERY = " + query);

            c = getConnection();
            PreparedStatement ps = null;
            try {
                ps = c.prepareStatement(query);

                if (where != null && whereParams != null) {
                    for (Entry<Integer, String> e : whereParams.entrySet()) {
                        ps.setString(e.getKey(), e.getValue());
                        // if (debug) {
                        // log.info("Param[" + e.getKey() + "] => " + e.getValue());
                        // }
                    }
                }

                ResultSet rs = null;
                try {
                    rs = ps.executeQuery();
                    rs.next();
                    return rs.getLong("total");
                } finally {
                    closeResultSet(rs);
                }
            } finally {
                closePreparedStatement(ps);
            }

        } finally {
            closeConnection(c);
        }
    }

    protected SearchUsersResult executePartialCommon(final String where,
            final Map<Integer, String> whereParams, String partialName, String partialValue)
            throws Exception, SQLException {

        if (!empty(partialName)) {
            SearchStatus ss = checkMinimumPartialString(partialValue, partialName);
            if (ss != null) {
                return new SearchUsersResult(ss);
            }
        }

        final int maxAllowed = getMaxAllowedNumberOfResults();

        long count = this.executeCount(where, whereParams);
        if (count > maxAllowed) {
            SearchStatus smax = errorMassaResultats(maxAllowed);
            return new SearchUsersResult(smax);
        }

        return new SearchUsersResult(executeQuery(where, whereParams));
    }


}
