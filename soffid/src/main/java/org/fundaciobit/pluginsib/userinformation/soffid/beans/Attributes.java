
package org.fundaciobit.pluginsib.userinformation.soffid.beans;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 * 
 * @author anadal
 * 3 jul 2025 11:13:10
 */
@Generated("jsonschema2pojo")
public class Attributes {

    public static final String EXPEDIENT_CERTIFICAT = "EXPEDIENT CERTIFICAT";

    public static final String NIF = "NIF";
    public static final String E_MAIL_CONTACTE = "E-MAIL CONTACTE";

    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getExpedientCertificat() {
        return (String) this.additionalProperties.get(EXPEDIENT_CERTIFICAT);        
    }

    public String getNif() {
        return (String) this.additionalProperties.get(NIF);
    }

    public String getEMailcontacte() {
        return (String) this.additionalProperties.get(E_MAIL_CONTACTE);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Attributes.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
