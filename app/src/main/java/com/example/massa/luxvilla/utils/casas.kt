package com.example.massa.luxvilla.utils

/**
 * Created by massa on 31/10/2017.
 */
class casas {



    var id: String? = null

    var local: String? = null

    var preco: String? = null

    var imgurl: String? = null





    constructor() {



    }



    constructor(local: String,

                preco: String,

                imgurl: String,

                id: String) {

        this.local = local

        this.preco = preco

        this.imgurl = imgurl

        this.id = id

    }



    override fun toString(): String {

        return ("ID: " + id

                + "\nLOCAL: " + local

                + "\nPRECO: " + preco

                + "\nIMGURL: " + imgurl)

    }

}