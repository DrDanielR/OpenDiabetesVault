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
    
    private String name;
    
    private String parameters;
    
    private double positionX;
       
    private double positionY;

    public FilterNode(String name, double positionX, double positionY) {
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;                
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
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
