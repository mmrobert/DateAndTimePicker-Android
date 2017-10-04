package com.homearound.www.homearound;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
 * Created by boqiancheng on 2016-10-21.
 */

public class AdapterServicesLoadedC extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVWITHRATING = 6660;
    public static final int CVWITHOUTRATING = 6661;

    private JSONArray downLoadedSvcList;
    // need re-consider from from internet
    private List<CustomerService> collectedServiceList;
    private String catString;

    public interface OnItemClickAndLikeListener {
        void onItemClick(JSONObject item, int itemClickPos);
        void onLike(JSONObject item, int itemClickPos);
    }

    private AdapterServicesLoadedC.OnItemClickAndLikeListener listener;

    public AdapterServicesLoadedC(String catString, JSONArray downLoadedSvcList,
                                  List<CustomerService> collectedServiceList,
                                  AdapterServicesLoadedC.OnItemClickAndLikeListener listener) {
        this.catString = catString;
        this.downLoadedSvcList = downLoadedSvcList;
        this.collectedServiceList = collectedServiceList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return downLoadedSvcList.length();
    }

    @Override
    public int getItemViewType(int position) {
        //  return super.getItemViewType(position);
        JSONObject tempO = downLoadedSvcList.optJSONObject(position);
        boolean hasReview = tempO.optBoolean("hasReview");
        if (hasReview) {
            return CVWITHRATING;
        } else {
            return CVWITHOUTRATING;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVWITHRATING:
                View view1 = inflater.inflate(R.layout.cardview_layout_service_loaded_c, parent, false);
                viewHolder = new AdapterServicesLoadedC.ItemViewHolder(view1);
                break;
            case CVWITHOUTRATING:
                View view2 = inflater.inflate(R.layout.cardview_layout_service_loaded_no_rating_c, parent, false);
                viewHolder = new AdapterServicesLoadedC.ItemViewHolderNoRating(view2);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_layout_service_loaded_no_rating_c, parent, false);
                viewHolder = new AdapterServicesLoadedC.ItemViewHolderNoRating(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVWITHRATING:
                AdapterServicesLoadedC.ItemViewHolder holder1 = (AdapterServicesLoadedC.ItemViewHolder)holder;
                JSONObject serviceItem1 = downLoadedSvcList.optJSONObject(position);
                if (TextUtils.isEmpty(serviceItem1.optString("name"))) {
                    holder1.txtServiceNameC.setText(catString);
                } else {
                    holder1.txtServiceNameC.setText(serviceItem1.optString("name"));
                }
                holder1.txtServiceDistanceC.setText(serviceItem1.optString("distance"));
                String tempRate = serviceItem1.optString("rating") + " (" + serviceItem1.optString("reviewNo")
                        + " reviews)";
                holder1.txtServiceRatingC.setText(tempRate);
                holder1.txtServiceDetailC.setText(serviceItem1.optString("detail"));

                String tempEmail = serviceItem1.optString("email");
                int collectedNo = collectedServiceList.size();
                for (int i = 0; i < collectedNo; i++) {
                    if (tempEmail.equals(collectedServiceList.get(i).getEmail())) {
                        holder1.imgHeartC.setImageResource(R.drawable.heartfull);
                        holder1.imgHeartC.setClickable(false);
                        holder1.imgHeartC.setEnabled(false);
                    } else {
                        holder1.imgHeartC.setImageResource(R.drawable.heart);
                        holder1.imgHeartC.setClickable(true);
                        holder1.imgHeartC.setEnabled(true);
                    }
                }

                ImageView[] starArr = {holder1.imgStar1C, holder1.imgStar2C, holder1.imgStar3C,
                        holder1.imgStar4C, holder1.imgStar5C};
                for (int ii = 0; ii < 5; ii++) {
                    starArr[ii].setImageResource(R.drawable.stargrey);
                }

                float rateFloat = Float.parseFloat(serviceItem1.optString("rating"));
                int rateInt = (int) Math.floor(rateFloat);

                for (int ii = 0; ii < rateInt; ii++) {
                    starArr[ii].setImageResource(R.drawable.starorange);
                }

                float remainR = rateFloat - (float) Math.floor(rateFloat);
                if (remainR > 0.24 && remainR < 0.75) {
                    starArr[rateInt].setImageResource(R.drawable.starhalforange);
                } else if (remainR >= 0.75) {
                    starArr[rateInt].setImageResource(R.drawable.starorange);
                }
           //     holder1.bind(serviceItem1, listener);
                holder1.clickItem(serviceItem1, listener);
                holder1.likeSvc(serviceItem1, listener);
                break;
            case CVWITHOUTRATING:
                AdapterServicesLoadedC.ItemViewHolderNoRating holder2 =
                        (AdapterServicesLoadedC.ItemViewHolderNoRating)holder;

                JSONObject serviceItem2 = downLoadedSvcList.optJSONObject(position);
                if (TextUtils.isEmpty(serviceItem2.optString("name"))) {
                    holder2.txtServiceNameNoRatingC.setText(catString);
                } else {
                    holder2.txtServiceNameNoRatingC.setText(serviceItem2.optString("name"));
                }
                holder2.txtServiceDistanceNoRatingC.setText(serviceItem2.optString("distance"));
                holder2.txtServiceRatingNoRatingC.setText("No reviews yet.");
                holder2.txtServiceDetailNoRatingC.setText(serviceItem2.optString("detail"));
                String tempEmail2 = serviceItem2.optString("email");
                int collectedNo2 = collectedServiceList.size();
                for (int i = 0; i < collectedNo2; i++) {
                    if (tempEmail2.equals(collectedServiceList.get(i).getEmail())) {
                        holder2.imgHeartNoRatingC.setImageResource(R.drawable.heartfull);
                        holder2.imgHeartNoRatingC.setClickable(false);
                        holder2.imgHeartNoRatingC.setEnabled(false);
                    } else {
                        holder2.imgHeartNoRatingC.setImageResource(R.drawable.heart);
                        holder2.imgHeartNoRatingC.setClickable(true);
                        holder2.imgHeartNoRatingC.setEnabled(true);
                    }
                }
           //     holder2.bind(serviceItem2, listener);
                holder2.clickItem(serviceItem2, listener);
                holder2.likeSvc(serviceItem2, listener);
                break;
            default:

                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemServicesLoadedC;

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

            cvItemServicesLoadedC = (CardView)itemView.findViewById(R.id.cvItem_service_loaded_c);

            txtServiceNameC = (TextView)itemView.findViewById(R.id.txt_service_loaded_name_c);
            txtServiceDistanceC = (TextView)itemView.findViewById(R.id.txt_service_loaded_distance_c);

            imgStar1C = (ImageView)itemView.findViewById(R.id.img_service_loaded_star1_c);
            imgStar2C = (ImageView)itemView.findViewById(R.id.img_service_loaded_star2_c);
            imgStar3C = (ImageView)itemView.findViewById(R.id.img_service_loaded_star3_c);
            imgStar4C = (ImageView)itemView.findViewById(R.id.img_service_loaded_star4_c);
            imgStar5C = (ImageView)itemView.findViewById(R.id.img_service_loaded_star5_c);
            txtServiceRatingC = (TextView)itemView.findViewById(R.id.txt_service_loaded_rating_c);
            imgHeartC = (ImageView)itemView.findViewById(R.id.img_service_loaded_heart_c);

            txtServiceDetailC = (TextView)itemView.findViewById(R.id.txt_service_loaded_detail_c);
        }

        public void clickItem(final JSONObject svcItem, final AdapterServicesLoadedC.OnItemClickAndLikeListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(svcItem, getAdapterPosition());
                }
            });
        }
        public void likeSvc(final JSONObject svcItem, final AdapterServicesLoadedC.OnItemClickAndLikeListener listener) {
            imgHeartC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgHeartC.setImageResource(R.drawable.heartfull);
                    imgHeartC.setClickable(false);
                    imgHeartC.setEnabled(false);
                    listener.onLike(svcItem, getAdapterPosition());
                }
            });
        }
    }

    public static class ItemViewHolderNoRating extends RecyclerView.ViewHolder {

        public CardView cvItemServicesLoadedNoRatingC;

        public TextView txtServiceNameNoRatingC;
        public TextView txtServiceDistanceNoRatingC;

        public TextView txtServiceRatingNoRatingC;
        public ImageView imgHeartNoRatingC;

        public TextView txtServiceDetailNoRatingC;

        public ItemViewHolderNoRating(View itemView) {
            super(itemView);

            cvItemServicesLoadedNoRatingC = (CardView)itemView.findViewById(R.id.cvItem_services_loaded_no_rating_c);

            txtServiceNameNoRatingC = (TextView)itemView.findViewById(R.id.txt_service_loaded_name_no_rating_c);
            txtServiceDistanceNoRatingC = (TextView)itemView.findViewById(R.id.txt_service_loaded_distance_no_rating_c);

            txtServiceRatingNoRatingC = (TextView)itemView.findViewById(R.id.txt_service_loaded_rating_no_rating_c);
            imgHeartNoRatingC = (ImageView)itemView.findViewById(R.id.img_service_loaded_heart_no_rating_c);

            txtServiceDetailNoRatingC = (TextView)itemView.findViewById(R.id.txt_service_loaded_detail_no_rating_c);
        }

        public void clickItem(final JSONObject svcItem, final AdapterServicesLoadedC.OnItemClickAndLikeListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(svcItem, getAdapterPosition());
                }
            });
        }
        public void likeSvc(final JSONObject svcItem, final AdapterServicesLoadedC.OnItemClickAndLikeListener listener) {
            imgHeartNoRatingC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgHeartNoRatingC.setImageResource(R.drawable.heartfull);
                    imgHeartNoRatingC.setClickable(false);
                    imgHeartNoRatingC.setEnabled(false);
                    listener.onLike(svcItem, getAdapterPosition());
                }
            });
        }

    }

}
