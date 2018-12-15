package com.apps.realestate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.item.ItemType;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Picasso;
import com.zanjou.http.debug.Logger;
import com.zanjou.http.request.FileUploadListener;
import com.zanjou.http.request.Request;
import com.zanjou.http.request.RequestStateListener;
import com.zanjou.http.response.JsonResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class AddPropertiesActivity extends AppCompatActivity implements Validator.ValidationListener {

    Toolbar toolbar;
    Spinner spinner_cat, spinner_pupose;
    ArrayList<ItemType> mListType;
    ArrayList<String> mPropertyName, mPropertyPurpose;
    ImageView img_feature, img_plan, img_gallery;
    RelativeLayout lay_feature, lay_plan, lay_gallery;
    Button lay_submit;
    TextView txtSelect1, txtSelect2, txtSelect3;
    private int REQUEST_FEATURED_PICKER = 2000;
    private int REQUEST_FEATURED_PICKER_PLAN = 2001;
    private int REQUEST_FEATURED_PICKER_GALLERY = 2003;
    boolean isFeatured = false;
    boolean isFeaturedPlan = false;
    boolean isFeaturedGallery = false;
    private ArrayList<Image> featuredImages, featuredImages_plan, featuredImages_gallery = new ArrayList<>();
    ProgressDialog pDialog;
    String strMessage;

    @Required(order = 1, message = "Enter Property Name")
     EditText edtPurposeName;

    @Required(order = 2, message = "Enter Property Description")
     EditText edtPurposeDesc;

    @Required(order = 3, message = "Enter Phone")
     EditText edtPurposePhone;

    @Required(order = 4, message = "Enter Address")
     EditText edtPurposeAddress;

    EditText edtPurposeLatitude;
    EditText edtPurposeLongitude;

     @Required(order = 5, message = "Enter Bedrooms")
    EditText edtPurposeBedroom;

    @Required(order = 6, message = "Enter Bathrooms")
    EditText edtPurposeBathroom;

    @Required(order = 7, message = "Enter Area")
    EditText edtPurposeArea;

    @Required(order = 8, message = "Enter Amenities")
    EditText edtPurposeAmenity;

    @Required(order = 9, message = "Enter Price")
    EditText edtPurposePrice;
    private Validator validator;
    MyApplication MyApp;
    JsonUtils jsonUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_properties);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_add_properties));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MyApp = MyApplication.getInstance();

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        mListType = new ArrayList<>();
        mPropertyName = new ArrayList<>();
        mPropertyPurpose = new ArrayList<>();
        pDialog = new ProgressDialog(AddPropertiesActivity.this);
        spinner_cat = (Spinner) findViewById(R.id.spPropertyType);
        spinner_pupose = (Spinner) findViewById(R.id.spPropertyPurpose);
        edtPurposeName = (EditText) findViewById(R.id.edt_Purpose_name);
        edtPurposeDesc = (EditText) findViewById(R.id.edt_Purpose_desc);
        edtPurposePhone = (EditText) findViewById(R.id.edt_Purpose_phone);
        edtPurposeAddress = (EditText) findViewById(R.id.edt_Purpose_address);
        edtPurposeLatitude = (EditText) findViewById(R.id.edt_Purpose_latitude);
        edtPurposeLongitude = (EditText) findViewById(R.id.edt_Purpose_longitude);
        edtPurposeBedroom = (EditText) findViewById(R.id.edt_Purpose_bedroom);
        edtPurposeBathroom = (EditText) findViewById(R.id.edt_Purpose_bathroom);
        edtPurposeArea = (EditText) findViewById(R.id.edt_Purpose_area);
        edtPurposeAmenity = (EditText) findViewById(R.id.edt_Purpose_amenity);
        edtPurposePrice = (EditText) findViewById(R.id.edt_Purpose_price);
        img_feature = (ImageView) findViewById(R.id.image_add_feature);
        img_plan = (ImageView) findViewById(R.id.image_add_plan);
        img_gallery = (ImageView) findViewById(R.id.image_add_gallery);
        lay_feature = (RelativeLayout) findViewById(R.id.lay_rel_feature);
        lay_plan = (RelativeLayout) findViewById(R.id.lay_rel_plan);
        lay_gallery = (RelativeLayout) findViewById(R.id.lay_rel_gallery);
        lay_submit = (Button) findViewById(R.id.btn_sub);
        txtSelect1 = (TextView) findViewById(R.id.text_select1);
        txtSelect2 = (TextView) findViewById(R.id.text_select2);
        txtSelect3 = (TextView) findViewById(R.id.text_select3);

        txtSelect1.setVisibility(View.VISIBLE);
        txtSelect2.setVisibility(View.VISIBLE);
        txtSelect3.setVisibility(View.VISIBLE);

        if (JsonUtils.isNetworkAvailable(AddPropertiesActivity.this)) {
            new getType().execute(Constant.PROPERTIES_TYPE);
        }

        if(edtPurposeLatitude.getText().toString().length() == 0 )
        {
            edtPurposeLatitude.setText("22.303894");
        }

        if(edtPurposeLongitude.getText().toString().length() == 0 )
        {
            edtPurposeLongitude.setText("70.802160");
        }

        lay_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validator.validateAsync();
            }

        });

        lay_feature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFeaturedImage();
            }
        });

        lay_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFeaturedImagePlan();
            }
        });

        lay_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFeaturedImageGallery();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onValidationSucceeded() {
        if(isFeatured && isFeaturedPlan && isFeaturedGallery)
        {
            if (JsonUtils.isNetworkAvailable(AddPropertiesActivity.this)) {
                uploadData();
            } else {
                showToast(getString(R.string.network_msg));
            }
        }
        else
        {
            Toast.makeText(AddPropertiesActivity.this,getResources().getString(R.string.select_image),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(this, "Record Not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private class getType extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || result.length() == 0) {

            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemType objItem = new ItemType();
                        objItem.setTypeId(objJson.getString(Constant.TYPE_ID));
                        objItem.setTypeName(objJson.getString(Constant.TYPE_NAME));
                        mPropertyName.add(objJson.getString(Constant.TYPE_NAME));
                        mListType.add(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mPropertyName);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_cat.setAdapter(typeAdapter);

        spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }


    public void chooseFeaturedImage() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Folder")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(REQUEST_FEATURED_PICKER);
    }

    public void chooseFeaturedImagePlan() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Folder")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(REQUEST_FEATURED_PICKER_PLAN);
    }

    public void chooseFeaturedImageGallery() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Folder")
                .imageTitle("Tap to select")
                .multi()
                .limit(5)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(REQUEST_FEATURED_PICKER_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FEATURED_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                featuredImages = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(featuredImages.get(0).getPath()));
                Picasso.with(AddPropertiesActivity.this).load(uri).into(img_feature);
                isFeatured = true;
                txtSelect1.setVisibility(View.GONE);

            }
        } else if (requestCode == REQUEST_FEATURED_PICKER_PLAN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                featuredImages_plan = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(featuredImages_plan.get(0).getPath()));
                Picasso.with(AddPropertiesActivity.this).load(uri).into(img_plan);
                isFeaturedPlan = true;
                txtSelect2.setVisibility(View.GONE);

            }
        } else if (requestCode == REQUEST_FEATURED_PICKER_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                featuredImages_gallery = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(featuredImages_gallery.get(0).getPath()));
                Picasso.with(AddPropertiesActivity.this).load(uri).into(img_gallery);
                isFeaturedGallery = true;
                txtSelect3.setVisibility(View.GONE);
            }
        }

    }

    public void uploadData() {

        Request request = Request.create(Constant.UPLOAD_PROPERTIES_URL);
        request.setMethod("POST")
                .setTimeout(120)
                .setLogger(new Logger(Logger.ERROR))
                .addParameter("user_id", MyApp.getUserId())
                .addParameter("type_id", mListType.get(spinner_cat.getSelectedItemPosition()).getTypeId())
                .addParameter("place_purpose", String.valueOf(spinner_pupose.getSelectedItem()))
                .addParameter("place_name", edtPurposeName.getText().toString())
                .addParameter("place_description", edtPurposeDesc.getText().toString())
                .addParameter("place_bed", edtPurposeBedroom.getText().toString())
                .addParameter("place_bath", edtPurposeBathroom.getText().toString())
                .addParameter("place_area", edtPurposeArea.getText().toString())
                .addParameter("place_amenities", edtPurposeAmenity.getText().toString())
                .addParameter("place_price", edtPurposePrice.getText().toString())
                .addParameter("place_phone", edtPurposePhone.getText().toString())
                .addParameter("place_address", edtPurposeAddress.getText().toString())
                .addParameter("place_map_latitude", edtPurposeLatitude.getText().toString())
                .addParameter("place_map_longitude", edtPurposeLongitude.getText().toString());


        if (isFeatured) {
            request.addParameter("place_image", new File(featuredImages.get(0).getPath()));
        }
        if (isFeaturedPlan) {
            request.addParameter("place_floor_plan", new File(featuredImages_plan.get(0).getPath()));
        }
        if (isFeaturedGallery) {
            request.addParameter("place_gallery_image[]", featuredImages_gallery);
        }
        request.setFileUploadListener(new FileUploadListener() {
            @Override
            public void onUploadingFile(File file, long size, long uploaded) {

            }
        })
                .setRequestStateListener(new RequestStateListener() {
                    @Override
                    public void onStart() {
                        showProgressDialog();
                    }

                    @Override
                    public void onFinish() {
                        dismissProgressDialog();
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new JsonResponseListener() {
                    @Override
                    public void onOkResponse(JSONObject jsonObject) throws JSONException {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.ARRAY_NAME);
                        JSONObject objJson = jsonArray.getJSONObject(0);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        strMessage = objJson.getString(Constant.MSG);
                        setResult();
                    }

                    @Override
                    public void onErrorResponse(JSONObject jsonObject) throws JSONException {

                    }

                    @Override
                    public void onParseError(JSONException e) {

                    }
                }).execute();
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {

            showToast(strMessage);
        } else {
            showToast(strMessage);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void showProgressDialog() {
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }

    public void showToast(String msg) {
        Toast.makeText(AddPropertiesActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}

