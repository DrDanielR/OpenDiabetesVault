/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.opendiabetes.vault.processing.filter.options.guibackend;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.processing.filter.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel
 */
public class FilterNode {

    private String name;

    private Map<String, String> parameterAndValues = new HashMap<>();
    ;
    private Map<String, List<FilterNode>> parameterAndFilterNodes = new HashMap<>();
    private List<Filter> filters;
    private List<VaultEntry> data;

    public FilterNode(String name) {
        this.name = name;
    }

    public FilterNode(String name, List<Filter> filters) {
        this.name = name;
        this.filters = filters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addParam(String type, String value) {
        parameterAndValues.put(type, value);
    }

    public Map<String, String> getParameterAndValues() {
        return parameterAndValues;
    }

    public void setParameterAndValue(Map<String, String> parameterAndValue) {
        this.parameterAndValues = parameterAndValue;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public void setData(List<VaultEntry> data) {
        this.data = data;
    }

    public List<VaultEntry> getData() {
        return data;
    }

    public void setParameterAndFilterNodes(String name, List<FilterNode> filterNodes) {
        parameterAndFilterNodes.put(name, filterNodes);
    }

    public void addParameterAndFilterNodes(String name, FilterNode filterNode) {
        if (parameterAndFilterNodes.get(name) == null) {
            List<FilterNode> filterNodes = new ArrayList<>();
            filterNodes.add(filterNode);
            parameterAndFilterNodes.put(name, filterNodes);
        } else {
            parameterAndFilterNodes.get(name).add(filterNode);
        }
    }

    public List<FilterNode> getParameterAndFilterNodesFromName(String name) {
        return parameterAndFilterNodes.get(name);
    }

    public Map<String, List<FilterNode>> getParameterAndFilterNodes() {
        return parameterAndFilterNodes;
    }

}
