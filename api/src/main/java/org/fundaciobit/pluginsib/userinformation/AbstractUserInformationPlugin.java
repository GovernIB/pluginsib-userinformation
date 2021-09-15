package org.fundaciobit.pluginsib.userinformation;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.fundaciobit.pluginsib.core.utils.AbstractPluginProperties;

/**
 * 
 * @author anadal
 *
 */
public abstract class AbstractUserInformationPlugin extends AbstractPluginProperties
        implements IUserInformationPlugin {

    protected final Logger log = Logger.getLogger(getClass());

    /**
     * @param propertyKeyBase
     */
    public AbstractUserInformationPlugin() {
        super();
    }

    /**
     * @param propertyKeyBase
     */
    public AbstractUserInformationPlugin(String propertyKeyBase) {
        super(propertyKeyBase);
    }

    /**
     * @param propertyKeyBase
     * @param properties
     */
    public AbstractUserInformationPlugin(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

    // =========================================================
    // =========================================================
    // ========================= UTILITATS =====================
    // =========================================================
    // =========================================================

    protected abstract int getMinimumCharactersToSearch();

    protected SearchStatus checkMinimumPartialString(String partialText, String field) {

        if (partialText == null || partialText.trim().length() == 0) {
            return errorCadenaDeCercaNullBuida(partialText);
        }

        final int minimumCharachtersToSearch = getMinimumCharactersToSearch();

        if (partialText.length() < minimumCharachtersToSearch) {
            return errorCadenaDeCercaMassaCurta(partialText.length(), minimumCharachtersToSearch,
                    field);
        }

        return null;
    }

    protected Map<String, Field> getAvailableUserInfoFields() {

        Map<String, Field> fieldsStr = new HashMap<String, Field>();
        Field[] fields = UserInfo.class.getDeclaredFields();
        for (Field field : fields) {
            fieldsStr.put(field.getName(), field);
        }
        return fieldsStr;
    }

    protected SearchStatus errorIntern(final String msg) {
        return new SearchStatus(SearchStatus.RESULT_CLIENT_ERROR, msg);
    }

    protected SearchStatus errorMassaResultats(final int maxAllowed) {
        return new SearchStatus(SearchStatus.RESULT_TOO_MANY_RESULTS_MATCH, "Massa resultats ("
                + maxAllowed + ") coincideixen amb el patró de cerca. Ajusti la cadena de cerca.");
    }

    protected SearchStatus errorCadenaDeCercaNullBuida(String searchString) {
        SearchStatus ss = new SearchStatus(SearchStatus.RESULT_PARTIAL_STRING_NULL_OR_EMPTY,
                "La cadena de cerca enviada és null o està buida: ]" + searchString + "[");
        // log.error("XYZ ZZZ", new Exception());
        return ss;
    }

    protected SearchStatus errorCadenaDeCercaMassaCurta(final float searchStringLen,
            final int minimumCharachtersToSearch, final String field) {
        SearchStatus ss = new SearchStatus(SearchStatus.RESULT_PARTIAL_STRING_TOO_SHORT,
                "Per iniciar la consulta es requereix un texte parcial de cerca amb una longitud mínima de "
                        + minimumCharachtersToSearch + " caràcters, però només s'han enviat "
                        + (int) searchStringLen + " caràcters per cerca al camp '" + field + "'");
        return ss;
    }

    protected boolean empty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public static class UserInfoComparator implements Comparator<UserInfo> {
        @Override
        public int compare(UserInfo o1, UserInfo o2) {
            return o1.getUsername().compareTo(o2.getUsername());
        }
    }

}
