package com.homearound.www.homearound;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by boqiancheng on 2016-11-28.
 */

public class AdapterJobPostedM extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVJOBPOSTED = 11118;

    private JSONArray jobPostedList;

    public interface OnItemClickListener {
        void onItemClick(JSONObject jobPostedItem, int itemClickPos);
    }

    private AdapterJobPostedM.OnItemClickListener listener;

    public AdapterJobPostedM(JSONArray jobPostedList, AdapterJobPostedM.OnItemClickListener listener) {
        this.jobPostedList = jobPostedList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return jobPostedList.length();
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);
        return CVJOBPOSTED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVJOBPOSTED:
                View view1 = inflater.inflate(R.layout.cardview_job_posted_m, parent, false);
                viewHolder = new AdapterJobPostedM.ItemViewHolder(view1);
                break;

            default:
                View view = inflater.inflate(R.layout.cardview_job_posted_m, parent, false);
                viewHolder = new AdapterJobPostedM.ItemViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVJOBPOSTED:
                AdapterJobPostedM.ItemViewHolder holder1 = (AdapterJobPostedM.ItemViewHolder)holder;
                JSONObject jobItem = jobPostedList.optJSONObject(position);

                holder1.txtJobPostedNameM.setText(jobItem.optString("name"));
                holder1.txtJobPostedDistanceM.setText(jobItem.optString("distance"));
                holder1.txtJobPostedTitleM.setText(jobItem.optString("jobTitle"));
                // here for time to finish job
                holder1.txtJobPostedTimeM.setText(jobItem.optString("timeFinish"));
                //     holder1.bind(serviceItem1, listener);
                holder1.clickItem(jobItem, listener);
                break;

            default:

                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemJobPostedM;

        public TextView txtJobPostedNameM;
        public TextView txtJobPostedDistanceM;
        public TextView txtJobPostedTitleM;
        public TextView txtJobPostedTimeM;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemJobPostedM = (CardView)itemView.findViewById(R.id.cvItem_job_posted_m);

            txtJobPostedNameM = (TextView)itemView.findViewById(R.id.txt_job_posted_name_m);
            txtJobPostedDistanceM = (TextView)itemView.findViewById(R.id.txt_job_posted_distance_m);
            txtJobPostedTitleM = (TextView)itemView.findViewById(R.id.txt_job_posted_title_m);

            txtJobPostedTimeM = (TextView) itemView.findViewById(R.id.txt_job_posted_time_m);
        }

        public void clickItem(final JSONObject item, final AdapterJobPostedM.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, getAdapterPosition());
                }
            });
        }
    }
}
