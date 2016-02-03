package com.example.massa.luxvilla.adaptadores;

import android.app.Application;
import android.content.Context;
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
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    public static Context ctx;
    BDAdapter adapter;
    int favflg;

    public  adaptadorrvtodas(Context context){
        layoutInflater=layoutInflater.from(context);
        volleySingleton=VolleySingleton.getInstancia(context);
        imageLoader=volleySingleton.getImageLoader();
        this.ctx=context;

    }

    public void setCasas(ArrayList<todascasas> cs){
        this.casas=cs;
        notifyItemChanged(0,casas.size());
    }

    @Override
    public vhtodas onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.itencasas, parent, false);
        vhtodas vhtds=new vhtodas(view);
        return vhtds;
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
        }else {

        }
        adapter=new BDAdapter(ctx);
        favflg=adapter.verfav(String.valueOf(position));
        Toast.makeText(ctx,String.valueOf(favflg),Toast.LENGTH_LONG).show();//only for check if work
        holder.favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favflg==0){
                    adapter.updatefav(position,String.valueOf(1));
                    favflg=adapter.verfav(String.valueOf(position));
                }else {
                    adapter.updatefav(position,String.valueOf(0));
                    favflg=adapter.verfav(String.valueOf(position));
                }

            }
        });
        if (favflg==1){
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

            imgcasa=(ImageView)itemView.findViewById(R.id.imgcasa);
            txtLocalcasa=(TextView)itemView.findViewById(R.id.txtlocalcasa);
            txtPrecocasa=(TextView)itemView.findViewById(R.id.txtprecocasa);
            favoriteButton=(MaterialFavoriteButton)itemView.findViewById(R.id.favbutton);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
