package com.homearound.www.homearound;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.homearound.www.homearound.db.CustomerJob;

import java.util.List;

/**
 * Created by boqiancheng on 2016-10-05.
 */
public class AdapterMyJobsC extends RecyclerView.Adapter<AdapterMyJobsC.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CustomerJob customerJob);
    }

    private Context context;
    private List<CustomerJob> customerJobList;
    private OnItemClickListener listener;

    public AdapterMyJobsC(Context context, List<CustomerJob> customerJobList, OnItemClickListener listener) {
        this.context = context;
        this.customerJobList = customerJobList;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_layout_myjobs_c, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        CustomerJob jobItem = customerJobList.get(position);
        holder.txtJobTitleC.setText(jobItem.getJobtitle());
        holder.txtJobTimeC.setText(jobItem.getTimecreated());
        holder.txtJobStatusC.setText(jobItem.getJobstatus());
        holder.txtJobDetailC.setText(jobItem.getJobdetail());

        holder.bind(jobItem, listener);
    }

    @Override
    public int getItemCount() {
        if(customerJobList != null) {
            return customerJobList.size();
        }
        return 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemC;
        public TextView txtJobTitleC;
        public TextView txtJobTimeC;
        public TextView txtJobStatusC;
        public TextView txtJobDetailC;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemC = (CardView)itemView.findViewById(R.id.cvItem_MyJobs_C);
            txtJobTitleC = (TextView)itemView.findViewById(R.id.txt_job_title_c);
            txtJobTimeC = (TextView)itemView.findViewById(R.id.txt_job_time_c);
            txtJobStatusC = (TextView)itemView.findViewById(R.id.txt_job_status_c);
            txtJobDetailC = (TextView)itemView.findViewById(R.id.txt_job_detail_c);

        }

        public void bind(final CustomerJob job, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(job);
                }
            });
        }
    }

}
