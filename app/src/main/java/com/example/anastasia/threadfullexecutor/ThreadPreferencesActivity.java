package com.example.anastasia.threadfullexecutor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ThreadPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener   {

    public static  class  ThreadPreferencesFragment extends PreferenceFragment {
        @Override
        public  void onCreate(Bundle  savedInstanceState)  {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(),R.xml.threadpool_preferences,false);
            addPreferencesFromResource(R.xml.threadpool_preferences);


        }


    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Resources resources = getResources();
        String corepoolvalue = "";
        String maxpoolvalue = "";
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (key.equals(resources.getString(R.string.corepoolsize_preference))) {

            corepoolvalue = SP.getString(key,"");



        }
        else if (key.equals(resources.getString(R.string.maximumpoolsize_preference))) {
            maxpoolvalue = SP.getString(key,"");


        }
        else {

        }
        if (!corepoolvalue.isEmpty() || !maxpoolvalue.isEmpty()){
            if (!corepoolvalue.isEmpty()){
            int corepool = Integer.parseInt(corepoolvalue);
                if (corepool <= 0  ){
                    Toast.makeText(getApplicationContext(),"You entered invalid value for corepool size.Using default values instead",Toast.LENGTH_LONG).show();
                }

            }
            if (!maxpoolvalue.isEmpty()) {
                int maxpool = Integer.parseInt(maxpoolvalue);
                if (maxpool <= 0) {
                    Toast.makeText(getApplicationContext(), "You entered invalid value for maxpool size. Using default values instead", Toast.LENGTH_LONG).show();
                }
            }
            if (!maxpoolvalue.isEmpty() && !corepoolvalue.isEmpty()){
                int corepool = Integer.parseInt(corepoolvalue);
                int maxpool = Integer.parseInt(maxpoolvalue);
                if(maxpool < corepool){
                    Toast.makeText(getApplicationContext(), "Core pool should be smaller than maxpool", Toast.LENGTH_LONG).show();
                }
            }
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new ThreadPreferencesFragment()).commit();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        settings.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();



    }
}
