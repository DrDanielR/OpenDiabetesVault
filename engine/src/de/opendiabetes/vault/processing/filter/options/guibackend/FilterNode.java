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
    
    private double positionX;
       
    private double positionY;
    
    private Map<String, String> parameterAndValue;

    public FilterNode(String name, double positionX, double positionY) {
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;     
        parameterAndValue = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }    
    
    public void addParam(String type, String value){
        parameterAndValue.put(type, value);
    }
    
    public Map<String, String> getParameterAndValues()
    {
        return parameterAndValue;
    }
    
}
