/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.opendiabetes.vault.processing.filter.options.guibackend;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.container.VaultEntryTypeGroup;
import de.opendiabetes.vault.processing.DataSlicer;
import de.opendiabetes.vault.processing.VaultEntrySlicer;
import de.opendiabetes.vault.processing.filter.AndFilter;
import de.opendiabetes.vault.processing.filter.DateTimePointFilter;
import de.opendiabetes.vault.processing.filter.DateTimeSpanFilter;
import de.opendiabetes.vault.processing.filter.Filter;
import de.opendiabetes.vault.processing.filter.FilterResult;
import de.opendiabetes.vault.processing.filter.OrFilter;
import de.opendiabetes.vault.processing.filter.ThresholdFilter;
import de.opendiabetes.vault.processing.filter.TimePointFilter;
import de.opendiabetes.vault.processing.filter.TimeSpanFilter;
import de.opendiabetes.vault.processing.filter.TypeGroupFilter;
import de.opendiabetes.vault.processing.filter.VaultEntryTypeFilter;
import de.opendiabetes.vault.processing.filter.options.AndFilterOption;
import de.opendiabetes.vault.processing.filter.options.DateTimePointFilterOption;
import de.opendiabetes.vault.processing.filter.options.DateTimeSpanFilterOption;
import de.opendiabetes.vault.processing.filter.options.FilterOption;
import de.opendiabetes.vault.processing.filter.options.OrFilterOption;
import de.opendiabetes.vault.processing.filter.options.ThresholdFilterOption;
import de.opendiabetes.vault.processing.filter.options.TimePointFilterOption;
import de.opendiabetes.vault.processing.filter.options.TimeSpanFilterOption;
import de.opendiabetes.vault.processing.filter.options.TypeGroupFilterOption;
import de.opendiabetes.vault.processing.filter.options.VaultEntryTypeFilterOption;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel
 */
public class FilterManagementUtil {

    private List<FilterAndOption> filterAndOptions;

    public FilterManagementUtil() {
        filterAndOptions = new ArrayList<>();

        //Combine Filter contains Filter list as parameter
        filterAndOptions.add(new FilterAndOption(new AndFilterOption(null), new AndFilter(new AndFilterOption(null))));
        filterAndOptions.add(new FilterAndOption(new OrFilterOption(null), new OrFilter(new OrFilterOption(null))));

        //normal Filter
        filterAndOptions.add(new FilterAndOption(new DateTimePointFilterOption(new Date(), 0), new DateTimePointFilter(new DateTimePointFilterOption(new Date(), 0))));
        filterAndOptions.add(new FilterAndOption(new DateTimeSpanFilterOption(new Date(), new Date()), new DateTimeSpanFilter(new DateTimeSpanFilterOption(new Date(), new Date()))));
        filterAndOptions.add(new FilterAndOption(new ThresholdFilterOption(0, 0), new ThresholdFilter(new ThresholdFilterOption(0, 0))));
        filterAndOptions.add(new FilterAndOption(new TimePointFilterOption(LocalTime.now(), 0), new TimePointFilter(new TimePointFilterOption(LocalTime.now(), 0))));
        filterAndOptions.add(new FilterAndOption(new TimeSpanFilterOption(LocalTime.now(), LocalTime.now()), new TimeSpanFilter(new TimeSpanFilterOption(LocalTime.now(), LocalTime.now()))));
        filterAndOptions.add(new FilterAndOption(new TypeGroupFilterOption(null), new TypeGroupFilter(new TypeGroupFilterOption(null))));
        filterAndOptions.add(new FilterAndOption(new VaultEntryTypeFilterOption(null), new VaultEntryTypeFilter(new VaultEntryTypeFilterOption(null))));
    }

    public List<String> getAllNotCombineFilters() {
        List<String> result = new ArrayList<>();

        for (FilterAndOption filterAndOption : filterAndOptions) {
            if (!filterAndOption.isCombine()) {
                result.add(filterAndOption.getName());
            }
        }

        return result;
    }

    public List getCombineFilter() {
        List<String> result = new ArrayList<>();

        for (FilterAndOption filterAndOption : filterAndOptions) {
            if (filterAndOption.isCombine()) {
                result.add(filterAndOption.getName());
            }
        }

        return result;
    }

    public Map<String, Class> getParametersFromName(String name) {
        Map<String, Class> result = new HashMap<>();

        for (FilterAndOption filterAndOption : filterAndOptions) {
            if (filterAndOption.getName().equals(name)) {
                result = filterAndOption.getParameterAndType();
            }
        }

        return result;
    }

    public FilterAndOption getFilterAndOptionFromName(String name) {
        FilterAndOption result = null;

        for (FilterAndOption filterAndOption : filterAndOptions) {
            if (filterAndOption.getName().equals(name)) {
                result = filterAndOption;
            }
        }

        return result;
    }

    public List<Filter> combineFilters(List<String> combineFilters, List<List<FilterNode>> allColumnsFilterNodes) {
        List<Filter> result = new ArrayList<>();

        int i = 0;

        for (String combineFilter : combineFilters) {

            List<FilterNode> filterNodes = allColumnsFilterNodes.get(i);
            List<Filter> filtersForCombine = new ArrayList<>();

            for (FilterNode filterNode : filterNodes) {
                Filter tempFilter = getFilterFromFilterNode(filterNode, null);
                filtersForCombine.add(tempFilter);
            }

            Filter tempFilter = getFilterFromFilterNode(new FilterNode(combineFilter, 0), filtersForCombine);
            result.add(tempFilter);

            i++;
        }

        return result;
    }

    public FilterResult sliceVaultEntries(List<Filter> filters, List<VaultEntry> vaultEntries) {
        VaultEntrySlicer vaultEntrySlicer = new VaultEntrySlicer();

        vaultEntrySlicer.registerFilter(filters);

        FilterResult result = vaultEntrySlicer.sliceEntries(vaultEntries);

        return result;

    }

    private Filter getFilterFromFilterNode(FilterNode filterNode, List<Filter> filtersForCombine) {
        Filter result = null;
        FilterAndOption filterAndOption = getFilterAndOptionFromName(filterNode.getName());

        //CombineFilter
        if (filterAndOption.getFilterOptionName().equals(AndFilterOption.class.getSimpleName())) {
            result = new OrFilter(new OrFilterOption(filtersForCombine));
        } else if (filterAndOption.getFilterOptionName().equals(OrFilterOption.class.getSimpleName())) {
            result = new AndFilter(new AndFilterOption(filtersForCombine));
        }//NonCombineFilter
        else if (filterAndOption.getFilterOptionName().equals(DateTimePointFilterOption.class.getSimpleName())) {
            result = new DateTimePointFilter(new DateTimePointFilterOption(new Date(filterNode.getParameterAndValues().get("DateTimePoint")), Integer.parseInt(filterNode.getParameterAndValues().get("MarginInMinutes").trim())));
        } else if (filterAndOption.getFilterOptionName().equals(DateTimeSpanFilterOption.class.getSimpleName())) {
            result = new DateTimeSpanFilter(new DateTimeSpanFilterOption(new Date(filterNode.getParameterAndValues().get("StartTime")), new Date(filterNode.getParameterAndValues().get("EndTime"))));
        } else if (filterAndOption.getFilterOptionName().equals(ThresholdFilterOption.class.getSimpleName())) {
            result = new ThresholdFilter(new ThresholdFilterOption(Integer.parseInt(filterNode.getParameterAndValues().get("MinThreshold").trim()), Integer.parseInt(filterNode.getParameterAndValues().get("MaxThreshold").trim()), Integer.parseInt(filterNode.getParameterAndValues().get("Mode").trim())));
        } else if (filterAndOption.getFilterOptionName().equals(TimePointFilterOption.class.getSimpleName())) {
            result = new TimePointFilter(new TimePointFilterOption(LocalTime.parse(filterNode.getParameterAndValues().get("LocalTime")), Integer.parseInt(filterNode.getParameterAndValues().get("MarginInMinutes").trim())));
        } else if (filterAndOption.getFilterOptionName().equals(TimeSpanFilterOption.class.getSimpleName())) {
            result = new TimeSpanFilter(new TimeSpanFilterOption(LocalTime.parse(filterNode.getParameterAndValues().get("StartTime")), LocalTime.parse(filterNode.getParameterAndValues().get("EndTime"))));
        } else if (filterAndOption.getFilterOptionName().equals(TypeGroupFilterOption.class.getSimpleName())) {
            result = new TypeGroupFilter(new TypeGroupFilterOption(VaultEntryTypeGroup.valueOf(filterNode.getParameterAndValues().get("VaultEntryTypeGroup"))));
        } else if (filterAndOption.getFilterOptionName().equals(VaultEntryTypeFilterOption.class.getSimpleName())) {
            result = new VaultEntryTypeFilter(new VaultEntryTypeFilterOption(VaultEntryType.valueOf(filterNode.getParameterAndValues().get("VaultEntryType"))));
            result = new VaultEntryTypeFilter(new VaultEntryTypeFilterOption(VaultEntryType.BASAL_MANUAL));
        }

        return result;
    }
}
