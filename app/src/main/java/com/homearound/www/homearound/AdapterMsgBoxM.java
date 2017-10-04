package com.homearound.www.homearound;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.homearound.www.homearound.db.MerchantMessageBox;

import java.util.List;

/**
 * Created by boqiancheng on 2016-12-03.
 */

public class AdapterMsgBoxM extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVNORMALM = 668089;
    public static final int CVDELETEM = 668189;

    // need re-consider from from internet
    private List<MerchantMessageBox> msgBoxList;
    // 0 for normal, 1 for delete
    private int normalOrDelete;

    public interface OnItemClickAndDeleteListener {
        void onItemClick(MerchantMessageBox boxItem, int itemClickPos);
        void onDelete(MerchantMessageBox boxItem, int itemClickPos);
    }

    private AdapterMsgBoxM.OnItemClickAndDeleteListener listener;

    public AdapterMsgBoxM(List<MerchantMessageBox> msgBoxList, int normalOrDelete,
                          AdapterMsgBoxM.OnItemClickAndDeleteListener listener) {
        this.msgBoxList = msgBoxList;
        this.normalOrDelete = normalOrDelete;
        this.listener = listener;
    }

    public void setNormalOrDelete(int inputND) {
        this.normalOrDelete = inputND;
    }

    @Override
    public int getItemCount() {
        return msgBoxList.size();
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);
        if (normalOrDelete == 0) {
            return CVNORMALM;
        } else {
            return CVDELETEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVNORMALM:
                View view1 = inflater.inflate(R.layout.cardview_msg_box_m, parent, false);
                viewHolder = new AdapterMsgBoxM.ItemViewHolder(view1);
                break;
            case CVDELETEM:
                View view2 = inflater.inflate(R.layout.cardview_msg_box_delete_m, parent, false);
                viewHolder = new AdapterMsgBoxM.ItemViewHolderDelete(view2);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_msg_box_m, parent, false);
                viewHolder = new AdapterMsgBoxM.ItemViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVNORMALM:
                AdapterMsgBoxM.ItemViewHolder holder1 = (AdapterMsgBoxM.ItemViewHolder)holder;
                MerchantMessageBox box = msgBoxList.get(position);

                holder1.txtMsgBoxNameM.setText(box.getName());
                holder1.txtMsgBoxTimeM.setText(box.getTimelastmessage());
                holder1.txtMsgBoxMsgM.setText(box.getLastmessage());
                //     holder1.bind(serviceItem1, listener);
                holder1.clickItem(box, listener);
                break;
            case CVDELETEM:
                AdapterMsgBoxM.ItemViewHolderDelete holder2 =
                        (AdapterMsgBoxM.ItemViewHolderDelete)holder;

                MerchantMessageBox box1 = msgBoxList.get(position);

                holder2.txtMsgBoxNameDeleteM.setText(box1.getName());
                holder2.txtMsgBoxTimeDeleteM.setText(box1.getTimelastmessage());
                holder2.txtMsgBoxMsgDeleteM.setText(box1.getLastmessage());
                //     holder2.bind(serviceItem2, listener);
                holder2.deleteItem(box1, listener);
                break;
            default:

                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemMsgBoxM;

        public TextView txtMsgBoxNameM;
        public TextView txtMsgBoxTimeM;
        public TextView txtMsgBoxMsgM;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemMsgBoxM = (CardView)itemView.findViewById(R.id.cvItem_msg_box_m);

            txtMsgBoxNameM = (TextView)itemView.findViewById(R.id.txt_msg_box_name_m);
            txtMsgBoxTimeM = (TextView)itemView.findViewById(R.id.txt_msg_box_time_m);
            txtMsgBoxMsgM = (TextView)itemView.findViewById(R.id.txt_msg_box_msg_m);
        }

        public void clickItem(final MerchantMessageBox item, final AdapterMsgBoxM.OnItemClickAndDeleteListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, getAdapterPosition());
                }
            });
        }
    }

    public static class ItemViewHolderDelete extends RecyclerView.ViewHolder {

        public CardView cvItemMsgBoxDeleteM;

        public TextView txtMsgBoxNameDeleteM;
        public TextView txtMsgBoxTimeDeleteM;
        public TextView txtMsgBoxMsgDeleteM;

        public Button btnMsgBoxDeleteM;

        public ItemViewHolderDelete(View itemView) {
            super(itemView);

            cvItemMsgBoxDeleteM = (CardView)itemView.findViewById(R.id.cvItem_msg_box_delete_m);

            txtMsgBoxNameDeleteM = (TextView)itemView.findViewById(R.id.txt_msg_box_name_delete_m);
            txtMsgBoxTimeDeleteM = (TextView)itemView.findViewById(R.id.txt_msg_box_time_delete_m);
            txtMsgBoxMsgDeleteM = (TextView)itemView.findViewById(R.id.txt_msg_box_msg_delete_m);

            btnMsgBoxDeleteM = (Button)itemView.findViewById(R.id.btn_msg_box_delete_m);
        }

        public void deleteItem(final MerchantMessageBox item, final AdapterMsgBoxM.OnItemClickAndDeleteListener listener) {
            btnMsgBoxDeleteM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelete(item, getAdapterPosition());
                }
            });
        }
    }
}
