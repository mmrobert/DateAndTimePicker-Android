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

import com.homearound.www.homearound.db.CustomerMessage;

import java.util.List;

/**
 * Created by boqiancheng on 2016-11-24.
 */

public class AdapterPersonalMsgC extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVME = 66550;
    public static final int CVOTHER = 66551;

    // need re-consider from from internet
    private List<CustomerMessage> customerMessageList;
    private String imgString;

    public AdapterPersonalMsgC(List<CustomerMessage> customerMessageList, String imgString) {
        this.customerMessageList = customerMessageList;
        this.imgString = imgString;
    }

    @Override
    public int getItemCount() {
        return customerMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);
        String direc = customerMessageList.get(position).getDirection();
        if (direc.equals("in")) {
            return CVOTHER;
        } else {
            return CVME;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVME:
                View view1 = inflater.inflate(R.layout.cardview_personal_msg_me_c, parent, false);
                viewHolder = new AdapterPersonalMsgC.ItemViewHolderMe(view1);
                break;
            case CVOTHER:
                View view2 = inflater.inflate(R.layout.cardview_personal_msg_other_c, parent, false);
                viewHolder = new AdapterPersonalMsgC.ItemViewHolderOther(view2);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_personal_msg_other_c, parent, false);
                viewHolder = new AdapterPersonalMsgC.ItemViewHolderOther(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVME:
                AdapterPersonalMsgC.ItemViewHolderMe holder1 = (AdapterPersonalMsgC.ItemViewHolderMe)holder;

                if (!TextUtils.isEmpty(imgString)) {
                    byte[] decodedByte = Base64.decode(imgString, Base64.DEFAULT);
                    Bitmap bitmapImg = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                    holder1.imgMeC.setImageBitmap(bitmapImg);
                } else {
                    holder1.imgMeC.setImageResource(R.drawable.user);
                }
                CustomerMessage messageMe = customerMessageList.get(position);

                holder1.txtTimeMeC.setText(messageMe.getMessagetime());
                holder1.txtMsgMeC.setText(messageMe.getMessagebody());
                //     holder1.bind(serviceItem1, listener);
                break;
            case CVOTHER:
                AdapterPersonalMsgC.ItemViewHolderOther holder2 =
                        (AdapterPersonalMsgC.ItemViewHolderOther)holder;

                CustomerMessage messageOther = customerMessageList.get(position);

                holder2.txtNameOtherC.setText(messageOther.getName());
                holder2.txtTimeOtherC.setText(messageOther.getMessagetime());
                holder2.txtMsgOtherC.setText(messageOther.getMessagebody());
                //     holder2.bind(serviceItem2, listener);
                break;
            default:

                break;
        }
    }

    public static class ItemViewHolderMe extends RecyclerView.ViewHolder {

        public CardView cvItemPersonalMsgMeC;

        public TextView txtTimeMeC;

        public ImageView imgMeC;

        public TextView txtMsgMeC;

        public ItemViewHolderMe(View itemView) {
            super(itemView);

            cvItemPersonalMsgMeC = (CardView)itemView.findViewById(R.id.cvItem_personal_msg_me_c);

            txtTimeMeC = (TextView)itemView.findViewById(R.id.txt_personal_msg_me_time_c);

            imgMeC = (ImageView)itemView.findViewById(R.id.img_personal_msg_me_c);

            txtMsgMeC = (TextView)itemView.findViewById(R.id.txt_personal_msg_me_msg_c);
        }

    }

    public static class ItemViewHolderOther extends RecyclerView.ViewHolder {

        public CardView cvItemPersonalMsgOtherC;

        public TextView txtNameOtherC;
        public TextView txtTimeOtherC;

        public TextView txtMsgOtherC;

        public ItemViewHolderOther(View itemView) {
            super(itemView);

            cvItemPersonalMsgOtherC = (CardView)itemView.findViewById(R.id.cvItem_personal_msg_other_c);

            txtNameOtherC = (TextView)itemView.findViewById(R.id.txt_personal_msg_other_name_c);

            txtTimeOtherC = (TextView) itemView.findViewById(R.id.txt_personal_msg_other_time_c);

            txtMsgOtherC = (TextView)itemView.findViewById(R.id.txt_personal_msg_other_msg_c);
        }

    }
}
