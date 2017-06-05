package com.potenzadomenico.hiddenroot;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Domenico on 05/07/16.
 */
public class SuRequest {
    private static ArrayList<String> PHONE_DIRECTORIES=new ArrayList<>();

    //vettore possibili posizioni root
    private static final String[] DEFAULT_DIRECTORIES={
            "/sbin/",
            "system/bin/","system/xbin/",
            "system/sd/xbin/","system/bin/failsafe/",
            "data/local/bin/","data/local/xbin/","data/local/"
    };

    //metodo di verifica dell'esistenza del file su e salvataggio nell'altro vettore
    private static boolean checkExist(String file) {
        boolean exist=false;
        for(int i=0; i<DEFAULT_DIRECTORIES.length-1; i++){
            if(new File(DEFAULT_DIRECTORIES[i]+file).exists()){
                PHONE_DIRECTORIES.add(DEFAULT_DIRECTORIES[i]);
                exist=true;
            }
        }
        return exist;
    }

    //riesco ad ottenere i permessi di root?
    public static boolean CanRunRootCommand(String cmd){
        boolean retval = false;
        Process suProcess;

        try{
            suProcess = Runtime.getRuntime().exec(cmd);

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

            if (os!=null  && osRes!=null){
                // Getting the id of the current user to check if this is root
                os.writeBytes("id\n");
                os.flush();

                String currUid = osRes.readLine();
                boolean exitSu = false;
                if(currUid==null){
                    retval = false;
                    exitSu = false;
                    Log.d("ROOT", "Can't get root access or denied by user");
                }else if(currUid.contains("uid=0")){
                    retval = true;
                    exitSu = true;
                    Log.d("ROOT", "Root access granted");
                }else{
                    retval = false;
                    exitSu = true;
                    Log.d("ROOT", "Root access rejected: " + currUid);
                }

                if (exitSu){
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        }
        catch (Exception e){
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os)
            // after su failed, meaning that the device is not rooted
            retval = false;
            Log.d("ROOT", "Root access rejected ["
                    + e.getClass().getName() + "] : " + e.getMessage());
        }
        return retval;
    }

    //rinomino file su in si
    protected static boolean suIntoSi(){
        boolean retval = false;
        //String[] command = {"su", "mv", "/system/xbin/su","/system/xbin/si"};
        Process suProcess;

        if(PHONE_DIRECTORIES.isEmpty()){
            checkExist("su");
        }

        try{
            //suProcess = Runtime.getRuntime().exec("su mv /system/xbin/su /system/xbin/si");
            suProcess = Runtime.getRuntime().exec("su");

            for(int i=0; i<PHONE_DIRECTORIES.size(); i++){
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
                os.writeBytes("mount -o remount,rw /\n");
                os.writeBytes("mv "+PHONE_DIRECTORIES.get(i)+"su "+PHONE_DIRECTORIES.get(i)+"si\n");
                os.writeBytes("exit\n");
                os.flush();
            }
            suProcess.waitFor();
            retval=CanRunRootCommand("si");
        }
        catch (Exception e){
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os)
            // after su failed, meaning that the device is not rooted
            retval = false;
            Log.d("STATE", "Cannot rename file su into si");
        }
        return retval;
    }

    //rinomino file si in su
    protected static boolean siIntoSu(){
        boolean retval = false;
        //String[] command = {"si", "mv", "/system/xbin/si","/system/xbin/su"};
        Process suProcess;

        if(PHONE_DIRECTORIES.isEmpty()){
            checkExist("si");
        }

        try{
            //suProcess = Runtime.getRuntime().exec("su mv /system/xbin/si /system/xbin/su");
            suProcess = Runtime.getRuntime().exec("si");

            for(int i=0; i<PHONE_DIRECTORIES.size(); i++) {
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
                os.writeBytes("mount -o remount,rw /\n");
                os.writeBytes("mv "+PHONE_DIRECTORIES.get(i)+"si "+PHONE_DIRECTORIES.get(i)+"su\n");
                Log.d("LEGGI", PHONE_DIRECTORIES.get(i));
                os.writeBytes("exit\n");
                os.flush();
            }
            suProcess.waitFor();
            retval=CanRunRootCommand("su");
        }
        catch (Exception e){
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os)
            // after su failed, meaning that the device is not rooted
            retval = false;
            Log.d("STATE", "Cannot rename file su into si");
        }
        return retval;
    }
}
