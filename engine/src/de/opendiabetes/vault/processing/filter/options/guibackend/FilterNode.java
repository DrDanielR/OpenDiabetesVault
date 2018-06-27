/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.opendiabetes.vault.processing.filter.options.guibackend;

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

    public FilterNode(String name, int columnNumber) {
        this.name = name;
        this.columnNumber = columnNumber;
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

}
