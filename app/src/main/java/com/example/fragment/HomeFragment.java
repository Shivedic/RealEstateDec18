package com.example.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.apps.realestate.HomeMoreActivity;
import com.apps.realestate.R;
import com.example.adapter.HomeAdapter;
import com.example.db.DatabaseHelper;
import com.example.item.ItemProperty;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ArrayList<ItemProperty> mSliderList;
    private FragmentManager fragmentManager;
    RecyclerView mPopularView, mLatestView;
    HomeAdapter mPopularAdapter, mLatestAdapter;
    ArrayList<ItemProperty> mPopularList, mLatestList;
    Button btnPopular, btnLatest;
    RelativeLayout lytRecent;
    DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mPopularView = (RecyclerView) rootView.findViewById(R.id.rv_featured);
        mLatestView = (RecyclerView) rootView.findViewById(R.id.rv_latest);
        btnPopular = (Button) rootView.findViewById(R.id.btn_latest);
        btnLatest = (Button) rootView.findViewById(R.id.btn_featured);
        lytRecent = (RelativeLayout) rootView.findViewById(R.id.lyt_recent_view);

        mSliderList = new ArrayList<>();
        mPopularList = new ArrayList<>();
        mLatestList = new ArrayList<>();

        fragmentManager = getActivity().getSupportFragmentManager();

        mPopularView.setHasFixedSize(false);
        mPopularView.setNestedScrollingEnabled(false);
        mPopularView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        mPopularView.addItemDecoration(itemDecoration);

        mLatestView.setHasFixedSize(false);
        mLatestView.setNestedScrollingEnabled(false);
        mLatestView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mLatestView.addItemDecoration(itemDecoration);

        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeMoreActivity.class);
                intent.putExtra("isLatest", true);
                startActivity(intent);
            }
        });

        btnPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeMoreActivity.class);
                intent.putExtra("isLatest", false);
                startActivity(intent);
            }
        });


        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new Home().execute(Constant.HOME_URL);
        } else {
            showToast(getString(R.string.conne_msg1));
        }


        return rootView;
    }

    private class Home extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray jsonSlider = jsonArray.getJSONArray(Constant.HOME_FEATURED_ARRAY);
                    JSONObject objJsonSlider;
                    for (int i = 0; i < jsonSlider.length(); i++) {
                        objJsonSlider = jsonSlider.getJSONObject(i);
                        ItemProperty objItem = new ItemProperty();
                        objItem.setPId(objJsonSlider.getString(Constant.PROPERTY_ID));
                        objItem.setPropertyName(objJsonSlider.getString(Constant.PROPERTY_TITLE));
                        objItem.setPropertyThumbnailB(objJsonSlider.getString(Constant.PROPERTY_IMAGE));
                        objItem.setPropertyAddress(objJsonSlider.getString(Constant.PROPERTY_ADDRESS));
                        mSliderList.add(objItem);
                    }

                    JSONArray jsonLatest = jsonArray.getJSONArray(Constant.HOME_LATEST_ARRAY);
                    JSONObject objJson;
                    for (int i = 0; i < jsonLatest.length(); i++) {
                        objJson = jsonLatest.getJSONObject(i);
                        ItemProperty objItem = new ItemProperty();
                        objItem.setPId(objJson.getString(Constant.PROPERTY_ID));
                        objItem.setPropertyName(objJson.getString(Constant.PROPERTY_TITLE));
                        objItem.setPropertyThumbnailB(objJson.getString(Constant.PROPERTY_IMAGE));
                        objItem.setRateAvg(objJson.getString(Constant.PROPERTY_RATE));
                        objItem.setPropertyPrice(objJson.getString(Constant.PROPERTY_PRICE));
                        objItem.setPropertyBed(objJson.getString(Constant.PROPERTY_BED));
                        objItem.setPropertyBath(objJson.getString(Constant.PROPERTY_BATH));
                        objItem.setPropertyArea(objJson.getString(Constant.PROPERTY_AREA));
                        objItem.setPropertyAddress(objJson.getString(Constant.PROPERTY_ADDRESS));
                        objItem.setPropertyPurpose(objJson.getString(Constant.PROPERTY_PURPOSE));
                        mLatestList.add(objItem);
                    }

                    JSONArray jsonPopular = jsonArray.getJSONArray(Constant.HOME_POPULAR_ARRAY);
                    JSONObject objJsonPopular;
                    for (int i = 0; i < jsonPopular.length(); i++) {
                        objJsonPopular = jsonPopular.getJSONObject(i);
                        ItemProperty objItem = new ItemProperty();
                        objItem.setPId(objJsonPopular.getString(Constant.PROPERTY_ID));
                        objItem.setPropertyName(objJsonPopular.getString(Constant.PROPERTY_TITLE));
                        objItem.setPropertyThumbnailB(objJsonPopular.getString(Constant.PROPERTY_IMAGE));
                        objItem.setRateAvg(objJsonPopular.getString(Constant.PROPERTY_RATE));
                        objItem.setPropertyPrice(objJsonPopular.getString(Constant.PROPERTY_PRICE));
                        objItem.setPropertyBed(objJsonPopular.getString(Constant.PROPERTY_BED));
                        objItem.setPropertyBath(objJsonPopular.getString(Constant.PROPERTY_BATH));
                        objItem.setPropertyArea(objJsonPopular.getString(Constant.PROPERTY_AREA));
                        objItem.setPropertyAddress(objJsonPopular.getString(Constant.PROPERTY_ADDRESS));
                        objItem.setPropertyPurpose(objJsonPopular.getString(Constant.PROPERTY_PURPOSE));
                        mPopularList.add(objItem);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        mLatestAdapter = new HomeAdapter(getActivity(), mLatestList);
        mLatestView.setAdapter(mLatestAdapter);

        mPopularAdapter = new HomeAdapter(getActivity(), mPopularList);
        mPopularView.setAdapter(mPopularAdapter);

        if (!mSliderList.isEmpty()) {
            Constant.mList=mSliderList;
            SliderFragment sliderFragment = new SliderFragment();
            fragmentManager.beginTransaction().replace(R.id.ContainerSlider, sliderFragment).commit();
        }

    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

}
