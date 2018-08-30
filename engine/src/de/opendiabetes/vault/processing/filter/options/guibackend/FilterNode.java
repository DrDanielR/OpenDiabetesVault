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

    private Map<String, String> parameterAndValues;
    private int columnNumber;
    private List<Filter> filters;
    private List<VaultEntry> data;
    private List<FilterNode> filterNodes;

    public FilterNode(String name, int columnNumber) {
        this.name = name;
        this.columnNumber = columnNumber;
        parameterAndValues = new HashMap<>();
    }

    public FilterNode(String name, List<Filter> filters) {
        this.name = name;
        this.filters = filters;
        columnNumber = 0;
        parameterAndValues = new HashMap<>();
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

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
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

    public void setFilterNodes(ArrayList<FilterNode> filterNodes) {
        this.filterNodes = filterNodes;
    }

    public List<FilterNode> getFilterNodes() {
        return filterNodes;
    }

}
