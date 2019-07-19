package com.njit.mentorapp;

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

import com.njit.mentorapp.Model.Service.WebServer;

public class SocialCapitalFragment extends Fragment {

    WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_socialcapital, container, false);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(WebServer.getSocialCapitalLink());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        SideBar side = (SideBar) getActivity();
        side.toolbar.setTitle("Social Capital");
        return view;
    }
}
