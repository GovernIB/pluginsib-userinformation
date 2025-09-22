
package org.fundaciobit.pluginsib.userinformation.soffid.beans;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author anadal
 * 22 sept 2025 10:52:40
 */
@Generated("jsonschema2pojo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoffidUserResults {

    private List<String> schemas;
    private Integer totalResults;
    private Integer startIndex;
    private Integer itemsPerPage;

    @JsonProperty("Resources")
    @javax.xml.bind.annotation.XmlElement(name = "Resources")
    private List<Resource> Resources;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public List<Resource> getResources() {
        return Resources;
    }

    public void setResources(List<Resource> resources) {
        this.Resources = resources;
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
        sb.append(SoffidUserResults.class.getName()).append(' ').append("{\n\t");
        sb.append("schemas");
        sb.append('=');
        sb.append(((this.schemas == null) ? "<null>" : this.schemas));
        sb.append("\n\t");
        sb.append("totalResults");
        sb.append('=');
        sb.append(((this.totalResults == null) ? "<null>" : this.totalResults));
        sb.append("\n\t");
        sb.append("startIndex");
        sb.append('=');
        sb.append(((this.startIndex == null) ? "<null>" : this.startIndex));
        sb.append("\n");
        
        sb.append("itemsPerPage");
        sb.append('=');
        sb.append(((this.itemsPerPage == null) ? "<null>" : this.itemsPerPage));
        sb.append("\n");
        
        sb.append("\tresources");
        sb.append('=');
        sb.append(((this.Resources == null) ? "<null>" : printResources(this.Resources, tab + "\t")));
        sb.append("\n");
        if ((this.additionalProperties != null && !this.additionalProperties.isEmpty())) {
            sb.append("\tadditionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
            sb.append("\n");
        }

        sb.append('}');

        sb.append("\n");
        return sb.toString();
    }

    public String printResources(List<Resource> resources, String tab) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[\n");
        int count = 0;
        for (Resource resource : resources) {

            sb.append(tab).append("\t").append("-------------- RESOURCES[" + count + "] ---------------\n");
            sb.append(resource.toString(tab + "\t"));
            count++;
        }
        sb.append(tab).append("]");
        return sb.toString();
    }
}
