package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.realestate.PropertyDetailsActivity;
import com.apps.realestate.R;
import com.example.item.ItemProperty;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.github.ornolfr.ratingview.RatingView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ItemRowHolder> {

    private ArrayList<ItemProperty> dataList;
    private Context mContext;
    private InterstitialAd mInterstitial;
    private int AD_COUNT = 0;

    public PropertyAdapter(Context context, ArrayList<ItemProperty> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_estate_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, final int position) {
        final ItemProperty singleItem = dataList.get(position);
        holder.text.setText(singleItem.getPropertyName());
        holder.textPrice.setText(singleItem.getPropertyPrice());
        holder.textAddress.setText(singleItem.getPropertyAddress());
        String text = mContext.getString(R.string.bed_bath, singleItem.getPropertyBed(), singleItem.getPropertyBath(), singleItem.getPropertyArea());
        holder.textBed.setText(text);
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRateAvg()));
        Picasso.with(mContext).load(singleItem.getPropertyThumbnailB()).placeholder(R.drawable.header_top_logo).into(holder.image);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {
                    AD_COUNT++;
                    if (AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                        AD_COUNT = 0;
                        mInterstitial = new InterstitialAd(mContext);
                        mInterstitial.setAdUnitId(Constant.SAVE_ADS_FULL_ID);
                        AdRequest adRequest;
                        if (JsonUtils.personalization_ad) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        mInterstitial.loadAd(adRequest);
                        mInterstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // TODO Auto-generated method stub
                                super.onAdLoaded();
                                if (mInterstitial.isLoaded()) {
                                    mInterstitial.show();
                                }
                            }

                            public void onAdClosed() {
                                Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                                intent.putExtra("Id", singleItem.getPId());
                                mContext.startActivity(intent);

                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                                intent.putExtra("Id", singleItem.getPId());
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                        intent.putExtra("Id", singleItem.getPId());
                        mContext.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                    intent.putExtra("Id", singleItem.getPId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.txtPurpose.setText(singleItem.getPropertyPurpose());
        holder.txtPurpose.setBackgroundResource(singleItem.getPropertyPurpose().equals("Rent") ? R.drawable.rounded_button : R.drawable.rounded_yellow_button);
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text, textPrice, textAddress, textBed, txtPurpose;
        public LinearLayout lyt_parent;
        public RatingView ratingView;

        public ItemRowHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            textAddress = (TextView) itemView.findViewById(R.id.textAddress);
            textBed = (TextView) itemView.findViewById(R.id.textBed);
            txtPurpose = (TextView) itemView.findViewById(R.id.textPurpose);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.rootLayout);
            ratingView = (RatingView) itemView.findViewById(R.id.ratingView);
        }
    }
}
