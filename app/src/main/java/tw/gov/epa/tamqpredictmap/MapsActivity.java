package tw.gov.epa.tamqpredictmap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlay;

//import com.google.maps.android.heatmaps.Gradient;
//import com.google.maps.android.heatmaps.HeatmapTileProvider;
//import com.google.maps.android.heatmaps.WeightedLatLng;
//import com.google.maps.android.kml.KmlLayer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /**
     * Alternative radius for convolution
     */
    private static final int ALT_HEATMAP_RADIUS = 10;

    /**
     * Alternative opacity of heatmap overlay
     */
    private static final double ALT_HEATMAP_OPACITY = 0.4;

    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(255, 255, 0),
            Color.rgb(255, 240, 0)
    };

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

//    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
//            ALT_HEATMAP_GRADIENT_START_POINTS);
//
//    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
//    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6, 121), 7.8f));

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style));



    }

    /**
     * Helper class - stores data sets and sources.
     */
//    private class DataSet {
//        private ArrayList<LatLng> mDataset;
//        private ArrayList<WeightedLatLng> mWeightedLatLng;
//
////        public DataSet(ArrayList<LatLng> dataSet) {
////            this.mDataset = dataSet;
////        }
//
//        public DataSet(ArrayList<WeightedLatLng> weightedLatLng){
//            this.mWeightedLatLng = weightedLatLng;
//        }
//
//        public ArrayList<WeightedLatLng> getData() {
//            return mWeightedLatLng;
//        }
//
//
////        public ArrayList<LatLng> getData() {
////            return mDataset;
////        }
//    }
//
//    // Datasets from http://data.gov.au
//    private ArrayList<WeightedLatLng> readItems(int resource) throws JSONException {
//        ArrayList<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
//        InputStream inputStream = getResources().openRawResource(resource);
//        String json = new Scanner(inputStream).useDelimiter("\\A").next();
//        JSONArray array = new JSONArray(json);
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject object = array.getJSONObject(i);
//            double lat = object.getDouble("lat");
//            double lng = object.getDouble("lng");
//            double intensity = object.getDouble("intensity");
////            list.add(new LatLng(lat, lng));
//            list.add(new WeightedLatLng(new LatLng(lat, lng),intensity));
//        }
//        return list;
//    }

}
