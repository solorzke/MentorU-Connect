package com.njit.mentorapp.sidebar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.WebServer;

@SuppressLint("SetJavaScriptEnabled")
public class AcademicsFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_academics, container, false);
        WebView webView = view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(WebServer.getAcademicsLink());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        SideBar side = (SideBar) getActivity();
        if(side != null)
            side.toolbar.setTitle("Academics");
        return view;
    }
}