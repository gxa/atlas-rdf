package uk.ac.ebi.spot.rdf.model.baseline;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

public class BaselineExperimentConfiguration {

    private XMLConfiguration config;

    public BaselineExperimentConfiguration(XMLConfiguration config) {
        this.config = config;
    }

    public String getExperimentDisplayName() {
        return config.getString("landingPageDisplayName");
    }

    private List<String> getListOfStrings(List<Object> objects) {
        List<String> result = newArrayList();
        for (Object object : objects) {
            result.add(object != null ? object.toString() : null);
        }
        return result;
    }

    public List<String> getDataProviderURL() {
        return getListOfStrings(config.getList("dataProviderURL"));
    }

    public List<String> getDataProviderDescription() {
        return getListOfStrings(config.getList("dataProviderDescription"));
    }

    public Set<Factor> getDefaultFilterFactors() {

        Set<Factor> defaultFilterFactors = new HashSet<>();
        List<HierarchicalConfiguration> fields =
                config.configurationsAt("defaultFilterFactors.filterFactor");
        for (HierarchicalConfiguration sub : fields) {
            String factorType = sub.getString("type");
            String factorValue = sub.getString("value");
            defaultFilterFactors.add(new Factor(factorType, factorValue));
        }

        return defaultFilterFactors;
    }

    public String getDefaultQueryFactorType() {

        String defaultQueryFactorType = config.getString("defaultQueryFactorType");
        if (defaultQueryFactorType == null || defaultQueryFactorType.trim().length() == 0) {
            throw new IllegalStateException("No defaultQueryFactorType found in factors file.");
        }

        return defaultQueryFactorType;
    }

    public boolean orderCurated() {
        return "curated".equals(config.getString("orderFactor"));
    }

    public Set<String> getMenuFilterFactorTypes() {

        Set<String> results = new HashSet<>();
        List<Object> menuFilterFactorTypes = config.getList("menuFilterFactorTypes");
        for (Object o : menuFilterFactorTypes) {
            String filterFactorType = (String) o;
            if (filterFactorType.trim().length() > 0) {
                results.add(filterFactorType);
            }
        }

        return results;
    }

    public Map<String, String> getSpeciesMapping() {

        Map<String, String> mapping = new HashMap<>();
        List<HierarchicalConfiguration> fields =
                config.configurationsAt("speciesMapping.mapping");
        for (HierarchicalConfiguration sub : fields) {
            String samples = sub.getString("samples").toLowerCase();
            String genes = sub.getString("genes").toLowerCase();
            mapping.put(samples, genes);
        }

        return mapping;
    }

    public List<String> getAlternativeViews() {
        List<String> result = new ArrayList<>();
        for(Object o:  config.getList("alternativeView")){
            if(o.toString().matches("E-\\w+-\\d+")){
                result.add(o.toString());
            }
        }

        return result;
    }
    public String disclaimer(){
        if("true".equals(config.getString("fortLauderdale"))){
            return "fortLauderdale";
        } else if (StringUtils.isNotEmpty(config.getString("disclaimer"))){
            return config.getString("disclaimer");
        } else {
            return "";
        }
    }

    public boolean isFortLauderdale() {
        return "true".equals(config.getString("fortLauderdale"));
    }
}

