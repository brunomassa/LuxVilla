package com.example.massa.luxvilla.adaptadores

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.utils.firebaseutils
import com.example.massa.luxvilla.utils.listasql
import com.like.IconType
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.itencasas.view.*

/**
 * Created by massa on 27/10/2017.
 */
class adaptadorrvtodasoffline(c: Context, dados: List<listasql>) : RecyclerView.Adapter<adaptadorrvtodasoffline.vhoffline>() {

    private val inflater: LayoutInflater
    private var dados = emptyList<listasql>()

    init {
        inflater = LayoutInflater.from(c)
        this.dados = dados
        ctx = c
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vhoffline {
        val view = inflater.inflate(R.layout.itencasas, parent, false)
        return vhoffline(view)
    }

    override fun onBindViewHolder(holder: vhoffline, position: Int) {

        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(holder.itemView)

        val offlinedata = dados[position]
        holder.txtLocalcasa.text = offlinedata.Loc
        holder.imgcasa.setImageResource(R.drawable.logo)
        holder.txtPrecocasa.text = offlinedata.Prec

        holder.favoriteButton.setIcon(IconType.Heart)
        holder.favoriteButton.setIconSizeDp(25)
        holder.favoriteButton.setCircleEndColorRes(R.color.colorAccent)
        holder.favoriteButton.setExplodingDotColorsRes(R.color.colorPrimary, R.color.colorAccent)
        holder.favoriteButton.setLikeDrawableRes(R.drawable.heartliked)
        holder.favoriteButton.setUnlikeDrawableRes(R.drawable.heartunliked)


        holder.favoriteButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                firebaseutils.setlike(offlinedata.Id!!)
                holder.favoriteButton.setLiked(true)
            }

            override fun unLiked(likeButton: LikeButton) {
                firebaseutils.removelike(offlinedata.Id!!)
                holder.favoriteButton.setLiked(false)
            }
        })

        firebaseutils.checklike(ctx!!, offlinedata.Id!!, holder.favoriteButton)
    }

    override fun getItemCount(): Int {
        return dados.size
    }

    inner class vhoffline(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
    }

    companion object {
        private var ctx: Context? = null
    }
}
