/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel
 */
public class FilterNode {
    
    private List<FilterNode> filterNodes;
    
    private String name;
    
    private String parameters;
    
    private boolean combineFilter;
    
    private double positionX;
       
    private double positionY;

    public FilterNode(String name, double positionX, double positionY, boolean combineFilter) {
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;        
        this.combineFilter = combineFilter;
        filterNodes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterNode> getFilterNodes() {
        return filterNodes;
    }

    public void setFilterNodes(List<FilterNode> filterNodes) {
        this.filterNodes = filterNodes;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public boolean isCombineFilter() {
        return combineFilter;
    }

    public void setCombineFilter(boolean combineFilter) {
        this.combineFilter = combineFilter;
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
    
}
