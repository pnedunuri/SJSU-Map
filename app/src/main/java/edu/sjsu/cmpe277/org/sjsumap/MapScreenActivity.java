package edu.sjsu.cmpe277.org.sjsumap;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Random;

public class MapScreenActivity extends AppCompatActivity implements Runnable {

    private static final int INTERNET_PERMISSION = 777;
	private static final double THRESHOLD = 0.0009;
    private static final double GPS_LOCATION_THRESHOLD = 0.00009;
    public static volatile MapScreenActivity me = null;
    public static float pxPerInch = 0;
    public static float aspectRatio = 0;
    public static volatile String searchString = null;
    private ImageView mapView = null;
    private ImageView invisibleMap = null;
    private double buildingCoordinates[][] = null;
    private volatile String prevString = null;
    private volatile boolean isAppRunning = false;
    private volatile GPSTracker gpsTracker = null;
    private volatile boolean gpsTrackerPrompted = false;
    private SearchView searchView = null;
    private volatile boolean markerCleared = false;
    private volatile Thread mapScreenThread = null;
    private volatile boolean shouldStopThread = false;
    private double previousLatitude = 0;
    private double previousLongitude = 0;
    private static volatile boolean isCurrentLocationUpdated = false;
    private double xPerLongitudeChange = 0;
    private double yPerLatitudeChange = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        me = this;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (metrics.widthPixels > metrics.heightPixels) {
            aspectRatio = (float) metrics.widthPixels / metrics.heightPixels;
        } else {
            aspectRatio = (float) metrics.heightPixels / metrics.widthPixels;
        }
        aspectRatio = Math.round(aspectRatio * 1000);
        aspectRatio /= 1000;

        pxPerInch = getResources().getDisplayMetrics().densityDpi / Constants.DEFAULT_DPI;

        mapView = (ImageView) findViewById(R.id.campus_map);
        mapView.setOnTouchListener(new ImageTouchListener());

        invisibleMap = (ImageView) findViewById(R.id.overlayInvisible);

        invisibleMap.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initMap();
            }
        });

        isAppRunning = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    private void initMap() {
        if (buildingCoordinates == null) {

            buildingCoordinates = new double[Constants.NO_OF_BUILDINGS][Constants.NUM_DIR_INDEXES];

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            double wRatio = metrics.widthPixels / Constants.MAP_RESOLUTION[Constants.RIGHT_INDEX];
            double hRatio = metrics.heightPixels / Constants.MAP_RESOLUTION[Constants.DOWN_INDEX];

            for (int bIndex = 0; bIndex < Constants.NO_OF_BUILDINGS; bIndex++) {
                for (int index = Constants.LEFT_INDEX; index < Constants.NUM_DIR_INDEXES; index++) {
                    double ratio = ((index % 2) == 0) ? wRatio : hRatio;
                    buildingCoordinates[bIndex][index] = Constants.MAP_BUILDINGS_COORDS[bIndex][index] * ratio;
                }
            }

//            int leftTopBuildX = (int) buildingCoordinates[Constants.KING_LIB][Constants.LEFT_INDEX];
//            leftTopBuildX += (buildingCoordinates[Constants.KING_LIB][Constants.RIGHT_INDEX] - leftTopBuildX) / 2;
//
//            int rightBottomBuildX = (int) buildingCoordinates[Constants.CVB][Constants.LEFT_INDEX];
//            int offset = (int) (buildingCoordinates[Constants.CVB][Constants.RIGHT_INDEX] - rightBottomBuildX) / 2;
//            rightBottomBuildX += offset;
//
//            xPerLongitudeChange = (Math.abs(leftTopBuildX - rightBottomBuildX) / Math.abs(Constants.MAP_LAT_LONG[Constants.LEFT_TOP_INDEX][Constants.LONGITUDE_INDEX] - Constants.MAP_LAT_LONG[Constants.RIGHT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX])) * Math.cos(Constants.MAP_VIEWING_ANGLE_RADIANS);
//
//            int rightTopBuildY = (int) buildingCoordinates[Constants.NP_GARAGE][Constants.TOP_INDEX];
//            rightTopBuildY += ((buildingCoordinates[Constants.NP_GARAGE][Constants.DOWN_INDEX] - rightTopBuildY) / 2);
//
//            int leftBottomBuildY = (int) buildingCoordinates[Constants.SP_GARAGE][Constants.TOP_INDEX];
//            leftBottomBuildY += ((buildingCoordinates[Constants.SP_GARAGE][Constants.DOWN_INDEX] - leftBottomBuildY) / 2);
//
//            yPerLatitudeChange = (Math.abs(leftBottomBuildY - rightTopBuildY) / Math.abs(Constants.MAP_LAT_LONG[Constants.RIGHT_TOP_INDEX][Constants.LATITUDE_INDEX] - Constants.MAP_LAT_LONG[Constants.LEFT_BOTTOM_INDEX][Constants.LATITUDE_INDEX])) * Math.cos(Constants.MAP_VIEWING_ANGLE_RADIANS) - offset;
            // no use of below code for now
//            DisplayMetrics metrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//            boolean[] bIdentified = new boolean[Constants.NO_OF_BUILDINGS];
//            int left = invisibleMap.getLeft();
//            int top = invisibleMap.getTop();
//            int imageWidth = invisibleMap.getWidth();
//            int imageHeihgt = invisibleMap.getHeight();
//
//            for (int row = 0; row < imageHeihgt;) {
//                row += 10;
//
//                for (int col = 0; col < imageWidth;) {
//                    col += 10;
//
//                    int pixel = getColour(col, row);
//                    if (pixel == -1) {
//                        continue;
//                    }
//
//                    int [] colors = new int[Constants.NUM_COLOR_INDEXES];
//
//                    int red = Color.red(pixel);
//                    colors[Constants.RED_COLOR_INDEX] = red;
//
//                    int green = Color.green(pixel);
//                    colors[Constants.GREEN_COLOR_INDEX] = green;
//
//                    int blue = Color.blue(pixel);
//                    colors[Constants.BLUE_COLOR_INDEX] = blue;
//
//                    for (int bIndex = 0; bIndex < Constants.NO_OF_BUILDINGS; bIndex++) {
//                        // skip the buildings already identified
//                        if (bIdentified[bIndex]) {
//                            if (row >= buildingCoordinates[bIndex][Constants.TOP_INDEX] && row <= buildingCoordinates[bIndex][Constants.DOWN_INDEX] && col >= buildingCoordinates[bIndex][Constants.LEFT_INDEX] && col <= buildingCoordinates[bIndex][Constants.RIGHT_INDEX])
//                            {
//                                col += (buildingCoordinates[bIndex][Constants.RIGHT_INDEX] - buildingCoordinates[bIndex][Constants.LEFT_INDEX]);
//                            }
//
//                            continue;
//                        }
//
//                        boolean buildingIdentified = true;
//                        for (int cIndex = Constants.RED_COLOR_INDEX; cIndex < Constants.NUM_COLOR_INDEXES; cIndex++) {
//                            if (!checkColorWithTolerance(Constants.BUILDINGS_COLOR_VALUES[bIndex][cIndex], colors[cIndex], 15)) {
//                                buildingIdentified = false;
//                            }
//                        }
//
//                        if (buildingIdentified) {
//                            buildingCoordinates[bIndex][Constants.LEFT_INDEX] = col;
//                            buildingCoordinates[bIndex][Constants.TOP_INDEX] = row;
//
//                            // calculate width
//                            int width = col;
//                            boolean endOfWidth = false;
//                            do {
//                                pixel = getColour(width, row);
//                                if (pixel == -1) {
//                                    endOfWidth = true;
//                                }
//
//                                colors = new int[Constants.NUM_COLOR_INDEXES];
//
//                                red = Color.red(pixel);
//                                colors[Constants.RED_COLOR_INDEX] = red;
//
//                                green = Color.green(pixel);
//                                colors[Constants.GREEN_COLOR_INDEX] = green;
//
//                                blue = Color.blue(pixel);
//                                colors[Constants.BLUE_COLOR_INDEX] = blue;
//
//                                for (int cIndex = Constants.RED_COLOR_INDEX; cIndex < Constants.NUM_COLOR_INDEXES; cIndex++) {
//                                    if (!checkColorWithTolerance(Constants.BUILDINGS_COLOR_VALUES[bIndex][cIndex], colors[cIndex], 15)) {
//                                        endOfWidth = true;
//                                    }
//                                }
//
//                                width++;
//                            } while (!endOfWidth);
//
//                            // calculate height
//                            int height = row;
//                            boolean endOfHeight = false;
//                            do {
//                                pixel = getColour(col, height);
//                                if (pixel == -1) {
//                                    endOfHeight = true;
//                                }
//
//                                colors = new int[Constants.NUM_COLOR_INDEXES];
//
//                                red = Color.red(pixel);
//                                colors[Constants.RED_COLOR_INDEX] = red;
//
//                                green = Color.green(pixel);
//                                colors[Constants.GREEN_COLOR_INDEX] = green;
//
//                                blue = Color.blue(pixel);
//                                colors[Constants.BLUE_COLOR_INDEX] = blue;
//
//                                for (int cIndex = Constants.RED_COLOR_INDEX; cIndex < Constants.NUM_COLOR_INDEXES; cIndex++) {
//                                    if (!checkColorWithTolerance(Constants.BUILDINGS_COLOR_VALUES[bIndex][cIndex], colors[cIndex], 15)) {
//                                        endOfHeight = true;
//                                    }
//                                }
//
//                                height++;
//                            } while (!endOfHeight);
//
//                            buildingCoordinates[bIndex][Constants.RIGHT_INDEX] = width;
//                            buildingCoordinates[bIndex][Constants.DOWN_INDEX] = height;
//
//                            bIdentified[bIndex] = true;
//                        }
//                    }
//                }
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        gpsTracker = new GPSTracker(this);
        mapScreenThread = new Thread(this);

        final View view = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapScreenThread.start();
            }
        }, 500);
    }

    private int getColour(int x, int y) {
        ImageView imageView = (ImageView) findViewById(R.id.overlayInvisible);
        Drawable imgDrawable = ((ImageView) imageView).getDrawable();

        int left = imageView.getLeft();
        int right = imageView.getWidth();
        int top = imageView.getTop();
        int down = imageView.getHeight();
//        Log.d("COORDINATES", "" + "x:: " + x + ", y:: " + y + ", left:: " + left + ", right:: " + right + ", top:: " + top + ", down:: " + down);
        if (x <= left || x >= right || y <= top || y >= down) {
            return -1;
        }

        Bitmap mutableBitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        imgDrawable.draw(canvas);
        return mutableBitmap.getPixel(x, y);
    }

    private boolean checkColorWithTolerance(int aColor, int cColor, int tolerance) {
        if (Math.abs(aColor - cColor) > tolerance) {
            return false;
        }

        return true;
    }

    @Override
    public void run() {
        while(isAppRunning) {
            // check for permission
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                Log.d("LOCATION", "Permissions are not given for GPS");
                ActivityCompat.requestPermissions(MapScreenActivity.me, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION);
            }

            if (shouldStopThread)  {
                try {
                    synchronized (mapScreenThread) {
                        mapScreenThread.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            initMap();

            // fetch location
            if (gpsTracker.canGetLocation()) {
                double userLatitude = gpsTracker.getLatitude();
                double userLongitude = gpsTracker.getLongitude();
                if ((!isCurrentLocationUpdated || previousLatitude == 0 || Math.abs(previousLatitude - userLatitude) > THRESHOLD) || (previousLongitude == 0 || Math.abs(previousLongitude - userLongitude) > THRESHOLD)) {
                    previousLatitude = userLatitude;
                    previousLongitude = userLongitude;

                    isCurrentLocationUpdated = false;

                    if (userLatitude == 0) {
                        gpsTracker.getLocation();
                    } else {
                        // user location

//                        int refBuildX = (int) buildingCoordinates[Constants.KING_LIB][Constants.LEFT_INDEX];
//                        refBuildX += (buildingCoordinates[Constants.KING_LIB][Constants.RIGHT_INDEX] - refBuildX) / 2;
//
//                        int refBuildY = (int) buildingCoordinates[Constants.NP_GARAGE][Constants.TOP_INDEX];
//                        refBuildY += ((buildingCoordinates[Constants.NP_GARAGE][Constants.DOWN_INDEX] - refBuildY) / 2);
//
//                        final int userX = refBuildX + (int) (xPerLongitudeChange * (Math.abs(Constants.MAP_LAT_LONG[Constants.LEFT_TOP_INDEX][Constants.LONGITUDE_INDEX] - userLongitude)));
//                        final int userY = refBuildY + (int) (yPerLatitudeChange * (Math.abs(Constants.MAP_LAT_LONG[Constants.RIGHT_TOP_INDEX][Constants.LATITUDE_INDEX]- userLatitude)));

                        int buildingIndex = -1;
                        for (int bIndex = 0; bIndex < Constants.NO_OF_BUILDINGS; bIndex++) {
                            // latitide check
                            boolean rightLT = false;
                            boolean bottomLT = false;
                            if (userLatitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_TOP_INDEX][Constants.LATITUDE_INDEX]) {
                                if (userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_TOP_INDEX][Constants.LONGITUDE_INDEX]) {
                                    rightLT = true;
                                }
                            } else if (userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_TOP_INDEX][Constants.LONGITUDE_INDEX]) {
                                bottomLT = true;
                            }

                            boolean leftRT = false;
                            boolean bottomRT = false;
                            if (userLatitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_TOP_INDEX][Constants.LATITUDE_INDEX]) {
                                if (userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_TOP_INDEX][Constants.LONGITUDE_INDEX]) {
                                    bottomRT = true;
                                } else {
                                    leftRT = true;
                                }
                            }

                            boolean topLB = false;
                            boolean rightLB = false;
                            if (userLatitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_BOTTOM_INDEX][Constants.LATITUDE_INDEX]) {
                                if (userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX]) {
                                    rightLB = true;
                                } else {
                                    topLB = true;
                                }
                            }

                            boolean leftRB = false;
                            boolean topRB = false;
                            if (userLatitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_BOTTOM_INDEX][Constants.LATITUDE_INDEX]) {
                                if (userLongitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX]) {
                                    topRB = true;
                                }
                            } else if (userLongitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX]) {
                                leftRB = true;
                            }

                            if ((rightLT || bottomLT) && (leftRT || bottomRT) && (rightLB || topLB) && (leftRB || topRB)) {
                                buildingIndex = bIndex;

                                break;
                            }

//                            if ((userLatitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_TOP_INDEX][Constants.LATITUDE_INDEX] && userLatitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_TOP_INDEX][Constants.LATITUDE_INDEX]) || (userLatitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_TOP_INDEX][Constants.LATITUDE_INDEX] && userLatitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_BOTTOM_INDEX][Constants.LATITUDE_INDEX]) || (userLatitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_TOP_INDEX][Constants.LATITUDE_INDEX] && userLatitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_BOTTOM_INDEX][Constants.LATITUDE_INDEX]) || (userLatitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_BOTTOM_INDEX][Constants.LATITUDE_INDEX] && userLatitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_BOTTOM_INDEX][Constants.LATITUDE_INDEX])) {
//                                if ((userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_TOP_INDEX][Constants.LONGITUDE_INDEX] && userLongitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_TOP_INDEX][Constants.LONGITUDE_INDEX]) || (userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_TOP_INDEX][Constants.LONGITUDE_INDEX] && userLongitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX]) || (userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_TOP_INDEX][Constants.LONGITUDE_INDEX] && userLongitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX]) || (userLongitude > Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.LEFT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX] && userLongitude < Constants.BUILDINGS_SURROUNDING_LAT_LONG[bIndex][Constants.RIGHT_BOTTOM_INDEX][Constants.LONGITUDE_INDEX])) {
//                                    buildingIndex = Constants.TOWER_HALL_BACK_SIDE;
//
//                                    break;
//                                }
//                            }
                        }

                        if (buildingIndex == -1) {
                            buildingIndex = new Random().nextInt(Constants.NO_OF_BUILDINGS);
                        }

                        int refBuildX = (int) buildingCoordinates[buildingIndex][Constants.LEFT_INDEX];
                        refBuildX += (buildingCoordinates[buildingIndex][Constants.RIGHT_INDEX] - refBuildX) / 2;

                        int refBuildY = (int) buildingCoordinates[buildingIndex][Constants.TOP_INDEX];
                        refBuildY += ((buildingCoordinates[buildingIndex][Constants.DOWN_INDEX] - refBuildY) / 2);

                        final int userX = refBuildX;
                        final int userY = refBuildY;

                        MapScreenActivity.me.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_map_screen);

                                ImageView uLocation = (ImageView) findViewById(R.id.userMarker);
                                rl.removeView(uLocation);

                                uLocation.setVisibility(View.VISIBLE);

                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(uLocation.getWidth(), uLocation.getHeight());

                                params.leftMargin = userX;
                                params.topMargin = userY - (uLocation.getHeight() >> 1);

                                rl.addView(uLocation, params);

                                searchView.clearFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                                isCurrentLocationUpdated = true;
                            }
                        });

                        Thread.yield();
                    }
                }
            } else if (!gpsTrackerPrompted && !gpsTracker.permissionFailed) {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTrackerPrompted = true;

                MapScreenActivity.me.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gpsTracker.showSettingsAlert();
                    }
                });
            }

            if (!markerCleared && (searchString == null || searchString.length() < 1)) {
                markerCleared = true;

                // remove the marker
                MapScreenActivity.me.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView bLocation = (ImageView) findViewById(R.id.destinationMarker);
                        bLocation.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (searchString != null && !searchString.equals(prevString)) {
                searchString = searchString.toUpperCase();
                prevString = searchString;

                // put the marker on that building
                for (int bIndex = 0; bIndex < Constants.NO_OF_BUILDINGS; bIndex++) {
                    if (Constants.BUILDING_NAMES[bIndex].toUpperCase().equals(searchString)) {
                        final int index = bIndex;
                        markerCleared = false;

                        MapScreenActivity.me.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_map_screen);

                                ImageView bLocation = (ImageView) findViewById(R.id.destinationMarker);
                                rl.removeView(bLocation);

                                bLocation.setVisibility(View.VISIBLE);

                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bLocation.getWidth(), bLocation.getHeight());

                                int left = (int) buildingCoordinates[index][Constants.LEFT_INDEX];
                                left += (buildingCoordinates[index][Constants.RIGHT_INDEX] - left) / 2;
                                int top = (int) buildingCoordinates[index][Constants.TOP_INDEX];
                                top += ((buildingCoordinates[index][Constants.DOWN_INDEX] - top) / 2) - bLocation.getHeight();
                                params.leftMargin = left;
                                params.topMargin = top;

                                rl.addView(bLocation, params);

                                searchView.clearFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                            }
                        });
                    }
                }
            } else if (searchView != null) {
                CharSequence sequence = searchView.getQuery();
                if (sequence.length() < 1) {
                    searchString = null;
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INTERNET_PERMISSION:
            {
            }
            break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isAppRunning = false;
    }

    public void stopThread() {
        shouldStopThread = true;
    }

    public void notifyThread() {
        try {
            synchronized (mapScreenThread) {
                mapScreenThread.notify();
            }
        } catch (Exception e) {

        }
        shouldStopThread = false;
    }

    class ImageTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();

                int pixel = getColour((int) x, (int) y);
                if (pixel == -1) {
                    return true;
                }

                int[] colors = new int[Constants.NUM_COLOR_INDEXES];

                int red = Color.red(pixel);
                colors[Constants.RED_COLOR_INDEX] = red;

                int green = Color.green(pixel);
                colors[Constants.GREEN_COLOR_INDEX] = green;

                int blue = Color.blue(pixel);
                colors[Constants.BLUE_COLOR_INDEX] = blue;

                int buildingIndex = -1;
                for (int index = 0; index < Constants.NO_OF_BUILDINGS; index++) {
                    boolean buildingIdentified = true;
                    for (int cIndex = 0; cIndex < Constants.NUM_COLOR_INDEXES; cIndex++) {
                        if (!checkColorWithTolerance(Constants.BUILDINGS_COLOR_VALUES[index][cIndex], colors[cIndex], 15)) {
                            buildingIdentified = false;

                            break;
                        }
                    }

                    if (buildingIdentified) {
                        buildingIndex = index;

                        break;
                    }
                }

                if (buildingIndex != -1) {
                    final int buildingIndexCopy = buildingIndex;
                    stopThread();

                    MapScreenActivity.me.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new GeoLocationFetcher().execute(buildingIndexCopy);
                        }
                    });
                }
                Log.d("Building Index", buildingIndex + "");
            }

            return true;
        }

        private class GeoLocationFetcher extends AsyncTask<Integer, Void, JSONObject> {
            protected JSONObject doInBackground(Integer... buildingIndex) {
                String response;
                JSONObject obj = null;
                String query = Constants.MAPS_API_REQUEST_QUERY;

                double userLatitude = gpsTracker.getLatitude();
                double userLongitude = gpsTracker.getLongitude();
                String usrLoc = userLatitude + "," + userLongitude;
                String destLoc = Constants.LATITUDE_LONGITUDE[buildingIndex[0]][Constants.LATITUDE_INDEX] + "," + Constants.LATITUDE_LONGITUDE[buildingIndex[0]][Constants.LONGITUDE_INDEX];
                query = query.replace("usrLoc", usrLoc);
                query = query.replace("destLoc", destLoc);

                response = RESTHelper.fetchData(Constants.MAPS_API_REQUEST_URI, query);
                try {
                    obj = new JSONObject(response);
                    obj.put("buildingIndex", buildingIndex[0].intValue());
                } catch (Exception e) {

                }
                return obj;
            }

            protected void onPostExecute(JSONObject result) {

                showBuildingDetailScreen(result);
            }
        }

        private int getColour( int x, int y)
        {
            ImageView imageView = (ImageView) findViewById(R.id.overlayInvisible);
            Drawable imgDrawable = ((ImageView)imageView).getDrawable();

            int left = imageView.getLeft();
            int right = imageView.getWidth();
            int top = imageView.getTop();
            int down = imageView.getHeight();

            if (x <= left || x >= right || y <= top || y >= down) {
                return -1;
            }

//            Log.d("COORDINATES", "" + "x:: " + x + ", y:: " + y + ", left:: " + left + ", right:: " + right + ", top:: " + top + ", down:: " + down);

            Bitmap mutableBitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mutableBitmap);
            imgDrawable.draw(canvas);
            return mutableBitmap.getPixel(x, y);
        }

        private boolean checkColorWithTolerance(int aColor, int cColor, int tolerance) {
            if (Math.abs(aColor - cColor) > tolerance) {
                return false;
            }

            return true;
        }
    }

    private void showBuildingDetailScreen(JSONObject result) {
        Log.d("Building details",result + "");

        Intent intent = new Intent(MapScreenActivity.this,BuildingDetailsActivity.class);
        try {
            intent.putExtra("name", Constants.BUILDING_NAMES[result.getInt("buildingIndex")]);
            String address = Constants.BUILDING_ADDRESSES[result.getInt("buildingIndex")];
            if(address.equals("")){
                JSONArray arr = result.getJSONArray("destination_addresses");
                address = arr.getString(0);
            }
            intent.putExtra("address",address);
            String distance = result.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("text");
            String duration = result.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");;
            intent.putExtra("distance",distance);
            intent.putExtra("duration",duration);
            intent.putExtra("imageName",Constants.BUILDING_IMAGE_NAMES[result.getInt("buildingIndex")]);
            double latitude = Constants.LATITUDE_LONGITUDE[result.getInt("buildingIndex")][Constants.LATITUDE_INDEX];
            double longitude = Constants.LATITUDE_LONGITUDE[result.getInt("buildingIndex")][Constants.LONGITUDE_INDEX];
            intent.putExtra("latitude",latitude);
            intent.putExtra("longitude",longitude);
            startActivity(intent);
        } catch(Exception e) {
            Log.d("Exception",e+"");
        }
    }
}