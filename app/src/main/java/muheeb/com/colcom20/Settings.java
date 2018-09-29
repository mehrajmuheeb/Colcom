package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity implements View.OnClickListener  {

    private Button buttonChoose;
    private Button buttonUpload;

    private ImageView imageView;

    private EditText editTextName;

    private Bitmap bitmap;
    LinearLayout layout;
    private int PICK_IMAGE_REQUEST = 1;
    SharedPreferences preferences;
    private String UPLOAD_URL = "http://colcom.pe.hu/Project/upload.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    String image;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        layout = (LinearLayout) findViewById(R.id.settingLayout);
        //editTextName = (EditText) findViewById(R.id.editText);

        imageView  = (ImageView) findViewById(R.id.imageView);
        // imageView2 = (ImageView) findViewById(R.id.imageView2);
        preferences =Settings.this.getSharedPreferences("UserCriticalData", MODE_PRIVATE);

        name = preferences.getString("FullName","");
        Log.e("getParams: ","Name "+name );



        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s)
                    {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Log.e("getParams: ", image);
                        //Showing toast message of the response
                        Toast.makeText(Settings.this, s , Toast.LENGTH_LONG).show();
                        if (s.equals("Successfully Uploaded"))
                        {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("images", image);

                            Log.e("onResponse: ", preferences.getString("images",""));

                            editor.commit();
                            //setImage();

                           // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                            String previouslySetImage = preferences.getString("images","");

                            if(!previouslySetImage.equalsIgnoreCase(""))
                            {
                                byte []b = Base64.decode(previouslySetImage, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(b,0,b.length);
                                MainActivity.image.setImageBitmap(bitmap);
                                MainActivity.profilePic.setImageBitmap(bitmap);

                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Log.e("onErrorResponse: ", volleyError.toString());
                        //Showing toast
                        Toast.makeText(Settings.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(Settings.this, "Couldn't Upload, Try again", Toast.LENGTH_SHORT).show();

                        Snackbar snackbar = Snackbar
                                .make(layout, "Could Upload Image", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        uploadImage();
                                    }
                                });


                        snackbar.show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                Log.e("getParams: ", params.toString());
                return params;
            }


        };


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to the queue
        requestQueue.add(stringRequest);


    }


    private void setImage()
    {
        final CircleImageView imageView = (CircleImageView) findViewById(R.id.profilePic);
        String url = "http://colcom.pe.hu/Project/uploads/"+name+".png";

        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response)
                    {

                        MainActivity.image.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(Settings.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        MySingleton.getInstance(Settings.this).addToRequestQue(imageRequest);
    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v == buttonChoose){
            showFileChooser();
        }

        if(v == buttonUpload){

            uploadImage();


        }
    }
}

