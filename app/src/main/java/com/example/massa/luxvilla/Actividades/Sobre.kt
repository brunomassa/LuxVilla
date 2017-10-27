package com.example.massa.luxvilla.Actividades

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.massa.luxvilla.R
import kotlinx.android.synthetic.main.activity_sobre.*

/**
 * Created by massa on 27/10/2017.
 */
class Sobre : AppCompatActivity() {
    internal var barsobre: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        txtsobre.text = "A imobiliária LuxVilla especializou-se na venda de imóveis de luxo em terras portuguesas. Começou a sua caminhada em 2003 na cidade do Porto, oferecendo aos seus compradores varias tipologias com vistas alternativas sobre aquela que é apelidada de cidade invicta. Em 2008, ano em que abrangemos a cidade de Braga, também conhecida por Bracara Augusta sempre seguindo ideais de qualidade e de excelência. Querendo cada vez mais, levar o seu nome a outros níveis 7 anos mais tarde , depois de Braga, chegamos à denominada “ Veneza portuguesa”, Aveiro, a cidade dos ovos moles, onde nos encontramos ainda em fase de expansão mas sempre com o objetivo de oferecer o melhor."

        txtsobre2.text = "Ás primeiras semanas do decorrer de 2015 e de mãos dadas com a “Douro Azul”, iniciamos uma parceria. Em que, alternadamente, a equipa de vendas visita semanalmente os cruzeiros da mesma, onde apresenta os imoveis que temos à disposição daqueles que queiram usufruir do solo português."
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}
