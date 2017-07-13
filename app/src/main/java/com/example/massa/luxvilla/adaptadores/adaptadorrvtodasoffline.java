package com.example.massa.luxvilla.adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.utils.firebaseutils;
import com.example.massa.luxvilla.utils.listasql;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;


import java.util.Collections;
import java.util.List;

/**
 * Created by massa on 26/01/2016.
 */
public class adaptadorrvtodasoffline extends RecyclerView.Adapter<adaptadorrvtodasoffline.vhoffline> {

    private LayoutInflater inflater;
    private List<listasql> dados= Collections.emptyList();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String PREFSNAME = "FAVS";
    private String id;
    private int favflag;
    private static Context ctx;

    public adaptadorrvtodasoffline(Context c,List<listasql> dados){
        inflater=LayoutInflater.from(c);
        this.dados=dados;
        ctx=c;
    }


    @Override
    public vhoffline onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.itencasas, parent, false);
        return new vhoffline(view);
    }

    @Override
    public void onBindViewHolder(final vhoffline holder, int position) {

        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(holder.itemView);

        final listasql offlinedata=dados.get(position);
        holder.txtLocalcasa.setText(offlinedata.Loc);
        holder.imgcasa.setImageResource(R.drawable.logo);
        holder.txtPrecocasa.setText(offlinedata.Prec);

        sharedPreferences=ctx.getSharedPreferences(PREFSNAME, 0);
        id=offlinedata.Id;
        favflag=sharedPreferences.getInt(id, 0);

        holder.favoriteButton.setIcon(IconType.Heart);
        holder.favoriteButton.setIconSizeDp(25);
        holder.favoriteButton.setCircleEndColorRes(R.color.colorAccent);
        holder.favoriteButton.setExplodingDotColorsRes(R.color.colorPrimary,R.color.colorAccent);
        holder.favoriteButton.setLikeDrawableRes(R.drawable.heartliked);
        holder.favoriteButton.setUnlikeDrawableRes(R.drawable.heartunliked);


        holder.favoriteButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                id=offlinedata.Id;
                favflag=sharedPreferences.getInt(id, 0);

                sharedPreferences=ctx.getSharedPreferences(PREFSNAME, 0);
                editor=sharedPreferences.edit();
                editor.putInt(id, 1);
                editor.apply();
                favflag=sharedPreferences.getInt(id,0);

                firebaseutils.setlike(id);

                holder.favoriteButton.setLiked(true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                id=offlinedata.Id;
                favflag=sharedPreferences.getInt(id, 0);

                sharedPreferences=ctx.getSharedPreferences(PREFSNAME,0);
                editor =sharedPreferences.edit();
                editor.putInt(String.valueOf(id),0);
                editor.apply();
                favflag=sharedPreferences.getInt(String.valueOf(id),0);

                firebaseutils.removelike(id);

                holder.favoriteButton.setLiked(false);
            }
        });

        firebaseutils.checklike(id,holder.favoriteButton);
    }

    @Override
    public int getItemCount() {
        return dados.size();
    }

    class vhoffline extends RecyclerView.ViewHolder{
        private ImageView imgcasa;
        private TextView txtLocalcasa;
        private TextView txtPrecocasa;
        private LikeButton favoriteButton;

        vhoffline(View itemView) {
            super(itemView);

            imgcasa=(ImageView)itemView.findViewById(R.id.imgcasa);
            txtLocalcasa=(TextView)itemView.findViewById(R.id.txtlocalcasa);
            txtPrecocasa=(TextView)itemView.findViewById(R.id.txtprecocasa);
            favoriteButton=(LikeButton) itemView.findViewById(R.id.favbutton);
        }
    }
}
