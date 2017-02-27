package com.forceawakened.www.filmikeeda;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by forceawakened on 26/2/17.
 */

public class NoConnection extends android.support.v4.app.Fragment {
    private Callback mCallback;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.no_connection, container, false);
        Button refreshBtn = (Button) v.findViewById(R.id.refresh_btn);
        mCallback = (Callback) getActivity();
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.Refresh();
            }
        });
        return v;
    }

    public interface Callback{
        void Refresh();
    }

}
