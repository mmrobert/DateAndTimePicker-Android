package com.homearound.www.homearound;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homearound.www.homearound.db.MerchantMessage;

import java.util.List;

/**
 * Created by boqiancheng on 2016-12-06.
 */

public class AdapterPersonalMsgM extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVMEM = 650;
    public static final int CVOTHERM = 651;

    // need re-consider from from internet
    private List<MerchantMessage> merchantMessageList;
    private String imgString;

    public AdapterPersonalMsgM(List<MerchantMessage> merchantMessageList, String imgString) {
        this.merchantMessageList = merchantMessageList;
        this.imgString = imgString;
    }

    @Override
    public int getItemCount() {
        return merchantMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
       // return super.getItemViewType(position);
        String direc = merchantMessageList.get(position).getDirection();
        if (direc.equals("in")) {
            return CVOTHERM;
        } else {
            return CVMEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVMEM:
                View view1 = inflater.inflate(R.layout.cardview_personal_msg_me_m, parent, false);
                viewHolder = new AdapterPersonalMsgM.ItemViewHolderMe(view1);
                break;
            case CVOTHERM:
                View view2 = inflater.inflate(R.layout.cardview_personal_msg_other_m, parent, false);
                viewHolder = new AdapterPersonalMsgM.ItemViewHolderOther(view2);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_personal_msg_other_m, parent, false);
                viewHolder = new AdapterPersonalMsgM.ItemViewHolderOther(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVMEM:
                AdapterPersonalMsgM.ItemViewHolderMe holder1 = (AdapterPersonalMsgM.ItemViewHolderMe)holder;

                if (!TextUtils.isEmpty(imgString)) {
                    byte[] decodedByte = Base64.decode(imgString, Base64.DEFAULT);
                    Bitmap bitmapImg = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                    holder1.imgMeM.setImageBitmap(bitmapImg);
                } else {
                    holder1.imgMeM.setImageResource(R.drawable.user);
                }
                MerchantMessage messageMe = merchantMessageList.get(position);

                holder1.txtTimeMeM.setText(messageMe.getMessagetime());
                holder1.txtMsgMeM.setText(messageMe.getMessagebody());
                //     holder1.bind(serviceItem1, listener);
                break;
            case CVOTHERM:
                AdapterPersonalMsgM.ItemViewHolderOther holder2 =
                        (AdapterPersonalMsgM.ItemViewHolderOther)holder;

                MerchantMessage messageOther = merchantMessageList.get(position);

                holder2.txtNameOtherM.setText(messageOther.getName());
                holder2.txtTimeOtherM.setText(messageOther.getMessagetime());
                holder2.txtMsgOtherM.setText(messageOther.getMessagebody());
                //     holder2.bind(serviceItem2, listener);
                break;
            default:

                break;
        }
    }

    public static class ItemViewHolderMe extends RecyclerView.ViewHolder {

        public CardView cvItemPersonalMsgMeM;

        public TextView txtTimeMeM;

        public ImageView imgMeM;

        public TextView txtMsgMeM;

        public ItemViewHolderMe(View itemView) {
            super(itemView);

            cvItemPersonalMsgMeM = (CardView)itemView.findViewById(R.id.cvItem_personal_msg_me_m);

            txtTimeMeM = (TextView)itemView.findViewById(R.id.txt_personal_msg_me_time_m);

            imgMeM = (ImageView)itemView.findViewById(R.id.img_personal_msg_me_m);

            txtMsgMeM = (TextView)itemView.findViewById(R.id.txt_personal_msg_me_msg_m);
        }

    }

    public static class ItemViewHolderOther extends RecyclerView.ViewHolder {

        public CardView cvItemPersonalMsgOtherM;

        public TextView txtNameOtherM;
        public TextView txtTimeOtherM;

        public TextView txtMsgOtherM;

        public ItemViewHolderOther(View itemView) {
            super(itemView);

            cvItemPersonalMsgOtherM = (CardView)itemView.findViewById(R.id.cvItem_personal_msg_other_m);

            txtNameOtherM = (TextView)itemView.findViewById(R.id.txt_personal_msg_other_name_m);

            txtTimeOtherM = (TextView) itemView.findViewById(R.id.txt_personal_msg_other_time_m);

            txtMsgOtherM = (TextView)itemView.findViewById(R.id.txt_personal_msg_other_msg_m);
        }

    }
}
