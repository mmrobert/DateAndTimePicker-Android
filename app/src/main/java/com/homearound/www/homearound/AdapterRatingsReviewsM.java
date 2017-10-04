package com.homearound.www.homearound;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by boqiancheng on 2016-12-07.
 */

public class AdapterRatingsReviewsM extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVRATINGOVERALL = 8910;
    public static final int CVRATINGITEM = 8911;
    public static final int CVRATINGSNO = 8912;

    private boolean hasReview;
    private String overallRate;
    private String reviewNo;
    // need re-consider from from internet
    private JSONArray reviewList;

    public AdapterRatingsReviewsM(boolean hasReview, String overallRate,
                                  String reviewNo, JSONArray reviewList) {
        this.hasReview = hasReview;
        this.overallRate = overallRate;
        this.reviewNo = reviewNo;
        this.reviewList = reviewList;
    }

    public void setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
    }

    public void setOverallRate(String overallRate) {
        this.overallRate = overallRate;
    }

    public void setReviewNo(String reviewNo) {
        this.reviewNo = reviewNo;
    }

    @Override
    public int getItemCount() {
        if (hasReview) {
            return reviewList.length() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);
        if (hasReview) {
            if (position == 0) {
                return CVRATINGOVERALL;
            } else {
                return CVRATINGITEM;
            }
        } else {
            return CVRATINGSNO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVRATINGOVERALL:
                View view1 = inflater.inflate(R.layout.cardview_ratings_reviews_overall_m, parent, false);
                viewHolder = new AdapterRatingsReviewsM.ItemViewHolderOverall(view1);
                break;
            case CVRATINGITEM:
                View view2 = inflater.inflate(R.layout.cardview_ratings_reviews_item_m, parent, false);
                viewHolder = new AdapterRatingsReviewsM.ItemViewHolderItem(view2);
                break;
            case CVRATINGSNO:
                View view3 = inflater.inflate(R.layout.cardview_ratings_reviews_no_m, parent, false);
                viewHolder = new AdapterRatingsReviewsM.ItemViewHolderNoReview(view3);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_ratings_reviews_no_m, parent, false);
                viewHolder = new AdapterRatingsReviewsM.ItemViewHolderNoReview(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVRATINGOVERALL:
                AdapterRatingsReviewsM.ItemViewHolderOverall holder1 =
                        (AdapterRatingsReviewsM.ItemViewHolderOverall)holder;

                holder1.txtOverallValueM.setText(overallRate);

                String tempR = reviewNo + " reviews";

                holder1.txtOverallNoM.setText(tempR);

                ImageView[] starArr = {holder1.imgStar1M, holder1.imgStar2M, holder1.imgStar3M,
                        holder1.imgStar4M, holder1.imgStar5M};
                for (int ii = 0; ii < 5; ii++) {
                    starArr[ii].setImageResource(R.drawable.stargrey);
                }

                float tempRate = Float.parseFloat(overallRate);
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

                break;
            case CVRATINGITEM:
                AdapterRatingsReviewsM.ItemViewHolderItem holder2 =
                        (AdapterRatingsReviewsM.ItemViewHolderItem)holder;

                JSONObject reviewItem = reviewList.optJSONObject(position - 1);

                holder2.txtItemValueM.setText(Double.toString(reviewItem.optDouble("rating")));
                holder2.txtItemTimeM.setText(reviewItem.optString("time"));
                holder2.txtItemNameM.setText(reviewItem.optString("name"));
                holder2.txtItemContentM.setText(reviewItem.optString("content"));

                ImageView[] starArrR = {holder2.imgStar1M, holder2.imgStar2M, holder2.imgStar3M,
                        holder2.imgStar4M, holder2.imgStar5M};
                for (int ii = 0; ii < 5; ii++) {
                    starArrR[ii].setImageResource(R.drawable.stargrey);
                }

                float tempRateR = (float) reviewItem.optDouble("rating");
                int tempRateIntR = (int) Math.floor(tempRateR);

                for (int ii = 0; ii < tempRateIntR; ii++) {
                    starArrR[ii].setImageResource(R.drawable.starorange);
                }

                float remainRR = tempRateR - (float) Math.floor(tempRateR);
                if (remainRR > 0.24 && remainRR < 0.75) {
                    starArrR[tempRateIntR].setImageResource(R.drawable.starhalforange);
                } else if (remainRR >= 0.75) {
                    starArrR[tempRateIntR].setImageResource(R.drawable.starorange);
                }
                break;
            case CVRATINGSNO:
                AdapterRatingsReviewsM.ItemViewHolderNoReview holder3 =
                        (AdapterRatingsReviewsM.ItemViewHolderNoReview)holder;

                holder3.txtNoReviewM.setText("No reviews yet.");
                break;
            default:

                break;
        }
    }

    public static class ItemViewHolderOverall extends RecyclerView.ViewHolder {

        public CardView cvItemRatingsReviewsOverallM;

        public TextView txtOverallValueM;
        public TextView txtOverallNoM;

        public ImageView imgStar1M;
        public ImageView imgStar2M;
        public ImageView imgStar3M;
        public ImageView imgStar4M;
        public ImageView imgStar5M;

        public ItemViewHolderOverall(View itemView) {
            super(itemView);

            cvItemRatingsReviewsOverallM =
                    (CardView)itemView.findViewById(R.id.cvItem_ratings_reviews_overall_m);

            txtOverallValueM = (TextView)itemView.findViewById(R.id.txt_ratings_reviews_overall_value_m);
            txtOverallNoM = (TextView)itemView.findViewById(R.id.txt_ratings_reviews_overall_review_no_m);

            imgStar1M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_overall_star1_m);
            imgStar2M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_overall_star2_m);
            imgStar3M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_overall_star3_m);
            imgStar4M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_overall_star4_m);
            imgStar5M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_overall_star5_m);
        }
    }

    public static class ItemViewHolderItem extends RecyclerView.ViewHolder {

        public CardView cvItemRatingsReviewsItemM;

        public TextView txtItemValueM;
        public TextView txtItemTimeM;
        public TextView txtItemNameM;
        public TextView txtItemContentM;

        public ImageView imgStar1M;
        public ImageView imgStar2M;
        public ImageView imgStar3M;
        public ImageView imgStar4M;
        public ImageView imgStar5M;

        public ItemViewHolderItem(View itemView) {
            super(itemView);

            cvItemRatingsReviewsItemM =
                    (CardView)itemView.findViewById(R.id.cvItem_ratings_reviews_item_m);

            txtItemValueM =
                    (TextView)itemView.findViewById(R.id.txt_ratings_reviews_item_value_m);
            txtItemTimeM =
                    (TextView)itemView.findViewById(R.id.txt_ratings_reviews_item_time_m);
            txtItemNameM =
                    (TextView)itemView.findViewById(R.id.txt_ratings_reviews_item_name_m);
            txtItemContentM =
                    (TextView)itemView.findViewById(R.id.txt_ratings_reviews_item_content_m);

            imgStar1M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_item_star1_m);
            imgStar2M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_item_star2_m);
            imgStar3M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_item_star3_m);
            imgStar4M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_item_star4_m);
            imgStar5M = (ImageView)itemView.findViewById(R.id.img_ratings_reviews_item_star5_m);
        }
    }

    public static class ItemViewHolderNoReview extends RecyclerView.ViewHolder {

        public CardView cvItemRatingsReviewsNoM;

        public TextView txtNoReviewM;

        public ItemViewHolderNoReview(View itemView) {
            super(itemView);

            cvItemRatingsReviewsNoM =
                    (CardView)itemView.findViewById(R.id.cvItem_ratings_reviews_no_m);

            txtNoReviewM = (TextView)itemView.findViewById(R.id.txt_ratings_reviews_no_m);
        }
    }
}
