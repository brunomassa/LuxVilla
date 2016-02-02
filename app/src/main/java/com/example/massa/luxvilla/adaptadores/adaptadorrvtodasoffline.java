package com.example.massa.luxvilla.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public adaptadorrvtodasoffline(Context c,List<listasql> dados){
        inflater=LayoutInflater.from(c);
        this.dados=dados;
    }


    @Override
    public vhoffline onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.itencasas, parent, false);
        vhoffline holder=new vhoffline(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(vhoffline holder, int position) {

        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(holder.itemView);

        listasql offlinedata=dados.get(position);
        holder.txtLocalcasa.setText(offlinedata.Loc);
        holder.imgcasa.setImageResource(R.drawable.logo);
        holder.txtPrecocasa.setText(offlinedata.Prec);
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
