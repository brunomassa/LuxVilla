package com.example.massa.luxvilla.utils;

/**
 * Created by massa on 22/01/2016.
 */
public class todascasas {

    private String ID;
    private String LOCAL;
    private String PRECO;
    private String IMGURL;


    public todascasas(){

    }

    public todascasas(String local,
                      String preco,
                      String imgurl){
        this.LOCAL=local;
        this.PRECO=preco;
        this.IMGURL=imgurl;
    }

    public void setLOCAL(String local){
        this.LOCAL=local;
    }

    public void setPRECO(String preco){
        this.PRECO=preco;
    }
    public void setIMGURL(String imgurl){
        this.IMGURL=imgurl;
    }

    public String getLOCAL(){
        return LOCAL;
    }

    public String getPRECO(){
        return PRECO;
    }
    public String getIMGURL(){
        return IMGURL;
    }

    public String toString(){
        return
                "LOCAL: "+ LOCAL
                +"\nPRECO: "+PRECO
                +"\nIMGURL: "+IMGURL;
    }
}
