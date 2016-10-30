package edu.sjsu.cmpe277.org.sjsumap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MapScreenActivity extends AppCompatActivity implements Runnable {

    public static MapScreenActivity me = null;

    public static float pxPerInch = 0;

    public static float aspectRatio = 0;

    private ImageView mapView = null;
    private ImageView invisibleMap = null;

    private double buildingCoordinates[][] = null;

    public static volatile String searchString = null;
    private volatile String prevString = null;

    private volatile boolean isAppRunning = false;

    private volatile GPSTracker gpsTracker = null;
    private volatile boolean gpsTrackerPrompted = false;
    private SearchView searchView = null;
    private volatile boolean markerCleared = false;
    private static final double THRESHOLD = 0.0009;
    private volatile Thread mapScreenThread = null;
    private boolean shouldStopThread = false;
    private double previousLatitude = 0;
    private double previousLongitude = 0;

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

        mapScreenThread = new Thread(this);
        mapScreenThread.start();

        gpsTracker = new GPSTracker(this);
    }

    private int getColour( int x, int y)
    {
        ImageView imageView = (ImageView) findViewById(R.id.overlayInvisible);
        Drawable imgDrawable = ((ImageView)imageView).getDrawable();

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

            if (gpsTracker == null)
            {
                return;
            }

            // fetch location
            if(gpsTracker.canGetLocation()) {
                double userLatitude = gpsTracker.getLatitude();
                double userLongitude = gpsTracker.getLongitude();
                if ((previousLatitude == 0 || Math.abs(previousLatitude - userLatitude) > THRESHOLD) || (previousLongitude == 0 || Math.abs(previousLongitude - userLongitude) > THRESHOLD)) {
                    previousLatitude = userLatitude;
                    previousLongitude = userLongitude;
                } else {
                    return;
                }

                if (userLatitude == 0) {
                    gpsTracker.getLocation();
                } else {
                    // user location
                    int currX = -1;
                    int currY = -1;

                    int refBuildX = (int) buildingCoordinates[Constants.KING_LIB][Constants.LEFT_INDEX];
                    refBuildX += (buildingCoordinates[Constants.KING_LIB][Constants.RIGHT_INDEX] - refBuildX) / 2;

                    int refBuildY = (int) buildingCoordinates[Constants.KING_LIB][Constants.TOP_INDEX];
                    refBuildY += ((buildingCoordinates[Constants.KING_LIB][Constants.DOWN_INDEX] - refBuildY) / 2);

                    double refLatitude = Constants.LATITUDE_LONGITUDE[Constants.KING_LIB][Constants.LATITUDE_INDEX];
                    double refLongitude = Constants.LATITUDE_LONGITUDE[Constants.KING_LIB][Constants.LONGITUDE_INDEX];

                    boolean userOutOfUniv = true;

                    if (!(userLatitude > refLatitude && userLongitude < refLongitude)) {
                        for (int bIndex = 0; bIndex < Constants.LATITUDE_LONGITUDE.length; bIndex++) {
                            if (bIndex == Constants.KING_LIB) {
                                continue;
                            }

                            double currBuildLat = Constants.LATITUDE_LONGITUDE[Constants.KING_LIB][Constants.LATITUDE_INDEX];
                            double currBuildLong = Constants.LATITUDE_LONGITUDE[Constants.KING_LIB][Constants.LONGITUDE_INDEX];

                            int currBuildX = (int) buildingCoordinates[bIndex][Constants.LEFT_INDEX];
                            currBuildX += (buildingCoordinates[bIndex][Constants.RIGHT_INDEX] - currBuildX) / 2;

                            int currBuildY = (int) buildingCoordinates[bIndex][Constants.TOP_INDEX];
                            currBuildY += ((buildingCoordinates[bIndex][Constants.DOWN_INDEX] - currBuildY) / 2);

                            if (Math.abs(currBuildLat - userLatitude) < THRESHOLD && Math.abs(currBuildLong - userLongitude) < THRESHOLD) {
                                currX = currBuildX;
                                currY = currBuildY;
                            } else if (userLatitude > currBuildLat && userLongitude < currBuildLong) {
                                // user is in between library and current building
                                int diffX = Math.abs(currBuildX - refBuildX);
                                int diffY = Math.abs(currBuildY - refBuildY);

                                double deltaLat = Math.abs(currBuildLat - refLatitude);
                                double deltaLon = Math.abs(currBuildLong - refLongitude);

                                double latitudePerPixel = (((deltaLat / diffX) + (deltaLat / diffY)) / 2);
                                double longitudePerPixel = (((deltaLon / diffX) + (deltaLon / diffY)) / 2);

                                final int relativeX = (int) ((refLatitude - userLatitude) / latitudePerPixel);
                                final int relativeY = (int) ((refLongitude - userLongitude) / longitudePerPixel);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_map_screen);

                                        ImageView uLocation = (ImageView) findViewById(R.id.userMarker);
                                        rl.removeView(uLocation);

                                        uLocation.setVisibility(View.VISIBLE);

                                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(uLocation.getWidth(), uLocation.getHeight());

                                        params.leftMargin = relativeX;
                                        params.topMargin = relativeY;

                                        rl.addView(uLocation, params);

                                        searchView.clearFocus();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                                    }
                                });

                                userOutOfUniv = false;
                                break;
                            }
                        }
                    }

                    // user marker invisible
                    if (userOutOfUniv) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView uLocation = (ImageView) findViewById(R.id.userMarker);
                                uLocation.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            } else if(!gpsTrackerPrompted && !gpsTracker.permissionFailed) {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTrackerPrompted = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gpsTracker.showSettingsAlert();
                    }
                });
            }

            if (!markerCleared && (searchString == null || searchString.length() < 1)) {
                markerCleared = true;

                // remove the marker
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView bLocation = (ImageView) findViewById(R.id.destinationMarker);
                        bLocation.setVisibility(View.INVISIBLE);
                    }
                });
            }
            else if (searchString != null && !searchString.equals(prevString)) {
                searchString = searchString.toUpperCase();
                prevString = searchString;

                // put the marker on that building
                for (int bIndex = 0; bIndex < Constants.NO_OF_BUILDINGS; bIndex++) {
                    if (Constants.BUILDING_NAMES[bIndex].toUpperCase().equals(searchString)) {
                        final int index = bIndex;
                        markerCleared = false;

                        runOnUiThread(new Runnable() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        isAppRunning = false;
    }

    public void stopThread() {
        shouldStopThread = true;
    }

    public void notifyThread() {
        try{
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

                int [] colors = new int[Constants.NUM_COLOR_INDEXES];

                int red = Color.red(pixel);
                colors[Constants.RED_COLOR_INDEX] = red;

                int green = Color.green(pixel);
                colors[Constants.GREEN_COLOR_INDEX] = green;

                int blue = Color.blue(pixel);
                colors[Constants.BLUE_COLOR_INDEX] = blue;

                int buildingIndex = -1;
                for(int index = 0; index < Constants.NO_OF_BUILDINGS; index++) {
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

                    runOnUiThread(new Runnable() {
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
                String destLoc= Constants.LATITUDE_LONGITUDE[buildingIndex[0]][Constants.LATITUDE_INDEX] + "," + Constants.LATITUDE_LONGITUDE[buildingIndex[0]][Constants.LONGITUDE_INDEX];
                query = query.replace("usrLoc", usrLoc);
                query = query.replace("destLoc", destLoc);

                response = RESTHelper.fetchData(Constants.MAPS_API_REQUEST_URI, query);
                try {
                    obj = new JSONObject(response);
                    obj.put("buildingIndex", buildingIndex[0].intValue());
                } catch(Exception e) {

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