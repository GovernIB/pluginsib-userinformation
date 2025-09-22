
package org.fundaciobit.pluginsib.userinformation.soffid.beans;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
/**
 * 
 * @author anadal
 * 22 sept 2025 10:52:50
 */
@Generated("jsonschema2pojo")
public class Meta {

    private String location;
    private Links links;
    private String resourceType;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String tab) {
        StringBuilder sb = new StringBuilder();
        sb.append(Meta.class.getName()).append(' ').append('{').append('\n');
        sb.append(tab).append("\tlocation");
        sb.append('=');
        sb.append(((this.location == null) ? "<null>" : this.location));
        sb.append('\n');
        sb.append(tab).append("\tlinks");
        sb.append('=');
        sb.append(((this.links == null) ? "<null>\n" : this.links.toString(tab + "\t")));
        sb.append(tab).append("\tresourceType");
        sb.append('=');
        sb.append(((this.resourceType == null) ? "<null>" : this.resourceType));
        sb.append('\n');
        if ((this.additionalProperties != null) && !this.additionalProperties.isEmpty()) {
            sb.append(tab).append("\tadditionalProperties");
            sb.append('=');
            sb.append((this.additionalProperties));
            sb.append('\n');
        }

        sb.append(tab).append('}');
        sb.append('\n');
        return sb.toString();
    }
}
