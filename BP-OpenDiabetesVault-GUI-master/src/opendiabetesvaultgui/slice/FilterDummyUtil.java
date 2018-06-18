/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import de.opendiabetes.vault.processing.filter.AndFilter;
import de.opendiabetes.vault.processing.filter.DateTimePointFilter;
import de.opendiabetes.vault.processing.filter.DateTimeSpanFilter;
import de.opendiabetes.vault.processing.filter.OrFilter;
import de.opendiabetes.vault.processing.filter.ThresholdFilter;
import de.opendiabetes.vault.processing.filter.TimePointFilter;
import de.opendiabetes.vault.processing.filter.TimeSpanFilter;
import de.opendiabetes.vault.processing.filter.TypeGroupFilter;
import de.opendiabetes.vault.processing.filter.VaultEntryTypeFilter;
import de.opendiabetes.vault.processing.filter.options.AndFilterOption;
import de.opendiabetes.vault.processing.filter.options.DateTimePointFilterOption;
import de.opendiabetes.vault.processing.filter.options.DateTimeSpanFilterOption;
import de.opendiabetes.vault.processing.filter.options.OrFilterOption;
import de.opendiabetes.vault.processing.filter.options.ThresholdFilterOption;
import de.opendiabetes.vault.processing.filter.options.TimePointFilterOption;
import de.opendiabetes.vault.processing.filter.options.TimeSpanFilterOption;
import de.opendiabetes.vault.processing.filter.options.TypeGroupFilterOption;
import de.opendiabetes.vault.processing.filter.options.VaultEntryTypeFilterOption;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Daniel
 */
public class FilterDummyUtil {

    private List<FilterAndOption> filterAndOptions;

    public FilterDummyUtil() {
        filterAndOptions = new ArrayList<>();
        
        //Combine Filter contains Filter list as parameter
        filterAndOptions.add(new FilterAndOption(new AndFilterOption(null), new AndFilter(new AndFilterOption(null))));
        filterAndOptions.add(new FilterAndOption(new OrFilterOption(null), new OrFilter(new OrFilterOption(null))));
        
        //normal Filter
        filterAndOptions.add(new FilterAndOption(new DateTimePointFilterOption(new Date(), 0), new DateTimePointFilter(new DateTimePointFilterOption(new Date(), 0))));
        filterAndOptions.add(new FilterAndOption(new DateTimeSpanFilterOption(new Date(),new Date()), new DateTimeSpanFilter(new DateTimeSpanFilterOption(new Date(),new Date()))));
        filterAndOptions.add(new FilterAndOption(new ThresholdFilterOption(0,0), new ThresholdFilter(new ThresholdFilterOption(0,0))));
        filterAndOptions.add(new FilterAndOption(new TimePointFilterOption(LocalTime.now(), 0), new TimePointFilter(new TimePointFilterOption(LocalTime.now(), 0))));
        filterAndOptions.add(new FilterAndOption(new TimeSpanFilterOption(LocalTime.now(), LocalTime.now()), new TimeSpanFilter(new TimeSpanFilterOption(LocalTime.now(), LocalTime.now()))));
        filterAndOptions.add(new FilterAndOption(new TypeGroupFilterOption(null), new TypeGroupFilter(new TypeGroupFilterOption(null))));
        filterAndOptions.add(new FilterAndOption(new VaultEntryTypeFilterOption(null), new VaultEntryTypeFilter(new VaultEntryTypeFilterOption(null))));
    }

    public List<String> getAllNotCombineFilters() {
        List<String> result = new ArrayList<>();
        
        for (FilterAndOption filterAndOption : filterAndOptions) {
            if(!filterAndOption.isCombine())
                result.add(filterAndOption.getName());
        }
        

        return result;
    }

    public List getCombineFilter() {
        List<String> result = new ArrayList<>();
        
        for (FilterAndOption filterAndOption : filterAndOptions) {
            if(filterAndOption.isCombine())
                result.add(filterAndOption.getName());
        }

        return result;
    }

    public Class[] getParametersFromName(String name) {
        Class[] result = new Class[0];
        
        for (FilterAndOption filterAndOption : filterAndOptions) {
            if(filterAndOption.getName().equals(name))
            {
                result = filterAndOption.getParameterTypes();
                
            }
                
        }
        
        return result;
    }
}

