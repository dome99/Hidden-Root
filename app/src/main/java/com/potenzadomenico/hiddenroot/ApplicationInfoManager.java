package com.potenzadomenico.hiddenroot;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domenico on 01/07/16.
 */

//classe che utilizzo per salvare le informazioni di una app
public class ApplicationInfoManager {
    String appName;
    String packageName;

    public ApplicationInfoManager() {}

    public ApplicationInfoManager(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public ApplicationInfoManager(ApplicationInfo applicationInfo, Context context) {
        PackageManager packageManager = context.getPackageManager();
        appName = applicationInfo.loadLabel(packageManager).toString();
        packageName = applicationInfo.packageName;
    }

    public ArrayList<ApplicationInfoManager> getApplicationInfoManagerList
                                            (List<ApplicationInfo> appsList, Context context){
        ArrayList<ApplicationInfoManager> applicationInfoManagerList=new ArrayList<>();
        for(int i=0; i<appsList.size(); i++){
            applicationInfoManagerList.add(new ApplicationInfoManager(appsList.get(i), context));
        }
        return applicationInfoManagerList;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getApplicationIcon(this.getPackageName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationInfoManager)) return false;

        ApplicationInfoManager that = (ApplicationInfoManager) o;

        return getPackageName().equals(that.getPackageName());

    }

    @Override
    public int hashCode() {
        int result = getAppName() != null ? getAppName().hashCode() : 0;
        result = 31 * result + (getPackageName() != null ? getPackageName().hashCode() : 0);
        return result;
    }
}
