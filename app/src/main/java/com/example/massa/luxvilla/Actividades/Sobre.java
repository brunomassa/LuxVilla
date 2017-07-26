package com.example.massa.luxvilla.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.massa.luxvilla.R;

public class Sobre extends AppCompatActivity {
    Toolbar barsobre;
    TextView textViewsobre;
    TextView textViewsobre2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewsobre=(TextView)findViewById(R.id.txtsobre);
        textViewsobre.setText("A imobiliária LuxVilla especializou-se na venda de imóveis de luxo em terras portuguesas. Começou a sua caminhada em 2003 na cidade do Porto, oferecendo aos seus compradores varias tipologias com vistas alternativas sobre aquela que é apelidada de cidade invicta. Em 2008, ano em que abrangemos a cidade de Braga, também conhecida por Bracara Augusta sempre seguindo ideais de qualidade e de excelência. Querendo cada vez mais, levar o seu nome a outros níveis 7 anos mais tarde , depois de Braga, chegamos à denominada “ Veneza portuguesa”, Aveiro, a cidade dos ovos moles, onde nos encontramos ainda em fase de expansão mas sempre com o objetivo de oferecer o melhor.");

        textViewsobre2=(TextView)findViewById(R.id.txtsobre2);
        textViewsobre2.setText("Ás primeiras semanas do decorrer de 2015 e de mãos dadas com a “Douro Azul”, iniciamos uma parceria. Em que, alternadamente, a equipa de vendas visita semanalmente os cruzeiros da mesma, onde apresenta os imoveis que temos à disposição daqueles que queiram usufruir do solo português.");
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
}
