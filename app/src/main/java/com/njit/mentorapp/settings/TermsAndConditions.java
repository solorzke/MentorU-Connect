package com.njit.mentorapp.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.WebServer;

public class TermsAndConditions extends AppCompatActivity
{
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        webview = findViewById(R.id.webview);

        /* Set Toolbar and back button */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Terms & Conditions");
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(WebServer.getTermsAndConditionsLink());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
