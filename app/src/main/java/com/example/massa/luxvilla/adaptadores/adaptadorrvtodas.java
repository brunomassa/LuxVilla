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
import com.example.massa.luxvilla.sqlite.BDAdapter;
import com.example.massa.luxvilla.utils.todascasas;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;

/**
 * Created by massa on 22/01/2016.
 */
public class adaptadorrvtodas extends RecyclerView.Adapter<adaptadorrvtodas.vhtodas> {

    ArrayList<todascasas> casas=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    public static Context ctx;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String PREFSNAME = "FAVS";
    String id;
    int favflag;

    public  adaptadorrvtodas(Context context){
        layoutInflater= LayoutInflater.from(context);
        VolleySingleton volleySingleton=VolleySingleton.getInstancia(context);
        imageLoader=volleySingleton.getImageLoader();
        ctx=context;

    }

    public void setCasas(ArrayList<todascasas> cs){
        this.casas=cs;
        notifyItemChanged(0,casas.size());
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
        sharedPreferences=ctx.getSharedPreferences(PREFSNAME, 0);
        id=casaexata.getID();
        favflag=sharedPreferences.getInt(id, 0);

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=casaexata.getID();
                favflag=sharedPreferences.getInt(id, 0);
                if (favflag==0){

                    sharedPreferences=ctx.getSharedPreferences(PREFSNAME, 0);
                    editor=sharedPreferences.edit();
                    editor.putInt(id, 1);
                    editor.apply();
                    favflag=sharedPreferences.getInt(id,0);
                    holder.favoriteButton.setFavorite(true);
                    //Toast.makeText(ctx,id+" "+" "+String.valueOf(favflag),Toast.LENGTH_LONG).show();
                }else {

                    sharedPreferences=ctx.getSharedPreferences(PREFSNAME,0);
                    editor =sharedPreferences.edit();
                    editor.putInt(String.valueOf(id),0);
                    editor.apply();
                    favflag=sharedPreferences.getInt(String.valueOf(id),0);
                    holder.favoriteButton.setFavorite(false);
                    //Toast.makeText(ctx,id+" "+" "+String.valueOf(favflag),Toast.LENGTH_LONG).show();
                }
            }
        });

        if (favflag==0){
            holder.favoriteButton.setFavorite(false);
        }else {
            holder.favoriteButton.setFavorite(true);
        }

    }

    @Override
    public int getItemCount() {
        return casas.size();
    }

    static class vhtodas extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgcasa;
        private TextView txtLocalcasa;
        private TextView txtPrecocasa;
        private MaterialFavoriteButton favoriteButton;

        public vhtodas(final View itemView) {
            super(itemView);

            imgcasa = (ImageView) itemView.findViewById(R.id.imgcasa);
            txtLocalcasa = (TextView) itemView.findViewById(R.id.txtlocalcasa);
            txtPrecocasa = (TextView) itemView.findViewById(R.id.txtprecocasa);
            favoriteButton = (MaterialFavoriteButton) itemView.findViewById(R.id.favbutton);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
