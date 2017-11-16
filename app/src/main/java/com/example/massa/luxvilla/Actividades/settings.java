package com.example.massa.luxvilla.Actividades;

import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;

import com.example.massa.luxvilla.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new UserPreferenceFragment()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class UserPreferenceFragment extends PreferenceFragment {

        Preference preferenceclear, preferencesobre;
        SwitchPreference switchPreferencenightmode;
        CheckBoxPreference switchPreferencenotifications;
        SearchHistoryTable msearchHistoryTable;
        List<SearchItem> sugestions;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferenceclear=getPreferenceScreen().findPreference(getResources().getString(R.string.clearhistory));

            preferenceclear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    sugestions=new ArrayList<>();
                    sugestions.clear();
                    msearchHistoryTable = new SearchHistoryTable(getActivity());
                    msearchHistoryTable.clearDatabase();
                    Snackbar.make(getView(), "histórico de busca eliminado", Snackbar.LENGTH_LONG).show();
                    return true;
                }
            });

            switchPreferencenotifications=(CheckBoxPreference)getPreferenceScreen().findPreference(getResources().getString(R.string.notificaçãoes));
            switchPreferencenotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean switched = ((SwitchPreference) preference)
                            .isChecked();
                    if (switched){
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("todos");
                    }else{
                        FirebaseMessaging.getInstance().subscribeToTopic("todos");
                    }
                    return true;
                }
            });

            switchPreferencenightmode=(SwitchPreference) getPreferenceScreen().findPreference(getResources().getString(R.string.night_mode));
            switchPreferencenightmode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean switched = ((SwitchPreference) preference)
                            .isChecked();
                    if (switched){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    getActivity().onBackPressed();
                    return true;
                }
            });

            preferencesobre=getPreferenceScreen().findPreference(getResources().getString(R.string.sobre));
            preferencesobre.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(),Sobre.class));
                    return true;
                }
            });

        }

    }
}
