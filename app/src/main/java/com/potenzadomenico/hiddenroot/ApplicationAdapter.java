package com.potenzadomenico.hiddenroot;
/**
 * Created by Domenico on 29/06/16.
 */
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class ApplicationAdapter extends ArrayAdapter<ApplicationInfoManager> {
    private SparseBooleanArray mSelectedItemsIds;
    private List<ApplicationInfoManager> appsList=null;
    private Context context;
    private PackageManager packageManager;

    public ApplicationAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfoManager> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfoManager getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //prendo le informazioni delle apps e prendo ciò che mi serve
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.my_sample_list, null);
        }

        ApplicationInfoManager applicationInfoManager = appsList.get(position);
        if (applicationInfoManager!=null){
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);

            appName.setText(applicationInfoManager.getAppName());
            packageName.setText(applicationInfoManager.getPackageName());
            try {
                iconview.setImageDrawable(applicationInfoManager.getIcon(context));
            } catch (PackageManager.NameNotFoundException e) {
                iconview.setImageDrawable(context.getResources().getDrawable(R.mipmap.app_icon));
            }
        }
        return view;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
}