package com.apps.realestate;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.db.DatabaseHelper;
import com.example.fragment.AmenitiesFragment;
import com.example.fragment.GalleryFragment;
import com.example.item.ItemProperty;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.github.ornolfr.ratingview.RatingView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;

public class PropertyDetailsActivity extends AppCompatActivity {

    ImageView imageFloor, imageMap;
    TextView txtName, txtAddress, txtPrice, txtBed, txtBath, txtArea, txtPhone, txtAmenities;
    WebView webView;
    Toolbar toolbar;
    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ItemProperty objBean;
    String Id;
    ArrayList<String> mGallery, mAmenities;
    private FragmentManager fragmentManager;
    RatingView ratingView;
    String rateMsg;
    Menu menu;
    DatabaseHelper databaseHelper;
    View view, view1;
    JsonUtils jsonUtils;
    LinearLayout adLayout;
    boolean iswhichscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.property_details));

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        databaseHelper = new DatabaseHelper(getApplicationContext());
        Intent i = getIntent();
        Id = i.getStringExtra("Id");

        fragmentManager = getSupportFragmentManager();

        objBean = new ItemProperty();
        mGallery = new ArrayList<>();
        mAmenities = new ArrayList<>();

        imageFloor = (ImageView) findViewById(R.id.image_floor);
        imageMap = (ImageView) findViewById(R.id.imageMap);
        txtName = (TextView) findViewById(R.id.text);
        txtAddress = (TextView) findViewById(R.id.textAddress);
        txtPrice = (TextView) findViewById(R.id.textPrice);
        txtBed = (TextView) findViewById(R.id.textBed);
        txtBath = (TextView) findViewById(R.id.textBath);
        txtArea = (TextView) findViewById(R.id.textArea);
        txtPhone = (TextView) findViewById(R.id.textPhone);
        txtAmenities = (TextView) findViewById(R.id.txtAmenities);
        view = findViewById(R.id.viewAmenities);
        view1 = findViewById(R.id.viewAmenities1);
        ratingView = (RatingView) findViewById(R.id.ratingView);

        webView = (WebView) findViewById(R.id.property_description);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        webView.setBackgroundColor(Color.TRANSPARENT);
        adLayout = (LinearLayout) findViewById(R.id.adview);
        Intent intent = getIntent();
        iswhichscreen = intent.getBooleanExtra("isNotification", false);
        if (!iswhichscreen) {
            if (JsonUtils.personalization_ad) {
                JsonUtils.showPersonalizedAds(adLayout, PropertyDetailsActivity.this);
            } else {
                JsonUtils.showNonPersonalizedAds(adLayout, PropertyDetailsActivity.this);
            }

        }


        if (JsonUtils.isNetworkAvailable(PropertyDetailsActivity.this)) {
            new getProperty().execute(Constant.SINGLE_PROPERTY_URL + Id);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        ratingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRating();
            }
        });

        imageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyDetailsActivity.this, MapActivity.class);
                intent.putExtra("latitude", objBean.getPropertyMapLatitude());
                intent.putExtra("longitude", objBean.getPropertyMapLongitude());
                intent.putExtra("title", objBean.getPropertyName());
                startActivity(intent);
            }
        });

    }

    private class getProperty extends AsyncTask<String, Void, String> {

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
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        objBean.setPId(objJson.getString(Constant.PROPERTY_ID));
                        objBean.setPropertyName(objJson.getString(Constant.PROPERTY_TITLE));
                        objBean.setPropertyThumbnailB(objJson.getString(Constant.PROPERTY_IMAGE));
                        objBean.setRateAvg(objJson.getString(Constant.PROPERTY_RATE));
                        objBean.setPropertyPrice(objJson.getString(Constant.PROPERTY_PRICE));
                        objBean.setPropertyBed(objJson.getString(Constant.PROPERTY_BED));
                        objBean.setPropertyBath(objJson.getString(Constant.PROPERTY_BATH));
                        objBean.setPropertyArea(objJson.getString(Constant.PROPERTY_AREA));
                        objBean.setPropertyAddress(objJson.getString(Constant.PROPERTY_ADDRESS));
                        objBean.setPropertyLocality(objJson.getString(Constant.PROPERTY_LOCALITY));
                        objBean.setPropertyCity(objJson.getString(Constant.PROPERTY_CITY));
                        objBean.setPropertyState(objJson.getString(Constant.PROPERTY_STATE));
                        objBean.setPropertyLocality(objJson.getString(Constant.PROPERTY_LOCALITY));
                        objBean.setPropertyPhone(objJson.getString(Constant.PROPERTY_PHONE));
                        objBean.setPropertyDescription(objJson.getString(Constant.PROPERTY_DESC));
                        objBean.setPropertyFloorPlan(objJson.getString(Constant.PROPERTY_FLOOR_PLAN));
                        objBean.setPropertyAmenities(objJson.getString(Constant.PROPERTY_AMENITIES));
                        objBean.setPropertyPurpose(objJson.getString(Constant.PROPERTY_PURPOSE));
                        objBean.setPropertyMapLatitude(objJson.getString(Constant.PROPERTY_LATITUDE));
                        objBean.setPropertyMapLongitude(objJson.getString(Constant.PROPERTY_LONGITUDE));

                        JSONArray jsonArrayGallery = objJson.getJSONArray(Constant.GALLERY_ARRAY_NAME);
                        if (jsonArrayGallery.length() != 0) {
                            for (int j = 0; j < jsonArrayGallery.length(); j++) {
                                JSONObject objChild = jsonArrayGallery.getJSONObject(j);
                                if (!objChild.has(Constant.SUCCESS)) {
                                    mGallery.add(objChild.getString(Constant.GALLERY_IMAGE_NAME));
                                } else {
                                    mGallery.add(objJson.getString(Constant.PROPERTY_IMAGE));
                                }

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        txtName.setText(objBean.getPropertyName());
        txtAddress.setText(objBean.getPropertyAddress());
        txtBath.setText(objBean.getPropertyBath() + " Bath");
        txtBed.setText(objBean.getPropertyBed() + " Bed");
        txtArea.setText(objBean.getPropertyArea());
        txtPhone.setText(objBean.getPropertyPhone());
        txtPrice.setText(objBean.getPropertyPrice());
        ratingView.setRating(Float.parseFloat(objBean.getRateAvg()));
        if (!objBean.getPropertyAmenities().isEmpty())
            mAmenities = new ArrayList<>(Arrays.asList(objBean.getPropertyAmenities().split(",")));


        Picasso.with(PropertyDetailsActivity.this).load(objBean.getPropertyFloorPlan()).placeholder(R.drawable.header_top_logo).into(imageFloor);

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = objBean.getPropertyDescription();

        String text = "<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #999999;text-align:left;font-size:14px;margin-left:0px}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        if (!mGallery.isEmpty()) {
            GalleryFragment sliderFragment = GalleryFragment.newInstance(mGallery);
            fragmentManager.beginTransaction().replace(R.id.ContainerGallery, sliderFragment).commit();
        }

        if (!objBean.getPropertyAmenities().isEmpty()) {
            AmenitiesFragment amenitiesFragment = AmenitiesFragment.newInstance(mAmenities);
            fragmentManager.beginTransaction().replace(R.id.ContainerAmenities, amenitiesFragment).commit();
        } else {
            txtAmenities.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        }

    }

    public void showToast(String msg) {
        Toast.makeText(PropertyDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showRating() {
        final String deviceId;
        final Dialog mDialog = new Dialog(PropertyDetailsActivity.this, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.rate_dialog);
        deviceId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        final RatingView ratingView = (RatingView) mDialog.findViewById(R.id.ratingView);
        ratingView.setRating(Float.parseFloat(objBean.getRateAvg()));
        Button button = (Button) mDialog.findViewById(R.id.btn_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JsonUtils.isNetworkAvailable(PropertyDetailsActivity.this)) {
                    new SentRating().execute(Constant.RATING_URL + Id + "&rate=" + ratingView.getRating() + "&device_id=" + deviceId);
                } else {
                    showToast(getString(R.string.conne_msg1));
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private class SentRating extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String Rate;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(PropertyDetailsActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {
                showToast("No data found from web!!!");

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        rateMsg = objJson.getString("MSG");
                        if (objJson.has(Constant.PROPERTY_RATE)) {
                            Rate = objJson.getString(Constant.PROPERTY_RATE);
                        } else {
                            Rate = "";
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setRate();
            }

        }

        public void setRate() {
            showToast(rateMsg);
            if (!Rate.isEmpty())
                ratingView.setRating(Float.parseFloat(Rate));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_property, menu);
        this.menu = menu;
        isFavourite();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_bookmark:
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(Id)) {
                    databaseHelper.removeFavouriteById(Id);
                    menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_white_24dp);
                    Toast.makeText(PropertyDetailsActivity.this, getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, Id);
                    fav.put(DatabaseHelper.KEY_TITLE, objBean.getPropertyName());
                    fav.put(DatabaseHelper.KEY_IMAGE, objBean.getPropertyThumbnailB());
                    fav.put(DatabaseHelper.KEY_RATE, objBean.getRateAvg());
                    fav.put(DatabaseHelper.KEY_BED, objBean.getPropertyBed());
                    fav.put(DatabaseHelper.KEY_BATH, objBean.getPropertyBath());
                    fav.put(DatabaseHelper.KEY_ADDRESS, objBean.getPropertyAddress());
                    fav.put(DatabaseHelper.KEY_AREA, objBean.getPropertyArea());
                    fav.put(DatabaseHelper.KEY_PRICE, objBean.getPropertyPrice());
                    fav.put(DatabaseHelper.KEY_PURPOSE, objBean.getPropertyPurpose());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    menu.getItem(0).setIcon(R.drawable.ic_bookmark_white_24dp);
                    Toast.makeText(PropertyDetailsActivity.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            menu.getItem(0).setIcon(R.drawable.ic_bookmark_white_24dp);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_white_24dp);
        }
    }
}
