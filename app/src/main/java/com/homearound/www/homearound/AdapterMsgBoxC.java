package com.homearound.www.homearound;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.homearound.www.homearound.db.CustomerMessageBox;

import java.util.List;

/**
 * Created by boqiancheng on 2016-11-20.
 */

public class AdapterMsgBoxC extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVNORMAL = 6680;
    public static final int CVDELETE = 6681;

    // need re-consider from from internet
    private List<CustomerMessageBox> msgBoxList;
    // 0 for normal, 1 for delete
    private int normalOrDelete;

    public interface OnItemClickAndDeleteListener {
        void onItemClick(CustomerMessageBox boxItem, int itemClickPos);
        void onDelete(CustomerMessageBox boxItem, int itemClickPos);
    }

    private AdapterMsgBoxC.OnItemClickAndDeleteListener listener;

    public AdapterMsgBoxC(List<CustomerMessageBox> msgBoxList, int normalOrDelete,
                          AdapterMsgBoxC.OnItemClickAndDeleteListener listener) {
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
            return CVNORMAL;
        } else {
            return CVDELETE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVNORMAL:
                View view1 = inflater.inflate(R.layout.cardview_msg_box_c, parent, false);
                viewHolder = new AdapterMsgBoxC.ItemViewHolder(view1);
                break;
            case CVDELETE:
                View view2 = inflater.inflate(R.layout.cardview_msg_box_delete_c, parent, false);
                viewHolder = new AdapterMsgBoxC.ItemViewHolderDelete(view2);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_msg_box_c, parent, false);
                viewHolder = new AdapterMsgBoxC.ItemViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVNORMAL:
                AdapterMsgBoxC.ItemViewHolder holder1 = (AdapterMsgBoxC.ItemViewHolder)holder;
                CustomerMessageBox box = msgBoxList.get(position);

                holder1.txtMsgBoxNameC.setText(box.getName());
                holder1.txtMsgBoxTimeC.setText(box.getTimelastmessage());
                holder1.txtMsgBoxMsgC.setText(box.getLastmessage());
                //     holder1.bind(serviceItem1, listener);
                holder1.clickItem(box, listener);
                break;
            case CVDELETE:
                AdapterMsgBoxC.ItemViewHolderDelete holder2 =
                        (AdapterMsgBoxC.ItemViewHolderDelete)holder;

                CustomerMessageBox box1 = msgBoxList.get(position);

                holder2.txtMsgBoxNameDeleteC.setText(box1.getName());
                holder2.txtMsgBoxTimeDeleteC.setText(box1.getTimelastmessage());
                holder2.txtMsgBoxMsgDeleteC.setText(box1.getLastmessage());
                //     holder2.bind(serviceItem2, listener);
                holder2.deleteItem(box1, listener);
                break;
            default:

                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemMsgBoxC;

        public TextView txtMsgBoxNameC;
        public TextView txtMsgBoxTimeC;
        public TextView txtMsgBoxMsgC;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemMsgBoxC = (CardView)itemView.findViewById(R.id.cvItem_msg_box_c);

            txtMsgBoxNameC = (TextView)itemView.findViewById(R.id.txt_msg_box_name_c);
            txtMsgBoxTimeC = (TextView)itemView.findViewById(R.id.txt_msg_box_time_c);
            txtMsgBoxMsgC = (TextView)itemView.findViewById(R.id.txt_msg_box_msg_c);
        }

        public void clickItem(final CustomerMessageBox item, final AdapterMsgBoxC.OnItemClickAndDeleteListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, getAdapterPosition());
                }
            });
        }
    }

    public static class ItemViewHolderDelete extends RecyclerView.ViewHolder {

        public CardView cvItemMsgBoxDeleteC;

        public TextView txtMsgBoxNameDeleteC;
        public TextView txtMsgBoxTimeDeleteC;
        public TextView txtMsgBoxMsgDeleteC;

        public Button btnMsgBoxDeleteC;

        public ItemViewHolderDelete(View itemView) {
            super(itemView);

            cvItemMsgBoxDeleteC = (CardView)itemView.findViewById(R.id.cvItem_msg_box_delete_c);

            txtMsgBoxNameDeleteC = (TextView)itemView.findViewById(R.id.txt_msg_box_name_delete_c);
            txtMsgBoxTimeDeleteC = (TextView)itemView.findViewById(R.id.txt_msg_box_time_delete_c);
            txtMsgBoxMsgDeleteC = (TextView)itemView.findViewById(R.id.txt_msg_box_msg_delete_c);

            btnMsgBoxDeleteC = (Button)itemView.findViewById(R.id.btn_msg_box_delete_c);
        }

        public void deleteItem(final CustomerMessageBox item, final AdapterMsgBoxC.OnItemClickAndDeleteListener listener) {
            btnMsgBoxDeleteC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelete(item, getAdapterPosition());
                }
            });
        }
    }
}
