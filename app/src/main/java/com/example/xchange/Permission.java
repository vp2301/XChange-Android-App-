package com.example.xchange;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class Permission {

    public static boolean validatePermission(String[] permissions, Activity activity, int requestCode){

        if (Build.VERSION.SDK_INT >= 23 ){

            List<String> listaPermission = new ArrayList<>();

            /*Scroll through past permissions,
            checking one by one
            * if you already have the permission released*/
            for ( String permission : permissions ){
                Boolean hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if ( !hasPermission ) listaPermission.add(permission);
            }

            /*If the list is empty, you do not need to request permission*/
            if ( listaPermission.isEmpty() ) return true;
            String[] newPermission = new String[ listaPermission.size() ];
            listaPermission.toArray( newPermission );

            //Request permission
            ActivityCompat.requestPermissions(activity, newPermission, requestCode );


        }

        return true;

    }

}
