package com.example.massa.luxvilla.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

/**
 * Created by massa on 27/10/2017.
 */
class BDAdapter(context: Context) {

    private val core: BDCore
    private var loc: String? = null
    private var prec: String? = null
    private var inf: String? = null
    internal var ctx: Context? = null

    init {
        core = BDCore(context)
        this.ctx = context
    }

    fun inserirdados(local: String?, preco: String?, info: String?): Long? {

        val sqLiteDatabase = core.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(BDCore.TABLE_LOCAL, local)
        contentValues.put(BDCore.TABLE_PRECO, preco)
        contentValues.put(BDCore.TABLE_INFO, info)
        contentValues.put(BDCore.TABLE_FAV, 0)

        return sqLiteDatabase.insert(BDCore.TABLE_NAME, null, contentValues)
    }

    fun verlocais(ID: String?): String? {
        val sqLiteDatabase = core.writableDatabase
        val colunas = arrayOf(BDCore.TABLE_LOCAL, BDCore.TABLE_PRECO, BDCore.TABLE_INFO)
        val cursor = sqLiteDatabase.query(BDCore.TABLE_NAME, colunas, BDCore.TABLE_ID + "='" + ID + "'", null, null, null, null)
        while (cursor.moveToNext()) {
            loc = cursor.getString(0)
            prec = cursor.getString(1)
            inf = cursor.getString(2)


            //Toast.makeText(ctx,loc+" "+prec+" "+inf+" ",Toast.LENGTH_LONG).show();

        }
        cursor.close()
        return loc
    }

    fun verprecos(ID: String?): String? {
        val sqLiteDatabase = core.writableDatabase
        val colunas = arrayOf(BDCore.TABLE_LOCAL, BDCore.TABLE_PRECO, BDCore.TABLE_INFO)
        val cursor = sqLiteDatabase.query(BDCore.TABLE_NAME, colunas, BDCore.TABLE_ID + "='" + ID + "'", null, null, null, null)
        while (cursor.moveToNext()) {

            loc = cursor.getString(0)
            prec = cursor.getString(1)
            inf = cursor.getString(2)


            //Toast.makeText(ctx,loc+" "+prec+" "+inf+" ",Toast.LENGTH_LONG).show();

        }
        cursor.close()
        return prec
    }

    fun verinfos(ID: String?): String? {
        val sqLiteDatabase = core.writableDatabase
        val colunas = arrayOf(BDCore.TABLE_LOCAL, BDCore.TABLE_PRECO, BDCore.TABLE_INFO)
        val cursor = sqLiteDatabase.query(BDCore.TABLE_NAME, colunas, BDCore.TABLE_ID + "='" + ID + "'", null, null, null, null)
        while (cursor.moveToNext()) {
            loc = cursor.getString(0)
            prec = cursor.getString(1)
            inf = cursor.getString(2)


            //Toast.makeText(ctx,loc+" "+prec+" "+inf+" ",Toast.LENGTH_LONG).show();

        }
        cursor.close()
        return inf
    }

    fun numerodecolunas(): Int {
        var cnt = 0
        try {
            val sqLiteDatabase = core.writableDatabase
            val cursor = sqLiteDatabase.query(BDCore.TABLE_NAME, null, null, null, null, null, null)
            while (cursor.moveToNext()) {
                cnt++


            }
            cursor.close()
        } catch (ex: Exception) {
            Toast.makeText(ctx, ex.toString(), Toast.LENGTH_LONG).show()
        }

        return cnt
    }

     class BDCore constructor(context: Context) : SQLiteOpenHelper(context, BD_NAME, null, BD_VERSION) {
         val ctx = context

        override fun onCreate(db: SQLiteDatabase) {

            try {
                db.execSQL("create table $TABLE_NAME($TABLE_ID integer primary key autoincrement,$TABLE_LOCAL text not null,$TABLE_PRECO text not null,infocasa text not null,$TABLE_FAV integer);")

            } catch (e: Exception) {
                Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show()
            }

        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            try {
                db.execSQL("drop table casa;")
                Toast.makeText(ctx, "base de dados atualizada", Toast.LENGTH_LONG).show()
                onCreate(db)
            } catch (e: Exception) {
                Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show()
            }

        }

        companion object {
            val BD_NAME = "dbcasas"
            val BD_VERSION = 4
            val TABLE_NAME = "casa"
            val TABLE_LOCAL = "local"
            val TABLE_PRECO = "preco"
            val TABLE_INFO = "infocasa"
            val TABLE_ID = "_id"
            val TABLE_FAV = "fav"
        }
    }

    companion object

}
