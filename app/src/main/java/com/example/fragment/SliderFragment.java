package com.example.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
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
import com.example.util.EnchantedViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderFragment extends Fragment {

    EnchantedViewPager mViewPager;
    CustomViewPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slider, container, false);
        mViewPager = (EnchantedViewPager) rootView.findViewById(R.id.viewPager);
        mViewPager.useScale();
        mViewPager.removeAlpha();
        mAdapter = new CustomViewPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        return rootView;
    }


    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        public CustomViewPagerAdapter() {
            // TODO Auto-generated constructor stub
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View imageLayout = inflater.inflate(R.layout.row_slider_item, container, false);
            assert imageLayout != null;
            ImageView image = (ImageView) imageLayout.findViewById(R.id.image);
            TextView text = (TextView) imageLayout.findViewById(R.id.text);
            TextView textAddress = (TextView) imageLayout.findViewById(R.id.textAddress);
            LinearLayout lytParent=(LinearLayout) imageLayout.findViewById(R.id.rootLayout);

            final ItemProperty ItemProperty = Constant.mList.get(position);

            text.setText(ItemProperty.getPropertyName());
            textAddress.setText(ItemProperty.getPropertyAddress());
            Picasso.with(getActivity()).load(ItemProperty.getPropertyThumbnailB()).placeholder(R.drawable.header_top_logo).into(image);
            imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
            lytParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
                    intent.putExtra("Id", ItemProperty.getPId());
                    startActivity(intent);
                }
            });
            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }
}
