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
import java.util.Iterator;
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
    private Map<String, Class> parameterAndType;    

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
    
    public Map<String, Class> getParameterAndType() {
        return parameterAndType;
    }

    public void setParameterAndType(Map<String, Class> parameterAndType) {
        this.parameterAndType = parameterAndType;
    }

    private void initializeParameters() {
        filterClassName = filter.getClass().getSimpleName();
        filterOptionClassName = option.getClass().getSimpleName();
        parameterAndType = option.getParameterNameAndType();

        Constructor<?>[] constructors = option.getClass().getConstructors();

        Iterator it = parameterAndType.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            
            if (((Class) pair.getValue()).getSimpleName().contains("List")) {
                isCombine = true;
            }
            //it.remove();
        }
    }

}
