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
public class FilterDummyUtil {
    public static final int ANDFILTER = 1;
    private static final String AND_FILTER = "AndFilter";
    
    public static final int ORFILTER = 2;
    private static final String OR_FILTER = "OrFilter";
    
    public static final int TYPEFILTER = 3;
    private static final String TYPE_FILTER = "TypeFilter";
    
    public static final int TIMESTAMPFILTER = 4;    
    private static final String TIME_STAMP_FILTER = "TimeStampFilter";

    public static int getIdFromName(String name) {
        int result = 0;
         
        if(name.equals(AND_FILTER))
            result = ANDFILTER;
        else if(name.equals(OR_FILTER))
            result = ORFILTER;
        else if(name.equals(TYPE_FILTER))
            result = TYPEFILTER;
        else if(name.equals(TIME_STAMP_FILTER))
            result = TIMESTAMPFILTER;
        
        return result;
    }    
    
    public static String getNameFromId(int id)
    {
        String result =  "";
        if(id == ANDFILTER)
            result = AND_FILTER;
        else if(id == ORFILTER)
            result = OR_FILTER;
        else if(id == TYPEFILTER)
            result = TYPE_FILTER;
        else if(id == TIMESTAMPFILTER)
            result = TIME_STAMP_FILTER;
        
        return result;
    }            
    
    public static List<String> getAllFilters(){
        List<String> result = new ArrayList<>();
        
        result.add(AND_FILTER);
        result.add(OR_FILTER);
        result.add(TYPE_FILTER);
        result.add(TIME_STAMP_FILTER);
        
        return result;
    }
    
    public static boolean combinesFilter(String name)
    {
        boolean result = false;
        
        int id = getIdFromName(name);
        
        if(id == ANDFILTER || id == ORFILTER)
            result = true;
        
        return result;
        
    }
}
