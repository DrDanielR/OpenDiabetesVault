/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import de.opendiabetes.vault.processing.filter.options.FilterOption;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Daniel
 */
public class FilterDummyUtil {

    public static final int ANDFILTER = 1;
    public static final String ANDFILTERPARAMETERS = null;
    private static final String AND_FILTER = "AndFilter";

    public static final int ORFILTER = 2;
    public static final String ORFILTERPARAMETERS = null;
    private static final String OR_FILTER = "OrFilter";

    public static final int TYPEFILTER = 3;
    public static final String TYPEFILTERPARAMETERS = "type:";
    private static final String TYPE_FILTER = "TypeFilter";

    public static final int TIMESTAMPFILTER = 4;
    public static final String TIMESTAMPFILTERPARAMETERS = "from: ;to:";
    private static final String TIME_STAMP_FILTER = "TimeStampFilter";

    public static int getIdFromName(String name) {
        int result = 0;

        if (name.equals(AND_FILTER)) {
            result = ANDFILTER;
        } else if (name.equals(OR_FILTER)) {
            result = ORFILTER;
        } else if (name.equals(TYPE_FILTER)) {
            result = TYPEFILTER;
        } else if (name.equals(TIME_STAMP_FILTER)) {
            result = TIMESTAMPFILTER;
        }

        return result;
    }

    public static String getParametersFromName(String name) {
        String result = null;

        if (name.equals(AND_FILTER)) {
            result = ANDFILTERPARAMETERS;
        } else if (name.equals(OR_FILTER)) {
            result = ORFILTERPARAMETERS;
        } else if (name.equals(TYPE_FILTER)) {
            result = TYPEFILTERPARAMETERS;
        } else if (name.equals(TIME_STAMP_FILTER)) {
            result = TIMESTAMPFILTERPARAMETERS;
        }

        return result;
    }

    public static String getNameFromId(int id) {
        String result = "";
        if (id == ANDFILTER) {
            result = AND_FILTER;
        } else if (id == ORFILTER) {
            result = OR_FILTER;
        } else if (id == TYPEFILTER) {
            result = TYPE_FILTER;
        } else if (id == TIMESTAMPFILTER) {
            result = TIME_STAMP_FILTER;
        }

        return result;
    }

    public static List<String> getAllNotCombineFilters() {
        List<String> result = new ArrayList<>();

        result.add(TYPE_FILTER);
        result.add(TIME_STAMP_FILTER);

        return result;
    }

    public static List getCombineFilter() {
        List<String> result = new ArrayList();
        
        result.add(AND_FILTER);
        result.add(OR_FILTER);
        
        return result;
    }
}

