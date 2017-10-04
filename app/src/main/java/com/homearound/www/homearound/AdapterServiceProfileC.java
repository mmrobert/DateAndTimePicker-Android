package com.homearound.www.homearound;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by boqiancheng on 2016-11-15.
 */

public class AdapterServiceProfileC extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVSUMMARYWITHRATING = 110;
    public static final int CVSUMMARYWITHOUTRATING = 111;
    public static final int CVREVIEW = 112;

    public interface OnLikeSendClickListener {
        void onLikeClick();
        void onSendClick();
    }

    private boolean hasReview;
    private String overallRate;
    private String reviewNo;
    private String sName;
    private String sDistance;
    private String sDetail;
    private String sCat;
    private boolean alreadyMyService;
    // need re-consider from from internet
    private JSONArray reviewList;
    //
    private AdapterServiceProfileC.OnLikeSendClickListener listener;

    public AdapterServiceProfileC(boolean hasReview, String overallRate, String reviewNo,
                                  String sName, String sDistance, String sDetail, String sCat,
                                  boolean alreadyMyService, JSONArray reviewList,
                              AdapterServiceProfileC.OnLikeSendClickListener listener) {
        this.hasReview = hasReview;
        this.overallRate = overallRate;
        this.reviewNo = reviewNo;

        this.sName = sName;
        this.sDistance = sDistance;
        this.sDetail = sDetail;
        this.sCat = sCat;

        this.alreadyMyService = alreadyMyService;

        this.reviewList = reviewList;
        this.listener = listener;
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

    public void setAlreadyMyService(boolean alreadyMyService) {
        this.alreadyMyService = alreadyMyService;
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
                return CVSUMMARYWITHRATING;
            } else {
                return CVREVIEW;
            }
        } else {
            return CVSUMMARYWITHOUTRATING;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVSUMMARYWITHRATING:
                View view1 = inflater.inflate(R.layout.cardview_service_profile_summary_c, parent, false);
                viewHolder = new AdapterServiceProfileC.ItemViewHolder(view1);
                break;
            case CVSUMMARYWITHOUTRATING:
                View view2 = inflater.inflate(R.layout.cardview_service_profile_summary_no_rating_c, parent, false);
                viewHolder = new AdapterServiceProfileC.ItemViewHolderNoRating(view2);
                break;
            case CVREVIEW:
                View view3 = inflater.inflate(R.layout.cardview_service_profile_review_c, parent, false);
                viewHolder = new AdapterServiceProfileC.ItemViewHolderReview(view3);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_service_profile_review_c, parent, false);
                viewHolder = new AdapterServiceProfileC.ItemViewHolderReview(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVSUMMARYWITHRATING:
                AdapterServiceProfileC.ItemViewHolder holder1 = (AdapterServiceProfileC.ItemViewHolder)holder;

                if (TextUtils.isEmpty(sName)) {
                    holder1.txtServiceProfileNameC.setText(sCat);
                } else {
                    holder1.txtServiceProfileNameC.setText(sName);
                }
                holder1.txtServiceProfileDistanceC.setText(sDistance);

                String tempR = overallRate + " (" + reviewNo + " reviews)";

                holder1.txtServiceProfileRatingC.setText(tempR);
                holder1.txtServiceProfileDetailC.setText(sDetail);

                ImageView[] starArr = {holder1.imgStar1C, holder1.imgStar2C, holder1.imgStar3C,
                        holder1.imgStar4C, holder1.imgStar5C};
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

                if (alreadyMyService) {
                    holder1.imgHeartC.setClickable(false);
                    holder1.imgHeartC.setEnabled(false);
                    holder1.imgHeartC.setImageResource(R.drawable.heartfull);
                } else {
                    holder1.imgHeartC.setClickable(true);
                    holder1.imgHeartC.setEnabled(true);
                    holder1.imgHeartC.setImageResource(R.drawable.heart);
                }

                holder1.likeTheService(listener);
                holder1.sendMsg(listener);
                break;
            case CVSUMMARYWITHOUTRATING:
                AdapterServiceProfileC.ItemViewHolderNoRating holder2 =
                        (AdapterServiceProfileC.ItemViewHolderNoRating)holder;

                if (TextUtils.isEmpty(sName)) {
                    holder2.txtServiceProfileNameNoRatingC.setText(sCat);
                } else {
                    holder2.txtServiceProfileNameNoRatingC.setText(sName);
                }
                holder2.txtServiceProfileDistanceNoRatingC.setText(sDistance);
                holder2.txtServiceProfileRatingNoRatingC.setText("No reviews yet.");
                holder2.txtServiceProfileDetailNoRatingC.setText(sDetail);

                if (alreadyMyService) {
                    holder2.imgHeartC.setClickable(false);
                    holder2.imgHeartC.setEnabled(false);
                    holder2.imgHeartC.setImageResource(R.drawable.heartfull);
                } else {
                    holder2.imgHeartC.setClickable(true);
                    holder2.imgHeartC.setEnabled(true);
                    holder2.imgHeartC.setImageResource(R.drawable.heart);
                }

                holder2.likeTheService(listener);
                holder2.sendMsg(listener);
                break;
            case CVREVIEW:
                AdapterServiceProfileC.ItemViewHolderReview holder3 =
                        (AdapterServiceProfileC.ItemViewHolderReview)holder;

                JSONObject reviewItem = reviewList.optJSONObject(position - 1);

                holder3.txtReviewTimeC.setText(reviewItem.optString("time"));
                holder3.txtReviewReviewerC.setText(reviewItem.optString("name"));
                holder3.txtReviewCommentC.setText(reviewItem.optString("content"));

                ImageView[] starArrR = {holder3.imgStar1C, holder3.imgStar2C, holder3.imgStar3C,
                        holder3.imgStar4C, holder3.imgStar5C};
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
            default:

                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemServiceProfileSummaryC;

        public TextView txtServiceProfileNameC;
        public TextView txtServiceProfileDistanceC;

        public ImageView imgStar1C;
        public ImageView imgStar2C;
        public ImageView imgStar3C;
        public ImageView imgStar4C;
        public ImageView imgStar5C;

        public TextView txtServiceProfileRatingC;
        public ImageView imgHeartC;

        public TextView txtServiceProfileDetailC;
        public Button btnSendMsg;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemServiceProfileSummaryC = (CardView)itemView.findViewById(R.id.cvItem_service_profile_summary_c);

            txtServiceProfileNameC = (TextView)itemView.findViewById(R.id.txt_service_profile_summary_name_c);
            txtServiceProfileDistanceC = (TextView)itemView.findViewById(R.id.txt_service_profile_summary_distance_c);

            imgStar1C = (ImageView)itemView.findViewById(R.id.img_service_profile_summary_star1_c);
            imgStar2C = (ImageView)itemView.findViewById(R.id.img_service_profile_summary_star2_c);
            imgStar3C = (ImageView)itemView.findViewById(R.id.img_service_profile_summary_star3_c);
            imgStar4C = (ImageView)itemView.findViewById(R.id.img_service_profile_summary_star4_c);
            imgStar5C = (ImageView)itemView.findViewById(R.id.img_service_profile_summary_star5_c);

            txtServiceProfileRatingC = (TextView)itemView.findViewById(R.id.txt_service_profile_summary_rating_c);
            imgHeartC = (ImageView)itemView.findViewById(R.id.img_service_profile_summary_heart_c);

            txtServiceProfileDetailC = (TextView)itemView.findViewById(R.id.txt_service_profile_summary_detail_c);

            btnSendMsg = (Button)itemView.findViewById(R.id.btn_service_profile_send_msg_c);
        }

        public void likeTheService(final AdapterServiceProfileC.OnLikeSendClickListener listener) {
            imgHeartC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgHeartC.setImageResource(R.drawable.heartfull);
                    imgHeartC.setClickable(false);
                    imgHeartC.setEnabled(false);
                    listener.onLikeClick();
                }
            });
        }

        public void sendMsg(final AdapterServiceProfileC.OnLikeSendClickListener listener) {
            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSendClick();
                }
            });
        }
    }

    public static class ItemViewHolderNoRating extends RecyclerView.ViewHolder {

        public CardView cvItemServiceProfileSummaryNoRatingC;

        public TextView txtServiceProfileNameNoRatingC;
        public TextView txtServiceProfileDistanceNoRatingC;

        public TextView txtServiceProfileRatingNoRatingC;
        public ImageView imgHeartC;

        public TextView txtServiceProfileDetailNoRatingC;
        public Button btnSendMsg;

        public ItemViewHolderNoRating(View itemView) {
            super(itemView);

            cvItemServiceProfileSummaryNoRatingC =
                    (CardView)itemView.findViewById(R.id.cvItem_service_profile_summary_no_rating_c);

            txtServiceProfileNameNoRatingC =
                    (TextView)itemView.findViewById(R.id.txt_service_profile_summary_name_no_rating_c);
            txtServiceProfileDistanceNoRatingC =
                    (TextView)itemView.findViewById(R.id.txt_service_profile_summary_distance_no_rating_c);

            txtServiceProfileRatingNoRatingC =
                    (TextView)itemView.findViewById(R.id.txt_service_profile_summary_rating_no_rating_c);
            imgHeartC = (ImageView)itemView.findViewById(R.id.img_service_profile_summary_heart_no_rating_c);

            txtServiceProfileDetailNoRatingC =
                    (TextView)itemView.findViewById(R.id.txt_service_profile_summary_detail_no_rating_c);

            btnSendMsg = (Button)itemView.findViewById(R.id.btn_service_profile_send_msg_no_rating_c);
        }

        public void likeTheService(final AdapterServiceProfileC.OnLikeSendClickListener listener) {
            imgHeartC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgHeartC.setImageResource(R.drawable.heartfull);
                    imgHeartC.setClickable(false);
                    imgHeartC.setEnabled(false);
                    listener.onLikeClick();
                }
            });
        }

        public void sendMsg(final AdapterServiceProfileC.OnLikeSendClickListener listener) {
            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSendClick();
                }
            });
        }
    }

    public static class ItemViewHolderReview extends RecyclerView.ViewHolder {

        public CardView cvItemServiceProfileReviewC;

        public ImageView imgStar1C;
        public ImageView imgStar2C;
        public ImageView imgStar3C;
        public ImageView imgStar4C;
        public ImageView imgStar5C;

        public TextView txtReviewTimeC;
        public TextView txtReviewReviewerC;

        public TextView txtReviewCommentC;

        public ItemViewHolderReview(View itemView) {
            super(itemView);

            cvItemServiceProfileReviewC =
                    (CardView)itemView.findViewById(R.id.cvItem_service_profile_review_c);

            imgStar1C = (ImageView)itemView.findViewById(R.id.img_service_profile_review_star1_c);
            imgStar2C = (ImageView)itemView.findViewById(R.id.img_service_profile_review_star2_c);
            imgStar3C = (ImageView)itemView.findViewById(R.id.img_service_profile_review_star3_c);
            imgStar4C = (ImageView)itemView.findViewById(R.id.img_service_profile_review_star4_c);
            imgStar5C = (ImageView)itemView.findViewById(R.id.img_service_profile_review_star5_c);

            txtReviewTimeC = (TextView)itemView.findViewById(R.id.txt_service_profile_review_time_c);

            txtReviewReviewerC = (TextView)itemView.findViewById(R.id.txt_service_profile_review_reviewer_c);

            txtReviewCommentC = (TextView)itemView.findViewById(R.id.txt_service_profile_review_comment_c);
        }
    }
}
