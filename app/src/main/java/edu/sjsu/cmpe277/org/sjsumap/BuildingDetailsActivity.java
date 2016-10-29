package edu.sjsu.cmpe277.org.sjsumap;

import android.content.Intent;
import android.media.Image;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import static java.security.AccessController.getContext;

public class BuildingDetailsActivity extends AppCompatActivity {
    TextView address, distance, duration;
    ActionBar actionBar;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        Bundle extras  = getIntent().getExtras();
        address = (TextView)findViewById(R.id.address);
        address.setText(extras.getString("address"));
        Log.d("address",extras.getString("address")+"");
        distance = (TextView)findViewById(R.id.distance);
        distance.setText(extras.getString("distance"));
        duration = (TextView) findViewById(R.id.duration);
        duration.setText(extras.getString("duration"));
        image = (ImageView)findViewById(R.id.buildingImage);
        String imageName = extras.getString("imageName");
        if(imageName.equals("king_library")) {
            image.setImageResource(R.drawable.king_library);
        }else if(imageName.equals("student_union")){
            image.setImageResource(R.drawable.student_union);
        }else if(imageName.equals("bbc")) {
            image.setImageResource(R.drawable.bbc);
        }else if(imageName.equals("south_parking_garage")) {
            image.setImageResource(R.drawable.south_parking_garage);
        }else if(imageName.equals("yoshihiro_uchida_hall")) {
            image.setImageResource(R.drawable.yoshihiro_uchida_hall);
        }else if(imageName.equals("engineering_building")) {
            image.setImageResource(R.drawable.engineering_building);
        }else {
            image.setImageResource(R.drawable.no_image);
        }

        // home screen button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MapScreenActivity.me.notifyThread();
    }
}
