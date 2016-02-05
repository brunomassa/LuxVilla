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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.utils.listasql;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.Collections;
import java.util.List;

/**
 * Created by massa on 26/01/2016.
 */
public class adaptadorrvtodasoffline extends RecyclerView.Adapter<adaptadorrvtodasoffline.vhoffline> {

    private LayoutInflater inflater;
    List<listasql> dados= Collections.emptyList();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String PREFSNAME = "FAVS";
    String id;
    int favflag;
    public static Context ctx;

    public adaptadorrvtodasoffline(Context c,List<listasql> dados){
        inflater=LayoutInflater.from(c);
        this.dados=dados;
        ctx=c;
    }


    @Override
    public vhoffline onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.itencasas, parent, false);
        vhoffline holder=new vhoffline(view);
        return holder;
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

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id=offlinedata.Id;
                favflag=sharedPreferences.getInt(id, 0);
                if (favflag==0){

                    sharedPreferences=ctx.getSharedPreferences(PREFSNAME, 0);
                    editor=sharedPreferences.edit();
                    editor.putInt(id, 1);
                    editor.apply();
                    favflag=sharedPreferences.getInt(id,0);
                    holder.favoriteButton.setFavorite(true);
                    //Toast.makeText(ctx, id + " " + " " + String.valueOf(favflag), Toast.LENGTH_LONG).show();
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
        return dados.size();
    }

    public class vhoffline extends RecyclerView.ViewHolder{
        private ImageView imgcasa;
        private TextView txtLocalcasa;
        private TextView txtPrecocasa;
        private MaterialFavoriteButton favoriteButton;

        public vhoffline(View itemView) {
            super(itemView);

            imgcasa=(ImageView)itemView.findViewById(R.id.imgcasa);
            txtLocalcasa=(TextView)itemView.findViewById(R.id.txtlocalcasa);
            txtPrecocasa=(TextView)itemView.findViewById(R.id.txtprecocasa);
            favoriteButton=(MaterialFavoriteButton)itemView.findViewById(R.id.favbutton);
        }
    }
}
