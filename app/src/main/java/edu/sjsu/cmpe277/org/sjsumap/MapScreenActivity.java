package edu.sjsu.cmpe277.org.sjsumap;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MapScreenActivity extends AppCompatActivity implements Runnable{

    public static Context context = null;

    public static float pxPerInch = 0;

    public static float aspectRatio = 0;

    private ImageView mapView = null;
    private ImageView invisibleMap = null;

    private double buildingCoordinates[][] = null;

    public static String searchString = null;
    private String prevString = null;

    private boolean isAppRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        context = getApplicationContext();

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

        new Thread(this).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
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
    }

    private int getColour( int x, int y)
    {
        ImageView imageView = (ImageView) findViewById(R.id.overlayInvisible);
        Drawable imgDrawable = ((ImageView)imageView).getDrawable();

        int left = imageView.getLeft();
        int right = imageView.getWidth();
        int top = imageView.getTop();
        int down = imageView.getHeight();
        Log.d("COORDINATES", "" + "x:: " + x + ", y:: " + y + ", left:: " + left + ", right:: " + right + ", top:: " + top + ", down:: " + down);
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
            initMap();
            
            if (searchString != prevString) {
                searchString = searchString.toUpperCase();
                prevString = searchString;

                if (searchString == null) {
                    // remove the marker
                } else {
                    // put the marker on that building
                    for (int bIndedx = 0; bIndedx < Constants.NO_OF_BUILDINGS; bIndedx++) {
                        if (Constants.BUILDING_NAMES[bIndedx].equals(searchString)) {
                            final int index = bIndedx;
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
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isAppRunning = false;
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

//                Log.d("COLOR", red + ", " + green + ", " + blue);

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

                Log.d("Building Index", buildingIndex + "");
            }

            return true;
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

            Log.d("COORDINATES", "" + "x:: " + x + ", y:: " + y + ", left:: " + left + ", right:: " + right + ", top:: " + top + ", down:: " + down);

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
}