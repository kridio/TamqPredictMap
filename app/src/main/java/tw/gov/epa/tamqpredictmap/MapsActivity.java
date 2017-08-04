package tw.gov.epa.tamqpredictmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.android.projection.SphericalMercatorProjection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import tw.gov.epa.tamqpredictmap.paint.TamqPredictTileProvider;
import tw.gov.epa.tamqpredictmap.predict.DriverService;
import tw.gov.epa.tamqpredictmap.predict.model.Result;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,DriverService.View {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    private static HashMap<String,LatLng> latlngSiteMap = new HashMap<>();
    private static HashMap<LatLng,Point> latlngPointHasValueMap = new HashMap<>();
    private static HashMap<LatLng,Point> latlngPointNoValueMap = new HashMap<>();
    private HashMap<String, DataSet> mDataSets = new HashMap<String, DataSet>();

    private TileOverlay mOverlay;

    DriverService driverService;
    View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        driverService = new DriverService();
        driverService.takeView(this);
        try {
            readItems(R.raw.site_latlng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        final View mapView = mapFragment.getView();
//        if (mapView.getViewTreeObserver().isAlive()) {
//            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    // remove the listener
//                    // ! before Jelly Bean:
//                    mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    // ! for Jelly Bean and later:
//                    //mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    // set map viewport
//                    // CENTER is LatLng object with the center of the map
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 15));
//                    // ! you can query Projection object here
//                    Point markerScreenPosition = map.getProjection().toScreenLocation(marker.getPosition());
//                    // ! example output in my test code: (356, 483)
//                    System.out.println(markerScreenPosition);
//                }
//            });
//        }


    }

    private Handler mThreadHandler;
    private HandlerThread mThread;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    Point markerScreenPosition;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6, 121), 7.8f)); //7.8
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6, 121), 7.8f));

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style));

        Log.d(MapsActivity.class.getSimpleName(),""+mMap.getMaxZoomLevel());
        Log.d(MapsActivity.class.getSimpleName(),""+mMap.getMinZoomLevel());

        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // remove the listener
                    // ! before Jelly Bean:

                    // ! for Jelly Bean and later:
                    //mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    // set map viewport
                    // CENTER is LatLng object with the center of the map
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6, 121), 15));
                    // ! you can query Projection object here
                    markerScreenPosition = mMap.getProjection().toScreenLocation(new LatLng(25.341075, 120.172856));

                    LatLng tt = mMap.getProjection().fromScreenLocation(new Point(0,0));
                    LatLng tc = mMap.getProjection().fromScreenLocation(new Point(1080,1845));

                    // ! example output in my test code: (356, 483)
                    Log.d(MapsActivity.class.getSimpleName(),"start:"+tt.latitude+","+tt.longitude);
                    Log.d(MapsActivity.class.getSimpleName(),"end:"+tc.latitude+","+tc.longitude);
                    Log.d(MapsActivity.class.getSimpleName(),mapView.getWidth()+","+ mapView.getHeight());
//                    for(LatLng latlng:latlngSiteMap.values()){
//                        Point screenPosition = mMap.getProjection().toScreenLocation(latlng);
//                        latlngPointHasValueMap.put(latlng,screenPosition);
//                        Log.d(MapsActivity.class.getSimpleName(),"Screen Display:"+screenPosition.x+","+screenPosition.y+" "+latlng.latitude+","+latlng.longitude);
//                    }

                    for(String site:latlngSiteMap.keySet()){
                        Point screenPosition = mMap.getProjection().toScreenLocation(latlngSiteMap.get(site));
                        latlngPointHasValueMap.put(latlngSiteMap.get(site),screenPosition);
//                        Log.d(MapsActivity.class.getSimpleName(),"Screen Display:{\"x:\""+screenPosition.x+",\"y:\""+screenPosition.y+",\"site\":\""+site+"\"}");
                    }
                }
            });
        }

        //gesture control disable
//        mMap.getUiSettings().setZoomGesturesEnabled(false);
//        mMap.getUiSettings().setRotateGesturesEnabled(false);
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
//        mMap.getUiSettings().setTiltGesturesEnabled(false);

//        String title = "This is Title";
//        String subTitle = "This is \nSubtitle";
//
//        //Marker
//        MarkerOptions markerOpt = new MarkerOptions();
//        markerOpt.position(new LatLng(23.6,121))
//                .title(title)
//                .snippet(subTitle)
//                .icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_info_black_24dp)));
//
//        //Set Custom InfoWindow Adapter
//        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
//        mMap.setInfoWindowAdapter(adapter);
//
//        mMap.addMarker(markerOpt);
//
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                marker.showInfoWindow();
//                return true;
//            }
//        });
        readFromFile();
//        getData();

//        Point laa = mMap.getProjection().toScreenLocation(new LatLng(21.942719, 121.910504));
//        LatLng lat = mMap.getProjection().fromScreenLocation(new Point(1000,1000));
//        Projection pro = mMap.getProjection();
//        Log.d(MapsActivity.class.getSimpleName(),"==>"+lat.latitude);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

//    public void showScreenSize(){
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
//    }

    /*
  *級距 R   G   B
  * 1  153 255 153
  * 2  0   255 0
  * 3  0   204 0
  * 4  255 255 0
  * 5  255 104 102
  * 6  255 153 0
  * 7  255 124 128
  * 8  255 0   0
  * 9  153 0   51
  * 10 204 0   255
   */
    int colors[] = new int[1440 * 2464];
    private void readFromFile() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.hr);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                int index=0;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    colors[index] = Integer.parseInt(receiveString);
                    if(colors[index]<5){
                        colors[index] = Color.argb(100,153,255,153);
                    }
                    else if(colors[index]<10){
                        colors[index] = Color.argb(100,0,255,0);
                    }
                    else if(colors[index]<15){
                        colors[index] = Color.argb(100,0,204,0);
                    }
                    else if(colors[index]<20){
                        colors[index] = Color.argb(100,255,255,0);
                    }
                    else if(colors[index]<25){
                        colors[index] = Color.argb(100,255,104,102);
                    }
                    else if(colors[index]<30){
                        colors[index] = Color.argb(100,255,153,0);
                    }
                    else if(colors[index]<35){
                        colors[index] = Color.argb(100,255,124,128);
                    }
                    else if(colors[index]<40){
                        colors[index] = Color.argb(100,255,0,0);
                    }
                    else if(colors[index]<45){
                        colors[index] = Color.argb(100,153,0,51);
                    }
                    else{
                        colors[index] = Color.argb(100,204,0,255);
                    }
                    index++;
//                    stringBuilder.append(receiveString+",");
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
        refreshMap();
    }

    void refreshMap(){
        PointTileOverlay pto = new PointTileOverlay();
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(pto));
    }


    @Override
    public void refreshMap(List<Result> resultList) {
        LatLng latlng = null;
//        String latlngArray[];
        ArrayList<WeightedLatLng> weightedLatLngList = new ArrayList<WeightedLatLng>();
        for(Result res:resultList){
            latlng = latlngSiteMap.get(res.getSiteName());
//            Double lat = latlng.latitude;
//            Double lng = latlng.longitude;
            Double value = 0.0;
            if (!res.getHr().toLowerCase().equals("nan")) {
                value = Double.parseDouble(res.getHr());
                if(value<12){
                    value = 0.1;
                }
                else if(value<24){
                    value = 0.2;
                }
                else if(value<36){
                    value = 0.3;
                }
                else if(value<42){
                    value = 0.4;
                }
                else if(value<48){
                    value = 0.5;
                }
                else if(value<54){
                    value = 0.6;
                }
                else if(value<59){
                    value = 0.7;
                }
                else if(value<65){
                    value = 0.8;
                }
                else if(value<71){
                    value = 0.9;
                }
                else{
                    value = 1.0;
                }
            }
            if(latlng!=null) {
                weightedLatLngList.add(new WeightedLatLng(latlng, value));
            }
        }
        setHeatMap(weightedLatLngList);
    }

    public void setHeatMap(List<WeightedLatLng> weightedLatLngs){
//        if(mProvider==null) {
//            mProvider = new HeatmapTileProvider.Builder()
//                    .weightedData(weightedLatLngs)
//                    .radius(30)
//                    .opacity(0.3)
//                    .build();
//            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

//            mPointTile  = new PointTileOverlay();
//            mPointTile.addPoint(new LatLng(0, 0));
//            mPointTile.addPoint(new LatLng(21, -10));
//            mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mPointTile));
//        }

//        if(mTamqTileProvider==null){
//            mTamqTileProvider = new TamqPredictTileProvider.Builder()
//                    .opacity(0.5)
//                    .radius(30)
//                    .weightedData(weightedLatLngs)
//                    .build();
//            mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mTamqTileProvider));
//        }

//        PointTileOverlay pto = new PointTileOverlay();
//        pto.addPoint(new LatLng(25.341075, 120.172856));
//        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(pto));
    }

    private class PointTileOverlay implements TileProvider {
        private List<Point> mPoints = new ArrayList<Point>();
        private int mTileSize = 256;
        private SphericalMercatorProjection mProjection = new SphericalMercatorProjection(mTileSize);
        private int mScale = 2;
        private int mDimension = mScale * mTileSize;

        @Override
        public Tile getTile(int x, int y, int zoom) {
//            Matrix matrix = new Matrix();
//            float scale = (float) Math.pow(2, zoom) * mScale;
//            matrix.postScale(scale, scale);
//            matrix.postTranslate(-x * mDimension, -y * mDimension);

//            Bitmap bitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888);
//            Canvas c = new Canvas(bitmap);
//            c.setMatrix(matrix);
//
//            for (Point p : mPoints) {
//                c.drawCircle((float) p.x, (float) p.y, 1, new Paint());
//            }

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            return new Tile(mDimension, mDimension, baos.toByteArray());
            double tileWidth = 1.0D / Math.pow(2.0D, (double)zoom);
            double minX = (double)x * tileWidth;
            double maxX = (double)(x + 1) * tileWidth ;
            double minY = (double)y * tileWidth;
            double maxY = (double)(y + 1) * tileWidth;
//            Bounds tileBounds;
//            tileBounds = new Bounds(minX, maxX, minY, maxY);

            Bitmap tile = Bitmap.createBitmap(1440, 2464, Bitmap.Config.ARGB_8888);
            tile.setPixels(colors, 0, 1440, 0, 0, 1440, 2464);
            return convertBitmap(tile);
        }

//        public void addPoint(LatLng latLng) {
//            mPoints.add(mProjection.toPoint(latLng));
//        }
    }

    private static Tile convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return new Tile(bitmap.getWidth(), bitmap.getHeight(), bitmapdata);
    }

    private void genMapArray(){
        //25.341075, 120.172856
        //25.341075, 121.910504
        //21.942719, 120.172856
        //21.942719, 121.910504
        double height = (25.341075 - 21.942719) / 9;
        double width = (121.910504 - 120.172856) / 9;

    }

    /**
     * Helper class - stores data sets and sources.
     */
    private class DataSet {
        private ArrayList<LatLng> mDataset;
        private ArrayList<WeightedLatLng> mWeightedLatLng;

//        public DataSet(ArrayList<LatLng> dataSet) {
//            this.mDataset = dataSet;
//        }

        public DataSet(ArrayList<WeightedLatLng> weightedLatLng){
            this.mWeightedLatLng = weightedLatLng;
        }

        public ArrayList<WeightedLatLng> getData() {
            return mWeightedLatLng;
        }


//        public ArrayList<LatLng> getData() {
//            return mDataset;
//        }
    }

    public void getData(){
        if(driverService!=null){
            driverService.getPredictData();
        }
    }

    private HashMap<String,LatLng> readItems(int resource) throws JSONException {
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            LatLng latLng = new LatLng(lat,lng);
            String site = object.getString("site");
            latlngSiteMap.put(site,latLng);
        }
        return latlngSiteMap;
    }
}
