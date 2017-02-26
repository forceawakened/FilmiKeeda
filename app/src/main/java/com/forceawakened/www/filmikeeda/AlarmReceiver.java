package com.forceawakened.www.filmikeeda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by forceawakened on 23/2/17.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.movie_reminder_toast, null);

        //show reminder message
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("reminder for " + intent.getStringExtra(ShowMovieInfoFragment.MOVIE_NAME));

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        //play alarm tone
        Uri reminder = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone reminderTone = RingtoneManager.getRingtone(context, reminder);
        reminderTone.play();
    }
}
