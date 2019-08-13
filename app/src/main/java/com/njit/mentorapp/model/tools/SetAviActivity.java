package com.njit.mentorapp.model.tools;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.njit.mentorapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.njit.mentorapp.model.service.FireBaseCallback;
import com.njit.mentorapp.model.service.ImageHandler;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.users.User;
import com.njit.mentorapp.model.users.UserType;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SetAviActivity extends AppCompatActivity
{
    private Button button;
    private View progress;
    private TextView save, name;
    private CircularImageView image;
    private String full_name;
    private String ucid;
    private byte [] byteArray;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avi);
        button = findViewById(R.id.take_picture);
        image = findViewById(R.id.photo);
        save = findViewById(R.id.save);
        name = findViewById(R.id.name);

        /* Set Progress bar layout */
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        RelativeLayout layout = progress.findViewById(R.id.prog_layout);
        layout.setBackgroundColor(Color.parseColor("#EFEFEF"));
        TextView subtitle = progress.findViewById(R.id.subtitle);
        String text = "Uploading image";
        subtitle.setText(text);
        save.setVisibility(View.INVISIBLE);

        UserType userType = new UserType(getApplicationContext());
        user = userType.getCurrentType().equals("mentee")
                ? new User(getApplicationContext(), "Mentee")
                : new User(getApplicationContext(), "Mentor");
        if(!user.getAvi().equals("")) /* -> */ Picasso.get().load(user.getAvi()).into(image);
        full_name = user.getFullName();

        /* Set the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle("Set Profile Picture");
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        name.setText(full_name);
        ucid = user.getUcid();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave(byteArray, ucid, user.getType(), progress);
            }
        });
    }

    /* When clicking the back button, go back to the last page. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            case 999999999:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* After the intent is complete, compress the bytes from the image taken to publish it to the image view */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK && intent.hasExtra("data"))
                {
                    save.setVisibility(View.VISIBLE);
                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if(bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byteArray = baos.toByteArray();
                        image.setImageBitmap(bitmap);
                    }
                }
                break;

            case 999999999:
                break;

            default:
                break;
        }
    }

    /* Upload the file_path to the Web Server */
    private void setAviRequest(final String [] data, final View progress)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebServer.getQueryLink(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG_OUTPUT", response);
                progress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                onBackPressed();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT", error.getMessage());
                progress.setVisibility(View.GONE);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "setAVI");
                params.put("path", data[1]); //https://tinyurl.com/yxpyc8jg
                params.put("user", data[0]);
                params.put("type", data[2]);
                return params;
            }
        };
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    /* Save the picture to the FireBase Storage, and upload its file path to the Web Server */
    private void onSave(byte [] byteArray, final String ucid, final String type, final View progress)
    {
        progress.setVisibility(View.VISIBLE);
        ImageHandler.uploadFile(byteArray, ucid, new FireBaseCallback() {
            @Override
            public void onCallback(String value) { }
            @Override
            public void onCallback(final StorageReference storageReference) {
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                user.setAvi(uri.toString());
                                String [] data = new String [] {ucid, uri.toString(), type};
                                setAviRequest(data, progress);
                            }
                        });
                    }
                });
            }
        });
    }
}
