/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import de.opendiabetes.vault.processing.filter.Filter;
import de.opendiabetes.vault.processing.filter.options.FilterOption;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel
 */
public class FilterAndOption {
    
    private String filterClassName;
    private String filterOptionClassName;
    private boolean isCombine;
    private final FilterOption option;
    private final Filter filter;
    private Class[] parameterTypes;

    public FilterAndOption(FilterOption option, Filter filter) {        
        this.option = option;
        this.filter = filter;
        
        initializeParameters();
    }
    
    public String getName() {
        return filterClassName;
    }
    
    public boolean isCombine() {
        return isCombine;
    }
    

    public String getFilterOptionClassName() {
        return filterOptionClassName;
    }

    public FilterOption getOption() {
        return option;
    }

    public Filter getFilter() {
        return filter;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    private void initializeParameters() {
        filterClassName = filter.getClass().getSimpleName();
        filterOptionClassName = option.getClass().getSimpleName();
        
        Constructor<?>[] constructors = option.getClass().getConstructors();
        
        if(constructors.length > 0)
        {
            Constructor constructor= constructors[0];
            parameterTypes = constructor.getParameterTypes();
            
            for (Class clazz : parameterTypes) {
                if(clazz.getSimpleName().contains("List"))
                   isCombine = true; 
            }
        }
    }
    
    
    
}
