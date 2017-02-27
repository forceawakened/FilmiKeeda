package com.forceawakened.www.filmikeeda;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by forceawakened on 28/2/17.
 */

public class AboutUs extends Fragment {
    private String PHONE_NO = "+91-9795089657";
    private String EMAIL_ID = "prabhatsingh6897@gmail.com";
    private String GITHUB_ID = "https://github.com/forceawakened";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_us, container, false);
        TextView txt1 = (TextView)v.findViewById(R.id.phone);
        TextView txt2 = (TextView)v.findViewById(R.id.email);
        TextView txt3 = (TextView)v.findViewById(R.id.github);
        txt1.setText(PHONE_NO);
        txt2.setText(EMAIL_ID);
        txt3.setText(GITHUB_ID);
        return v;
    }
}
