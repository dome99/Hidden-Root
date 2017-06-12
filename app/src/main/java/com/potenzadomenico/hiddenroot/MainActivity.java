package com.potenzadomenico.hiddenroot;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domenico on 26/06/16.
 */

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button allOff;
    private Button allOn;
    private TextView noRoot;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //toolbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()){
            cl.getLogDialog().show();
        }
        List<String> permissions =new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_SUPERUSER);
        permissions.add(Manifest.permission.REQUEST_SUPERUSER);

        for(String permission: permissions){
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, CONTEXT_INCLUDE_CODE );
            }
        }

        final boolean isSU=SuRequest.CanRunRootCommand("su");
        final boolean isSI=SuRequest.CanRunRootCommand("si");

        //dichiaro i vari button presenti in content_main.xml
        button=(Button)findViewById(R.id.button);
        allOff=(Button)findViewById(R.id.butAllOff);
        allOn=(Button)findViewById(R.id.butAllOn);
        noRoot=(TextView)findViewById(R.id.text_error);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(isSU || isSI){
            noRoot.setVisibility(View.GONE);
            //chiamo il metodo CanRunRootCommand("su")/CanRunRootCommand("si")
            // della classe SuRequest per verificare eventuali errori
            if(isSU){
                allOn.setEnabled(false);
            }

            if(isSI){
                allOff.setEnabled(false);
            }
        }else{
            showMessage();
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //reindirizzo l'utente all'activity ExecutingApps
            if(isSU || isSI){
                startActivity(new Intent(MainActivity.this, ExecutingApps.class));
            }else{
                showMessage();
            }
            }
        });

        allOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(SuRequest.CanRunRootCommand("su")){
                boolean done=SuRequest.suIntoSi();
                if (done){
                    SuRequest.CanRunRootCommand("si");
                    Toast.makeText(MainActivity.this,
                            R.string.disabled_root, Toast.LENGTH_LONG).show();
                    allOff.setEnabled(false);
                    allOn.setEnabled(true);
                }
            }else{
                showMessage();
            }
            }
        });

        allOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SuRequest.CanRunRootCommand("si")){
                    boolean done=SuRequest.siIntoSu();
                    if (done){
                        SuRequest.CanRunRootCommand("su");
                        Toast.makeText(MainActivity.this,
                                R.string.enabled_root, Toast.LENGTH_LONG).show();
                        allOff.setEnabled(true);
                        allOn.setEnabled(false);
                    }
                }else{
                    showMessage();
                }
            }
        });
    }

    private void showMessage() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.message_error_title)
                .setMessage(R.string.message_error_text)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            /*case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;*/
            case R.id.action_donate:
                startActivity(new Intent(MainActivity.this, Donate.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
