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

    private Map<String, String> parameterAndValue;
    private int columnNumber;

    public FilterNode(String name, int columnNumber) {
        this.name = name;
        this.columnNumber = columnNumber;
        parameterAndValue = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addParam(String type, String value) {
        parameterAndValue.put(type, value);
    }

    public Map<String, String> getParameterAndValues() {
        return parameterAndValue;
    }

    public Map<String, String> getParameterAndValue() {
        return parameterAndValue;
    }

    public void setParameterAndValue(Map<String, String> parameterAndValue) {
        this.parameterAndValue = parameterAndValue;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

}
