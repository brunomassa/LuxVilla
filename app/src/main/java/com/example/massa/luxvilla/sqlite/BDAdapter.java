package com.example.massa.luxvilla.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by massa on 26/01/2016.
 */
public class BDAdapter {

    BDCore core;
    static Context ctx;
   public String loc;
   public String prec;
   public String inf;
    public int favflag;

    public BDAdapter(Context context){
        core=new BDCore(context);
        this.ctx=context;
    }

    public Long inserirdados(String local, String preco, String info){

        SQLiteDatabase sqLiteDatabase=core.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(BDCore.TABLE_LOCAL,local);
        contentValues.put(BDCore.TABLE_PRECO,preco);
        contentValues.put(BDCore.TABLE_INFO, info);
        contentValues.put(BDCore.TABLE_FAV, 0);
        Long id=sqLiteDatabase.insert(BDCore.TABLE_NAME, null, contentValues);

        return id;
    }

    public String verlocais(String ID){
        SQLiteDatabase sqLiteDatabase=core.getWritableDatabase();
        String[] colunas={BDCore.TABLE_LOCAL,BDCore.TABLE_PRECO,BDCore.TABLE_INFO};
        Cursor cursor=sqLiteDatabase.query(BDCore.TABLE_NAME, colunas, BDCore.TABLE_ID + "='" + ID + "'", null, null, null, null);
        while (cursor.moveToNext()){
            loc=cursor.getString(0);
            prec=cursor.getString(1);
            inf=cursor.getString(2);



            //Toast.makeText(ctx,loc+" "+prec+" "+inf+" ",Toast.LENGTH_LONG).show();

        }
        return loc;
    }

    public String verprecos(String ID){
        SQLiteDatabase sqLiteDatabase=core.getWritableDatabase();
        String[] colunas={BDCore.TABLE_LOCAL,BDCore.TABLE_PRECO,BDCore.TABLE_INFO};
        Cursor cursor=sqLiteDatabase.query(BDCore.TABLE_NAME, colunas, BDCore.TABLE_ID+"='"+ID+"'", null, null, null, null);
        while (cursor.moveToNext()){

            loc=cursor.getString(0);
            prec=cursor.getString(1);
            inf=cursor.getString(2);



            //Toast.makeText(ctx,loc+" "+prec+" "+inf+" ",Toast.LENGTH_LONG).show();

        }
        return prec;
    }

    public String verinfos(String ID){
        SQLiteDatabase sqLiteDatabase=core.getWritableDatabase();
        String[] colunas={BDCore.TABLE_LOCAL,BDCore.TABLE_PRECO,BDCore.TABLE_INFO};
        Cursor cursor=sqLiteDatabase.query(BDCore.TABLE_NAME, colunas, BDCore.TABLE_ID+"='"+ID+"'", null, null, null, null);
        while (cursor.moveToNext()){
            loc=cursor.getString(0);
            prec=cursor.getString(1);
            inf=cursor.getString(2);



            //Toast.makeText(ctx,loc+" "+prec+" "+inf+" ",Toast.LENGTH_LONG).show();

        }
        cursor.close();
        return inf;
    }

    public  int numerodecolunas(){
        int cnt=0;
        try {
            SQLiteDatabase sqLiteDatabase=core.getWritableDatabase();
            Cursor cursor=sqLiteDatabase.query(BDCore.TABLE_NAME, null,null, null, null, null, null);
            while (cursor.moveToNext()){
                cnt++;


            }
        }catch (Exception ex){
            Toast.makeText(ctx,ex.toString(),Toast.LENGTH_LONG).show();
        }

        return cnt;
    }

    static class BDCore extends SQLiteOpenHelper {
        private static final String BD_NAME="dbcasas";
        private static final int BD_VERSION=4;
        private static final String TABLE_NAME="casa";
        private static final String TABLE_LOCAL="local";
        private static final String TABLE_PRECO="preco";
        private static final String TABLE_INFO="infocasa";
        private static final String TABLE_ID="_id";
        private static final String TABLE_FAV="fav";
        Context context;

        public BDCore(Context context) {
            super(context, BD_NAME, null, BD_VERSION);
            this.context=context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL("create table "+TABLE_NAME+"("+TABLE_ID+" integer primary key autoincrement,"+TABLE_LOCAL+" text not null,"+TABLE_PRECO+" text not null,infocasa text not null,"+TABLE_FAV+" integer);");

            }catch (Exception e){
                Toast.makeText(ctx,e.toString(),Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL("drop table casa;");
                Toast.makeText(ctx,"base de dados atualizada",Toast.LENGTH_LONG).show();
                onCreate(db);
            }catch (Exception e){
                Toast.makeText(ctx,e.toString(),Toast.LENGTH_LONG).show();
            }

        }
    }

}
