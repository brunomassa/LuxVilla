package com.example.massa.luxvilla.Actividades;

import android.content.SharedPreferences;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.sugestoes.SearchSugestionsProvider;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    Toolbar barra;
    RelativeLayout limparhistorico;
    Switch notificaoes;
    TextView textViewinfo;
    SharedPreferences sharedPreferences;
    final String NOTIFICATION="notificationsenabled";
    SharedPreferences.Editor editor;
    SearchHistoryTable msearchHistoryTable;
    List<SearchItem> sugestions;
    RelativeLayout rlnotificacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        barra=(Toolbar)findViewById(R.id.brcimadefenicoes);
        barra.setTitle("Defenições");
        barra.setTitleTextColor(getResources().getColor(android.R.color.white));
        barra.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        setSupportActionBar(barra);
        barra.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        limparhistorico=(RelativeLayout)findViewById(R.id.lmphistorico);
        limparhistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sugestions=new ArrayList<SearchItem>();
                sugestions.clear();
                msearchHistoryTable = new SearchHistoryTable(SettingsActivity.this);
                msearchHistoryTable.clearDatabase();
                Snackbar.make(limparhistorico, "histórico de busca eliminado", Snackbar.LENGTH_LONG).show();
            }
        });

        rlnotificacoes=(RelativeLayout)findViewById(R.id.rlnotifications);
        notificaoes=(Switch)findViewById(R.id.switch1);
        sharedPreferences=getSharedPreferences(NOTIFICATION, 0);
        int flagenabled=sharedPreferences.getInt("enabled",0);
        if (flagenabled==0){
            notificaoes.setChecked(true);
        }else {
            notificaoes.setChecked(false);
        }
        rlnotificacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificaoes.isChecked()){
                    notificaoes.setChecked(false);
                    sharedPreferences=getSharedPreferences(NOTIFICATION, 0);
                    editor=sharedPreferences.edit();
                    editor.putInt("enabled", 1);
                    editor.apply();
                }else {
                    notificaoes.setChecked(true);
                    sharedPreferences=getSharedPreferences(NOTIFICATION, 0);
                    editor=sharedPreferences.edit();
                    editor.putInt("enabled", 0);
                    editor.apply();
                }
            }
        });

        textViewinfo=(TextView)findViewById(R.id.txtinfo);
        textViewinfo.setText("A imobiliária LuxVilla especializou-se na venda de imóveis de luxo em terras portuguesas. Começou a sua caminhada em 2003 na cidade do Porto, oferecendo aos seus compradores varias tipologias com vistas alternativas sobre aquela que é apelidada de cidade invicta. Em 2008, ano em que abrangemos a cidade de Braga, também conhecida por Bracara Augusta sempre seguindo ideais de qualidade e de excelência. Querendo cada vez mais, levar o seu nome a outros níveis 7 anos mais tarde , depois de Braga, chegamos à denominada “ Veneza portuguesa”, Aveiro, a cidade dos ovos moles, onde nos encontramos ainda em fase de expansão mas sempre com o objetivo de oferecer o melhor.");
    }
}
