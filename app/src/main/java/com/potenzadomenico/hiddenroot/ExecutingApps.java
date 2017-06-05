package com.potenzadomenico.hiddenroot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by Domenico on 29/06/16.
 */
public class ExecutingApps extends AppCompatActivity{

    private PackageManager packageManager = null;
    private List<ApplicationInfoManager> applist = null;
    private ApplicationAdapter listadaptor = null;
    private SparseBooleanArray sparseBooleanArray;
    private Button button;
    private ListView listView;
    private ApplicationInfoManager selected=null;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //toolbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executing_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            //se il device permette viene creato il tasto '<-' nella toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        listView=(ListView)findViewById(android.R.id.list);
        button=(Button)findViewById(R.id.button);

        packageManager = getPackageManager();
        //carico tutte le app presenti nel device (vedi sotto classe LoadApplications())
        new LoadApplications().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //evento selezione di una app
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected= (ApplicationInfoManager) adapterView.getItemAtPosition(i);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected!=null){
                    //chiedo il permesso di root come 'su' e poi cambio il file 'su' in 'si'
                    SuRequest.CanRunRootCommand("su");
                    boolean done=SuRequest.suIntoSi();
                    //chiedo accesso root come 'si'
                    SuRequest.CanRunRootCommand("si");
                    if(done){
                            try {
                                //lancio l'app selezionata
                                Intent intent = packageManager.getLaunchIntentForPackage(
                                                                selected.getPackageName());
                                intent.setFlags(0);

                                // ancora da elaborare

                                startActivityForResult(intent, 0);
                                Toast.makeText(ExecutingApps.this, R.string.disabled_root,
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ExecutingApps.this, getResources().getString(R.string.not_runnable),
                                        Toast.LENGTH_LONG).show();
                            }

                    }
                } else{
                    Toast.makeText(ExecutingApps.this, getResources().getString(R.string.not_selected),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("STATE", "onActivityResult, requestCode: " + requestCode
                                        + ", resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK || resultCode==Activity.RESULT_CANCELED){
            //al termine il file 'si' verrà rinominato in 'su'
            SuRequest.siIntoSu();
            SuRequest.CanRunRootCommand("su");
            Toast.makeText(ExecutingApps.this, R.string.enabled_root, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //gestione dell'evento del clic del tasto '<-' nella toolbar
        //che eseguirà la main activity
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ExecutingApps.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            /*  genero in background la lista delle app e le carico
            *   secondo il mio ApplicationAdapter
            *   e anche al file list_selector.xml
            */

            ApplicationInfoManager applicationInfoManager=new ApplicationInfoManager();
            /*applist = applicationInfoManager.getApplicationInfoManagerList(packageManager,
       packageManager.getInstalledApplications(PackageManager.GET_META_DATA), ExecutingApps.this);*/

            List<ApplicationInfo> l=packageManager.getInstalledApplications(
                                                    PackageManager.GET_META_DATA);
            List<ApplicationInfo> lt=new ArrayList<>();

            for (ApplicationInfo app: l) {
                if (packageManager.getLaunchIntentForPackage(app.packageName) != null) {
                    // apps with launcher intent
                    if (!((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1 ||
                            (app.flags & ApplicationInfo.FLAG_SYSTEM) == 1)) {
                        //not updated system apps or system apps
                        lt.add(app);
                    }
                }
            }

            applist = applicationInfoManager.getApplicationInfoManagerList(lt, ExecutingApps.this);

            Collections.sort(applist, new MyComparator());

            l.clear();
            lt.clear();

            listadaptor = new ApplicationAdapter(ExecutingApps.this,
                    R.layout.my_sample_list, applist);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            //visualizzo la lista delle apps
            listView.setAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            /*  mentre genera la lista delle app
            *   gli mostro un messaggio di caricamento
            */
            progress = ProgressDialog.show(ExecutingApps.this, null,
                    getResources().getString(R.string.load_all));
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
