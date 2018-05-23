/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import java.util.List;

/**
 *
 * @author Daniel
 */
public class FilterNode {
    
    private List<FilterNode> filterNodes;
    
    private List<String> Parameters;
    
    private boolean combineFilter;
    
    private double positionX;
       
    private double positionY;

    public FilterNode(double positionX, double positionY, boolean combineFilter) {
        this.positionX = positionX;
        this.positionY = positionY;        
        this.combineFilter = combineFilter;
    }

    public List<FilterNode> getFilterNodes() {
        return filterNodes;
    }

    public void setFilterNodes(List<FilterNode> filterNodes) {
        this.filterNodes = filterNodes;
    }

    public List<String> getParameters() {
        return Parameters;
    }

    public void setParameters(List<String> Parameters) {
        this.Parameters = Parameters;
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
