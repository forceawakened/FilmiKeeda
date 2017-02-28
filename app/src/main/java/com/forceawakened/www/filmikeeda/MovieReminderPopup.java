package com.forceawakened.www.filmikeeda;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MovieReminderPopup extends AppCompatActivity implements View.OnClickListener {
    private Ringtone reminderTone;
    private String name;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_reminder_popup);
        Intent intent = getIntent();
        //set reminder message
        TextView text = (TextView) findViewById(R.id.msg);
        name = intent.getStringExtra(MovieUtils.MOVIE_NAME);
        String msg = "reminder for " + name;
        text.setText(msg);
        //set movie poster
        id = intent.getIntExtra(MovieUtils.MOVIE_ID, 1);
        String poster = MovieUtils.getImgPath(getFilesDir() + File.separator, String.valueOf(id), MovieUtils.REMINDER);
        //Log.d("MRP", poster);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(poster, options);
        ImageView img = (ImageView) findViewById(R.id.img);
        img.setImageBitmap(bitmap);
        //Log.d("MRP",String.valueOf(bitmap));
        //set ok button
        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(this);
        //play alarm tone
        final Uri reminder = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        reminderTone = RingtoneManager.getRingtone(this, reminder);
        reminderTone.play();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        reminderTone.stop();
        File file = new File(MovieUtils.getImgPath(String.valueOf(getFilesDir() + File.separator) , String.valueOf(id), MovieUtils.REMINDER));
        if(file.exists()) {
            boolean result = file.delete();
            //Log.d("SWL", "file deleted: proof--> result=" + result);
        }
        else{
            //Log.d("SWL", "file does not exist so not deleted");
        }
        super.onDestroy();
    }
}
