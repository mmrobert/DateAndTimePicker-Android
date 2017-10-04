package com.homearound.www.homearound;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.homearound.www.homearound.db.MerchantJob;

import java.util.List;

/**
 * Created by boqiancheng on 2016-12-01.
 */

public class PVFGAdapterRVMyJobsM extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVMYJOBM = 88118;

    List<MerchantJob> myJobsList;

    public interface OnItemClickListener {
        void onItemClick(MerchantJob jobItem, int itemClickPos);
    }

    private PVFGAdapterRVMyJobsM.OnItemClickListener listener;

    public PVFGAdapterRVMyJobsM(List<MerchantJob> myJobsList,
                                PVFGAdapterRVMyJobsM.OnItemClickListener listener) {
        this.myJobsList = myJobsList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return myJobsList.size();
    }

    @Override
    public int getItemViewType(int position) {
       // return super.getItemViewType(position);
        return CVMYJOBM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVMYJOBM:
                View view1 = inflater.inflate(R.layout.pvfg_cardview_my_jobs_m, parent, false);
                viewHolder = new PVFGAdapterRVMyJobsM.ItemViewHolder(view1);
                break;

            default:
                View view = inflater.inflate(R.layout.pvfg_cardview_my_jobs_m, parent, false);
                viewHolder = new PVFGAdapterRVMyJobsM.ItemViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVMYJOBM:
                PVFGAdapterRVMyJobsM.ItemViewHolder holder1 = (PVFGAdapterRVMyJobsM.ItemViewHolder)holder;
                MerchantJob jobItem = myJobsList.get(position);

                holder1.txtMyJobsTitleM.setText(jobItem.getJobtitle());
                holder1.txtMyJobsDistanceM.setText(jobItem.getDistance());
                holder1.txtMyJobsNameM.setText(jobItem.getName());
                // here for time to finish job
                holder1.txtMyJobsTimeFinishM.setText(jobItem.getTimefinish());
                //     holder1.bind(serviceItem1, listener);
                holder1.clickItem(jobItem, listener);
                break;

            default:

                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemMyJobsM;

        public TextView txtMyJobsTitleM;
        public TextView txtMyJobsDistanceM;
        public TextView txtMyJobsNameM;
        public TextView txtMyJobsTimeFinishM;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemMyJobsM = (CardView)itemView.findViewById(R.id.cvItem_my_jobs_m);

            txtMyJobsTitleM = (TextView)itemView.findViewById(R.id.txt_my_job_title_m);
            txtMyJobsDistanceM = (TextView)itemView.findViewById(R.id.txt_my_job_distance_m);
            txtMyJobsNameM = (TextView)itemView.findViewById(R.id.txt_my_job_name_m);

            txtMyJobsTimeFinishM = (TextView) itemView.findViewById(R.id.txt_my_job_timefinish_m);
        }

        public void clickItem(final MerchantJob item, final PVFGAdapterRVMyJobsM.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, getAdapterPosition());
                }
            });
        }
    }
}
