package com.homearound.www.homearound;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homearound.www.homearound.db.CustomerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by boqiancheng on 2016-10-17.
 */

public class AdapterMyServicesC extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVWITHRATING = 0;
    public static final int CVWITHOUTRATING = 1;
    public static final int CVWITHOUTSERVICE = 2;

    public interface OnItemClickListener {
        void onItemClick(CustomerService customerService, JSONObject rateSum);
        void onDeleteSvc(CustomerService customerService);
    }

    private Context context;
    private List<CustomerService> customerServiceList;
    // need re-consider from from internet
    private JSONArray ratingDownloadedList;
    //
    private AdapterMyServicesC.OnItemClickListener listener;

    public AdapterMyServicesC(Context context, List<CustomerService> customerServiceList,
                              JSONArray ratingDownloadedList,
                              AdapterMyServicesC.OnItemClickListener listener) {
        this.context = context;
        this.customerServiceList = customerServiceList;
        this.ratingDownloadedList = ratingDownloadedList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        if(customerServiceList != null) {
            if (customerServiceList.size() > 0) {
                return customerServiceList.size();
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);
        if (customerServiceList != null) {
            if (customerServiceList.size() > 0) {
                String tempEmail = customerServiceList.get(position).getEmail();
                JSONObject tempObject = new JSONObject();
                for (int i = 0; i < ratingDownloadedList.length(); i++) {
                    JSONObject aa = ratingDownloadedList.optJSONObject(i);
                    if (tempEmail.equals(aa.optString("email"))) {
                        tempObject = aa;
                    }
                }
                if (tempObject.optBoolean("hasReview")) {
                    return CVWITHRATING;
                } else {
                    return CVWITHOUTRATING;
                }
            } else {
                return CVWITHOUTSERVICE;
            }
        } else {
            return CVWITHOUTSERVICE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVWITHRATING:
                View view1 = inflater.inflate(R.layout.cardview_layout_myservices_c, parent, false);
                viewHolder = new AdapterMyServicesC.ItemViewHolder(view1);
                break;
            case CVWITHOUTRATING:
                View view2 = inflater.inflate(R.layout.cardview_layout_myservices_no_rating_c, parent, false);
                viewHolder = new AdapterMyServicesC.ItemViewHolderNoRating(view2);
                break;
            case CVWITHOUTSERVICE:
                View view3 = inflater.inflate(R.layout.cardview_layout_myservices_no_service_c, parent, false);
                viewHolder = new AdapterMyServicesC.ItemViewHolderNoService(view3);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_layout_myservices_no_service_c, parent, false);
                viewHolder = new AdapterMyServicesC.ItemViewHolderNoService(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVWITHRATING:
                ItemViewHolder holder1 = (ItemViewHolder)holder;
                CustomerService serviceItem1 = customerServiceList.get(position);
                holder1.txtServiceNameC.setText(serviceItem1.getName());
                holder1.txtServiceDistanceC.setText(serviceItem1.getDistance());

                String tempEmail = serviceItem1.getEmail();
                JSONObject tempObject = new JSONObject();
                for (int i = 0; i < ratingDownloadedList.length(); i++) {
                    JSONObject aa = ratingDownloadedList.optJSONObject(i);
                    if (tempEmail.equals(aa.optString("email"))) {
                        tempObject = aa;
                    }
                }
                String tempR = tempObject.optString("rating") + " (" + tempObject.optString("reviewNo")
                        + " reviews)";

                holder1.txtServiceRatingC.setText(tempR);
                holder1.txtServiceDetailC.setText(serviceItem1.getDetail());

                ImageView[] starArr = {holder1.imgStar1C, holder1.imgStar2C, holder1.imgStar3C, holder1.imgStar4C, holder1.imgStar5C};
                for (int ii = 0; ii < 5; ii++) {
                    starArr[ii].setImageResource(R.drawable.stargrey);
                }

                float tempRate = Float.parseFloat(tempObject.optString("rating"));
                int tempRateInt = (int)Math.floor(tempRate);

                for (int ii = 0; ii < tempRateInt; ii++) {
                    starArr[ii].setImageResource(R.drawable.starorange);
                }

                float remainR = tempRate - (float) Math.floor(tempRate);
                if (remainR > 0.24 && remainR < 0.75) {
                    starArr[tempRateInt].setImageResource(R.drawable.starhalforange);
                } else if (remainR >= 0.75) {
                    starArr[tempRateInt].setImageResource(R.drawable.starorange);
                }

                holder1.bind(serviceItem1, tempObject, listener);
                holder1.deleteSvc(serviceItem1, listener);
                break;
            case CVWITHOUTRATING:
                ItemViewHolderNoRating holder2 = (ItemViewHolderNoRating)holder;
                CustomerService serviceItem2 = customerServiceList.get(position);

                String tempEmail12 = serviceItem2.getEmail();
                JSONObject tempObject12 = new JSONObject();
                for (int i = 0; i < ratingDownloadedList.length(); i++) {
                    JSONObject aa12 = ratingDownloadedList.optJSONObject(i);
                    if (tempEmail12.equals(aa12.optString("email"))) {
                        tempObject12 = aa12;
                    }
                }

                holder2.txtServiceNameNoRatingC.setText(serviceItem2.getName());
                holder2.txtServiceDistanceNoRatingC.setText(serviceItem2.getDistance());
                holder2.txtServiceRatingNoRatingC.setText("No reviews yet.");
                holder2.txtServiceDetailNoRatingC.setText(serviceItem2.getDetail());

                holder2.bind(serviceItem2, tempObject12, listener);
                holder2.deleteSvc(serviceItem2, listener);
                break;
            case CVWITHOUTSERVICE:
                ItemViewHolderNoService holder3 = (ItemViewHolderNoService)holder;
                holder3.txtServiceNoServiceC.setText("No service collected yet.");
                break;
            default:
                ItemViewHolderNoService holder4 = (ItemViewHolderNoService)holder;
                holder4.txtServiceNoServiceC.setText("No service collected yet.");
                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemMyServicesC;

        public ImageView imgDeleteCrossC;
        public TextView txtServiceNameC;
        public TextView txtServiceDistanceC;

        public ImageView imgStar1C;
        public ImageView imgStar2C;
        public ImageView imgStar3C;
        public ImageView imgStar4C;
        public ImageView imgStar5C;
        public TextView txtServiceRatingC;
        public ImageView imgHeartC;

        public TextView txtServiceDetailC;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemMyServicesC = (CardView)itemView.findViewById(R.id.cvItem_MyServices_C);

            imgDeleteCrossC = (ImageView)itemView.findViewById(R.id.img_my_service_delete_cross_c);
            txtServiceNameC = (TextView)itemView.findViewById(R.id.txt_my_service_name_c);
            txtServiceDistanceC = (TextView)itemView.findViewById(R.id.txt_my_service_distance_c);

            imgStar1C = (ImageView)itemView.findViewById(R.id.img_my_service_star1_c);
            imgStar2C = (ImageView)itemView.findViewById(R.id.img_my_service_star2_c);
            imgStar3C = (ImageView)itemView.findViewById(R.id.img_my_service_star3_c);
            imgStar4C = (ImageView)itemView.findViewById(R.id.img_my_service_star4_c);
            imgStar5C = (ImageView)itemView.findViewById(R.id.img_my_service_star5_c);
            txtServiceRatingC = (TextView)itemView.findViewById(R.id.txt_my_service_rating_c);
            imgHeartC = (ImageView)itemView.findViewById(R.id.img_my_service_heart_c);

            txtServiceDetailC = (TextView)itemView.findViewById(R.id.txt_my_service_detail_c);
        }

        public void bind(final CustomerService customerService, final JSONObject rateSummary,
                         final AdapterMyServicesC.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(customerService, rateSummary);
                }
            });
        }

        public void deleteSvc(final CustomerService customerService, final AdapterMyServicesC.OnItemClickListener listener) {
            imgDeleteCrossC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteSvc(customerService);
                }
            });
        }
    }

    public static class ItemViewHolderNoRating extends RecyclerView.ViewHolder {

        public CardView cvItemMyServicesNoRatingC;

        public ImageView imgDeleteCrossNoRatingC;
        public TextView txtServiceNameNoRatingC;
        public TextView txtServiceDistanceNoRatingC;

        public TextView txtServiceRatingNoRatingC;
        public ImageView imgHeartNoRatingC;

        public TextView txtServiceDetailNoRatingC;

        public ItemViewHolderNoRating(View itemView) {
            super(itemView);

            cvItemMyServicesNoRatingC = (CardView)itemView.findViewById(R.id.cvItem_MyServices_no_rating_C);

            imgDeleteCrossNoRatingC = (ImageView)itemView.findViewById(R.id.img_my_service_delete_cross_no_rating_c);
            txtServiceNameNoRatingC = (TextView)itemView.findViewById(R.id.txt_my_service_name_no_rating_c);
            txtServiceDistanceNoRatingC = (TextView)itemView.findViewById(R.id.txt_my_service_distance_no_rating_c);

            txtServiceRatingNoRatingC = (TextView)itemView.findViewById(R.id.txt_my_service_rating_no_rating_c);
            imgHeartNoRatingC = (ImageView)itemView.findViewById(R.id.img_my_service_heart_no_rating_c);

            txtServiceDetailNoRatingC = (TextView)itemView.findViewById(R.id.txt_my_service_detail_no_rating_c);
        }

        public void bind(final CustomerService customerService, final JSONObject rateSummary,
                         final AdapterMyServicesC.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(customerService, rateSummary);
                }
            });
        }

        public void deleteSvc(final CustomerService customerService, final AdapterMyServicesC.OnItemClickListener listener) {
            imgDeleteCrossNoRatingC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteSvc(customerService);
                }
            });
        }
    }

    public static class ItemViewHolderNoService extends RecyclerView.ViewHolder {

        public CardView cvItemMyServicesNoServiceC;

        public TextView txtServiceNoServiceC;

        public ItemViewHolderNoService(View itemView) {
            super(itemView);

            cvItemMyServicesNoServiceC = (CardView)itemView.findViewById(R.id.cvItem_MyServices_no_service_C);

            txtServiceNoServiceC = (TextView)itemView.findViewById(R.id.txt_my_service_no_service_c);
        }
    }
}
