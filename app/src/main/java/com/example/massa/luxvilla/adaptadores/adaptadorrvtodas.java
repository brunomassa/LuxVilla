package com.example.massa.luxvilla.adaptadores;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.network.VolleySingleton;
import com.example.massa.luxvilla.utils.firebaseutils;
import com.example.massa.luxvilla.utils.todascasas;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

/**
 * Created by massa on 22/01/2016.
 */
public class adaptadorrvtodas extends RecyclerView.Adapter<adaptadorrvtodas.vhtodas> {

    private ArrayList<todascasas> casas=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    public static Context ctx;

    public  adaptadorrvtodas(Context context){
        layoutInflater= LayoutInflater.from(context);
        VolleySingleton volleySingleton=VolleySingleton.getInstancia(context);
        imageLoader=volleySingleton.getImageLoader();
        ctx=context;

    }

    public void setCasas(ArrayList<todascasas> cs){
        this.casas=cs;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        casas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, casas.size());
    }

    @Override
    public vhtodas onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.itencasas, parent, false);
        return new vhtodas(view);
    }

    @Override
    public void onBindViewHolder(final vhtodas holder, final int position) {

        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(holder.itemView);

        final todascasas casaexata=casas.get(position);
        holder.txtLocalcasa.setText(casaexata.getLOCAL());
        holder.txtPrecocasa.setText(casaexata.getPRECO());
        String url=casaexata.getIMGURL();
        if(url!=null){
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.imgcasa.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    holder.imgcasa.setImageResource(R.drawable.logo);
                }
            });
        }
        holder.favoriteButton.setIcon(IconType.Heart);
        holder.favoriteButton.setIconSizeDp(25);
        holder.favoriteButton.setCircleEndColorRes(R.color.colorAccent);
        holder.favoriteButton.setExplodingDotColorsRes(R.color.colorPrimary,R.color.colorAccent);
        holder.favoriteButton.setLikeDrawableRes(R.drawable.heartliked);
        holder.favoriteButton.setUnlikeDrawableRes(R.drawable.heartunliked);

        holder.favoriteButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                firebaseutils.setlike(casaexata.getID());
                holder.favoriteButton.setLiked(true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                firebaseutils.removelike(casaexata.getID());
                holder.favoriteButton.setLiked(false);
            }
        });
        firebaseutils.checklike(ctx,casaexata.getID(),holder.favoriteButton);
    }

    @Override
    public int getItemCount() {
        return casas.size();
    }

    static class vhtodas extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgcasa;
        private TextView txtLocalcasa;
        private TextView txtPrecocasa;
        private LikeButton favoriteButton;

        vhtodas(final View itemView) {
            super(itemView);

            imgcasa = (ImageView) itemView.findViewById(R.id.imgcasa);
            txtLocalcasa = (TextView) itemView.findViewById(R.id.txtlocalcasa);
            txtPrecocasa = (TextView) itemView.findViewById(R.id.txtprecocasa);
            favoriteButton = (LikeButton) itemView.findViewById(R.id.favbutton);


        }

        @Override
        public void onClick(View v) {

        }
    }
}
