package com.potenzadomenico.hiddenroot;

import java.util.Comparator;

/**
 * Created by Domenico on 01/04/17.
 */

//metodo di comparazione per ordinare per nome la lista delle apps
public class MyComparator implements Comparator<ApplicationInfoManager> {
    @Override
    public int compare(ApplicationInfoManager t1, ApplicationInfoManager t2) {
        int result=0;
        if(t1.getAppName().compareTo(t2.getAppName())>0){
            result=1;
        }else if(t1.getAppName().compareTo(t2.getAppName())<0){
            result=-1;
        }
        return result;
    }
}