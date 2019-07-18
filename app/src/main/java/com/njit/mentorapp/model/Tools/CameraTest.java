package com.njit.mentorapp.model.Tools;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CameraTest extends AppCompatActivity
{
    /* This class was supposed to be used to test how to upload images to the server. Unfortunately,
     * due to an administrative issue with the r/w permissions set on the server, writing the encoded string
     * containing the bitmap image to a file is not available to complete right now. This issue may
     * not be solved until either the admin finds out what the problem is and can solve it, or set
     * up our own server with our own preferences as admin. */

    Button button;
    CircularImageView image;
    String encodedImage;
    String url = "https://web.njit.edu/~kas58/mentorDemo/Model/scrap.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        button = findViewById(R.id.button);
        image = findViewById(R.id.photo);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK && intent.hasExtra("data"))
                {
                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                    if (bitmap != null)
                    {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bitmap is required image which have to send  in Bitmap form
                        byte[] byteArray = baos.toByteArray();
                        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        image.setImageBitmap(bitmap);
                        sendImage();
                    }

                }
                break;

            default:
                break;
        }
    }

    private void sendImage()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.print("WAKA WAKA "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print("Volley" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "sendImage");
                params.put("image", encodedImage);
                params.put("user", "kas58");
                return params;
            }
        };
        queue.add(request);
    }

}
