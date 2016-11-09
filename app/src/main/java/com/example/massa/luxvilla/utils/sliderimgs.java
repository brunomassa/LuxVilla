package com.example.massa.luxvilla.utils;

/**
 * Created by massa on 08/11/2016.
 */

public class sliderimgs {

    private String ID;
    private String IMGURL;

    public sliderimgs(){

    }

    public sliderimgs(String id,String imgurl){
        this.IMGURL=imgurl;
        this.ID=id;
    }

    public void setIMGURL(String imgurl){
        this.IMGURL=imgurl;
    }

    public void setID(String id){
        this.ID=id;
    }

    public String getIMGURL(){
        return IMGURL;
    }

    public String getID(){
        return ID;
    }

    public String toString(){
        return
                "ID: "+ID
                        +"\nIMGURL: "+IMGURL;
    }
}
