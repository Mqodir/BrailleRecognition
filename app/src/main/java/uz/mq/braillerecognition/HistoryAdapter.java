package uz.mq.braillerecognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static uz.mq.braillerecognition.HistoryDB.removeFav;
import static uz.mq.braillerecognition.HistoryDB.switchFav;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{

    Context ctx;
    private ArrayList<HistoryModel> list = new ArrayList<>();
    boolean isFavs;
    public HistoryAdapter(Context ctx, ArrayList<HistoryModel> list, boolean isFavs) {
        this.ctx = ctx;
        this.isFavs = isFavs;
        this.list = list;
    }


    @Override
    public void onBindViewHolder(final HistoryAdapter.MyViewHolder holder, final int position) {
        final HistoryModel item = list.get(position);
        holder.tvBraille.setText(item.getBraille());
        holder.tvText.setText(item.getText());
        if (item.getFav()){
            holder.btnFav.setImageResource(R.drawable.ic_icons8_christmas_star);
        }else {
            holder.btnFav.setImageResource(R.drawable.ic_icons8_star_1);
        }
        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavs){
                    removeFav(ctx, item.getDate());
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                }else{
                    switchFav(ctx, list.size()-position-1);
                    list.get(list.size()-position-1).setFav(!item.getFav());
                    if (!item.getFav()){
                        holder.btnFav.setImageResource(R.drawable.ic_icons8_christmas_star);
                    }else {
                        holder.btnFav.setImageResource(R.drawable.ic_icons8_star_1);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBraille, tvText;
        ImageButton btnFav;
        public MyViewHolder(View view) {
            super(view);
            tvBraille = (TextView) view.findViewById(R.id.tvBraille);
            tvText = (TextView) view.findViewById(R.id.tvText);
            btnFav = (ImageButton) view.findViewById(R.id.btnFav);
        }
    }



}
