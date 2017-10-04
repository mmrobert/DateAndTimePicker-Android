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

import java.util.ArrayList;

/**
 * Created by boqiancheng on 2016-11-25.
 */

public class AdapterPromotionsC extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CVPROMOTIONS = 611666;
    public static final int CVNOPROS = 622666;

    private ArrayList<String> downPromotionsList;
    // need re-consider from from internet

    public interface OnItemClickListener {
        void onItemClick(String item, int itemClickPos);
    }

    private AdapterPromotionsC.OnItemClickListener listener;

    public AdapterPromotionsC(ArrayList<String> downPromotionsList,
                              AdapterPromotionsC.OnItemClickListener listener) {
        this.downPromotionsList = downPromotionsList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        if (downPromotionsList.size() > 0) {
            return downPromotionsList.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);
        if (downPromotionsList.size() > 0) {
            return CVPROMOTIONS;
        } else {
            return CVNOPROS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case CVPROMOTIONS:
                View view1 = inflater.inflate(R.layout.cardview_promotions_c, parent, false);
                viewHolder = new AdapterPromotionsC.ItemViewHolder(view1);
                break;
            case CVNOPROS:
                View view2 = inflater.inflate(R.layout.cardview_promotions_no_pro_c, parent, false);
                viewHolder = new AdapterPromotionsC.ItemViewHolderNoPro(view2);
                break;
            default:
                View view = inflater.inflate(R.layout.cardview_promotions_no_pro_c, parent, false);
                viewHolder = new AdapterPromotionsC.ItemViewHolderNoPro(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewTypeHere = holder.getItemViewType();

        switch (viewTypeHere) {
            case CVPROMOTIONS:
                AdapterPromotionsC.ItemViewHolder holder1 = (AdapterPromotionsC.ItemViewHolder)holder;
                String proItem1 = downPromotionsList.get(position);

                if (!TextUtils.isEmpty(proItem1)) {
                    byte[] decodedByte = Base64.decode(proItem1, Base64.DEFAULT);
                    Bitmap bitmapImg = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                    holder1.imgPromotion.setImageBitmap(bitmapImg);

                    holder1.clickItem(proItem1, listener);
                }
                //     holder1.bind(serviceItem1, listener);
                break;
            case CVNOPROS:
                AdapterPromotionsC.ItemViewHolderNoPro holder2 =
                        (AdapterPromotionsC.ItemViewHolderNoPro)holder;

                holder2.txtNoPro.setText("No Promotions Now.");
                //     holder2.bind(serviceItem2, listener);
                break;
            default:

                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvItemPromotionsC;

        public ImageView imgPromotion;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cvItemPromotionsC = (CardView)itemView.findViewById(R.id.cvItem_promotions_c);

            imgPromotion = (ImageView)itemView.findViewById(R.id.img_promotions_c);
        }

        public void clickItem(final String proItem, final AdapterPromotionsC.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(proItem, getAdapterPosition());
                }
            });
        }
    }

    public static class ItemViewHolderNoPro extends RecyclerView.ViewHolder {

        public CardView cvItemPromotionsNoProC;

        public TextView txtNoPro;

        public ItemViewHolderNoPro(View itemView) {
            super(itemView);

            cvItemPromotionsNoProC = (CardView)itemView.findViewById(R.id.cvItem_promotions_no_pro_c);

            txtNoPro = (TextView) itemView.findViewById(R.id.txt_promotions_no_pro_c);
        }
    }
}
