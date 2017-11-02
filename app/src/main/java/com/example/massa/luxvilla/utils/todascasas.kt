package com.example.massa.luxvilla.utils

import com.google.gson.annotations.SerializedName

/**
 * Created by massa on 27/10/2017.
 */
data class Todascasas( @SerializedName("id") var id: String? = null,
                       @SerializedName("local") var local: String? = null,
                       @SerializedName("preco") var preco: String? = null,
                       @SerializedName("imgURL") var imgurl: String? = null,
                       @SerializedName("infocasa") var infocasa: String? = null)
