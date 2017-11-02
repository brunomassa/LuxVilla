package com.example.massa.luxvilla.adaptadores

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.toolbox.ImageLoader
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.network.VolleySingleton
import com.example.massa.luxvilla.utils.casas
import com.example.massa.luxvilla.utils.firebaseutils
import com.like.IconType
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.itencasas.view.*
import java.util.ArrayList

/**
 * Created by massa on 27/10/2017.
 */
class adaptadorrvtodas(context: Context) : RecyclerView.Adapter<adaptadorrvtodas.vhtodas>() {

    private var casas = ArrayList<casas>()
    private val layoutInflater: LayoutInflater
    private val imageLoader: ImageLoader

    init {
        layoutInflater = LayoutInflater.from(context)
        val volleySingleton = VolleySingleton.getInstancia(context)
        imageLoader = volleySingleton.imageLoader
        ctx = context

    }

    fun setCasas(cs: ArrayList<casas>) {
        this.casas = cs
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        casas.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, casas.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vhtodas {
        val view = layoutInflater.inflate(R.layout.itencasas, parent, false)
        return vhtodas(view)
    }

    override fun onBindViewHolder(holder: vhtodas, position: Int) {

        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(holder.itemView)

        val casaexata = casas[position]
        holder.txtLocalcasa.text = casaexata.local
        holder.txtPrecocasa.text = casaexata.preco
        val url = casaexata.imgurl
        if (url != null) {
            Picasso.with(ctx).load(url).error(R.drawable.logo).into(holder.imgcasa)
        }
        holder.favoriteButton.setIcon(IconType.Heart)
        holder.favoriteButton.setIconSizeDp(25)
        holder.favoriteButton.setCircleEndColorRes(R.color.colorAccent)
        holder.favoriteButton.setExplodingDotColorsRes(R.color.colorPrimary, R.color.colorAccent)
        holder.favoriteButton.setLikeDrawableRes(R.drawable.heartliked)
        holder.favoriteButton.setUnlikeDrawableRes(R.drawable.heartunliked)

        holder.favoriteButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                firebaseutils.setlike(casaexata.id!!)
                holder.favoriteButton.setLiked(true)
            }

            override fun unLiked(likeButton: LikeButton) {
                firebaseutils.removelike(casaexata.id!!)
                holder.favoriteButton.setLiked(false)
            }
        })
        firebaseutils.checklike(ctx!!, casaexata.id!!, holder.favoriteButton)
    }

    override fun getItemCount(): Int {
        return casas.size
    }

    class vhtodas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

         val imgcasa: ImageView
         val txtLocalcasa: TextView
         val txtPrecocasa: TextView
         val favoriteButton: LikeButton

        init {

            imgcasa = itemView.imgcasa
            txtLocalcasa = itemView.txtlocalcasa
            txtPrecocasa = itemView.txtprecocasa
            favoriteButton = itemView.favbutton


        }

        override fun onClick(v: View) {

        }
    }

    companion object {
        var ctx: Context? = null
    }
}
