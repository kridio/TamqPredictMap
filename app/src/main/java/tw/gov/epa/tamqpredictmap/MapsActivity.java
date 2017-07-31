package tw.gov.epa.tamqpredictmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import tw.gov.epa.tamqpredictmap.predict.DriverService;
import tw.gov.epa.tamqpredictmap.predict.TamqPredictTileProvider;
import tw.gov.epa.tamqpredictmap.predict.model.Result;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,DriverService.View {

    private GoogleMap mMap;

    private static HashMap<String,String> latlngSiteMap = new HashMap<>();
    private HashMap<String, DataSet> mDataSets = new HashMap<String, DataSet>();

    private HeatmapTileProvider mProvider;

    private TamqPredictTileProvider mTamqTileProvider;

    private TileOverlay mOverlay;

    DriverService driverService;

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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6, 121), 7.8f));

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style));

        //25.341075, 120.172856
        //21.942719, 121.910504
        Log.d(MapsActivity.class.getSimpleName(),""+mMap.getMaxZoomLevel());
        Log.d(MapsActivity.class.getSimpleName(),""+mMap.getMinZoomLevel());

        //gesture control disable
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);

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

        getData();
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

    public void showScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
    }

    @Override
    public void refreshMap(List<Result> resultList) {
        String latlng="";
        String latlngArray[];
        ArrayList<WeightedLatLng> weightedLatLngList = new ArrayList<WeightedLatLng>();
        for(Result res:resultList){
            latlng = latlngSiteMap.get(res.getSiteName());
            latlngArray = latlng.split(",");
            Double lat = Double.parseDouble(latlngArray[0]);
            Double lng = Double.parseDouble(latlngArray[1]);
            Double value = 0.0;
            if (!res.getHr().toLowerCase().equals("nan")) {
                value = Double.parseDouble(res.getHr());
                if(value<36){
                    value = 0.1;
                }
                else if(value<54){
                    value = 0.2;
                }
                else if(value<71){
                    value = 0.6;
                }
                else{
                    value = 1.0;
                }
            }
            weightedLatLngList.add(new WeightedLatLng(new LatLng(lat,lng),value));
        }
        setHeatMap(weightedLatLngList);
    }

    public void setHeatMap(List<WeightedLatLng> weightedLatLngs){
        if(mProvider==null) {
            mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(weightedLatLngs)
                    .radius(30)
                    .opacity(0.3)
                    .build();
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

//            mPointTile  = new PointTileOverlay();
//            mPointTile.addPoint(new LatLng(0, 0));
//            mPointTile.addPoint(new LatLng(21, -10));
//            mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mPointTile));
        }

        if(mTamqTileProvider==null){
            mTamqTileProvider = new TamqPredictTileProvider.Builder()
                    .opacity(0.5)
                    .weightedData(weightedLatLngs)
                    .build();
            mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mTamqTileProvider));
        }

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

    private HashMap<String,String> readItems(int resource) throws JSONException {
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            String site = object.getString("site");
            latlngSiteMap.put(site,lat+","+lng);
        }
        return latlngSiteMap;
    }
}
