package com.example.microvpn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegionListAdapter extends RecyclerView.Adapter<RegionListAdapter.ViewHolder> {
    private List<Country> regions;
    private RegionListAdapterInterface listAdapterInterface;
   /* onClickInterface onClickInterface;

    public interface onClickInterface {
        void setClick(int abc);
    }*/
   private OnitemClickListener mListener;
    public interface OnitemClickListener{
       void  onItemClick(int position);
    }
    public void setOnItemClickedListener(OnitemClickListener listener){
        mListener=listener;
    }
    public RegionListAdapter(RegionListAdapterInterface listAdapterInterface) {
        this.listAdapterInterface = listAdapterInterface;
    }
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RegionListAdapter.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.region_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v,mListener);
        return vh;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.region_title)
        TextView regionTitle;
        ImageView regionImage;

        public ViewHolder(View v,final OnitemClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            regionImage = v.findViewById(R.id.region_image);
            regionTitle = v.findViewById(R.id.region_title);
v.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (listener!=null){
            int position = getLayoutPosition();
            if (position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position);
               // Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getName(), Toast.LENGTH_SHORT).show();

            }
        }
    }
});
        }
        public void setClicks() {
            regionTitle.setClickable(false);}

    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RegionListAdapter.ViewHolder holder, int position) {
        Country country = regions.get(position);
        Locale locale = new Locale("", regions.get(position).getCountry());
        if (country.getCountry() != null && !country.getCountry().equals("")) {
            holder.regionTitle.setText(locale.getDisplayCountry());
            String str = regions.get(position).getCountry().toLowerCase();
            holder.regionImage.setImageResource(MainApplication.getStaticContext().getResources().getIdentifier("drawable/" + str, "drawable", MainApplication.getStaticContext().getPackageName()));
            if (Prefs.getBoolean("appFailure", false)){
                // holder.rl_lock.setVisibility(View.GONE);

                holder.itemView.setOnClickListener(view -> {
                    listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition()));
                    Prefs.putString("sname", regions.get(position).getCountry());
                    Prefs.putString("simage", regions.get(position).getCountry());
                });

               // onClickInterface.setClick(position);
            }

            else{

                if (country.getCountry().equals("de")
                        ||country.getCountry().equals("hk")
                        ||country.getCountry().equals("ru")
                        ||country.getCountry().equals("jp")
                        ||country.getCountry().equals("it")
                        ||country.getCountry().equals("es")
                        ||country.getCountry().equals("au")
                        ||country.getCountry().equals("gb")
                        ||country.getCountry().equals("us")
                        ||country.getCountry().equals("ca")

                ){

                    //holder.rl_lock.setVisibility(View.GONE);

                    holder.itemView.setOnClickListener(view -> {
                        listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition()));
                        Prefs.putString("sname", regions.get(position).getCountry());
                        Prefs.putString("simage", regions.get(position).getCountry());
                    });
                }else {
                    //  holder.rl_lock.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.regionTitle.setText("Unknown Server");
            // holder.regionImage.setImageResource(R.drawable.ca);
            holder.setClicks();
        }
    }
    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        regions.add(new Country(""));
        regions.addAll(list);
        regions.remove(0);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }
    public interface RegionListAdapterInterface {
        void onCountrySelected(Country item);
    }
}
