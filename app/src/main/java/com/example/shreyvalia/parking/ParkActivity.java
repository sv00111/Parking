package com.example.shreyvalia.parking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ParkActivity extends ActionBarActivity {

    private ImageView iv;
    private Timer updater;
    private int id;
    private final int refresh_duration = 15;

    private class LotReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            int capacity = intent.getIntExtra("cap", 0);
            int population = intent.getIntExtra("pop", 0);
            TextView capacity_text = (TextView) findViewById(R.id.capacityTextView);
            capacity_text.setText(population + "/" + capacity + " spots taken");
            ProgressBar progress = (ProgressBar) findViewById(R.id.capacity_progressbar);
            progress.setProgress(100 * population / capacity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);

        iv = (ImageView) findViewById(R.id.imageView);

        IntentFilter intentFilter = new IntentFilter("BROADCAST_LOTDATA");
        LocalBroadcastManager.getInstance(this).registerReceiver(new LotReceiver(), intentFilter);
        ProgressBar progress = (ProgressBar) findViewById(R.id.capacity_progressbar);
        progress.setProgress(0);

        Intent intent = getIntent();
        id = intent.getIntExtra("lot_id", 0);
        setResources(id);
        refresh_lot(id);
    }

    public void refresh_lot(int lot_number) {
        //deliver intent to lot service
        Intent serviceIntent = new Intent(getApplicationContext(), LotIntentService.class);
        serviceIntent.putExtra("lot", lot_number);
        startService(serviceIntent);
    }

    //Action bar titles for each navigation drawer activity. change in strings.xml
    //to preserve continuity
    public void setResources(int number) {
        CharSequence title = getTitle();
        switch (number) {
            case 0:
                title = getString(R.string.title_section1);
                iv.setImageResource(R.mipmap.corewest);
                break;
            case 1:
                title = getString(R.string.title_section2);
                iv.setImageResource(R.mipmap.northremote);
                break;
            case 2:
                title = getString(R.string.title_section3);
                iv.setImageResource(R.mipmap.eastremote);
                break;
            case 3:
                title = getString(R.string.title_section4);
                iv.setImageResource(R.mipmap.c10);
                break;
            case 4:
                title = getString(R.string.title_section5);
                iv.setImageResource(R.mipmap.crown);
                break;
            case 5:
                title = getString(R.string.title_section6);
                iv.setImageResource(R.mipmap.healthcenter);
                break;
        }
        setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updater = new Timer();
        updater.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_lot(id);
                    }
                });
            }
        }, 0, refresh_duration * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updater.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
