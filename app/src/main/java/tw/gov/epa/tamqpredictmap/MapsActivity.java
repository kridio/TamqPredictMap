package tw.gov.epa.tamqpredictmap;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
//import com.google.maps.android.heatmaps.Gradient;
//import com.google.maps.android.heatmaps.HeatmapTileProvider;
//import com.google.maps.android.heatmaps.WeightedLatLng;
//import com.google.maps.android.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
      //        String latlng_t = "119.96445191400005,25.945523174000073 119.96426754100003," +
//                "25.945488368000042 119.96419042300011,25.945495744000084 119.96417144500003," +
//                "25.945495871000048 119.96414427800005,25.945500055000082 119.9641374680001," +
//                "25.94551840400004 119.96415925000008,25.945588208000061 119.96419533100004," +
//                "25.945632408000051 119.96421249900004,25.945643160000088 119.96423409600004," +
//                "25.94565388500007 119.96424359800005,25.94565553700005 119.96439985000006," +
//                "25.945732852000049 119.96443788400006,25.945742323000047 119.96445941900004," +
//                "25.945745612000053 119.964467001,25.945744418000061 119.96448523000004," +
//                "25.945729997000058 119.96455429000002,25.94566547900007 119.96456047700008," +
//                "25.945648279000068 119.96455653700002,25.945630575000052 119.96455136500003," +
//                "25.945616883000071 119.96453392600006,25.94557295900006 119.96452181400002," +
//                "25.945561601000065 119.964512893,25.945553652000058 119.9644900080001," +
//                "25.945540077000089 119.96447095600001,25.945531054000071 119.96445191400005,25.945523174000073";
//        String []latlng_s = latlng_t.split(" ");
//        PolygonOptions rectOptions = new PolygonOptions()
//                .fillColor(Color.rgb( 255, 0, 0))
//                .strokeColor(Color.alpha(0));
//
//        for(String s:latlng_s){
//            String []temp = s.split(",");
//            rectOptions.add(new LatLng(Double.valueOf(temp[1]),Double.valueOf(temp[0])));
//        }
//
//
//        PolygonOptions rectOptions2 = new PolygonOptions()
//                .add(new LatLng(24.30, 120.9),
//                        new LatLng(24.40, 120.9),
//                        new LatLng(24.40, 121.1),
//                        new LatLng(24.30, 121.1),
//                        new LatLng(24.30, 120.9))
//                .fillColor(Color.rgb(255, 0, 0))
//                .strokeColor(Color.alpha(0));
//
//        // Get back the mutable Polygon
//        mMap.addPolygon(rectOptions2);
//        mMap.addPolygon(rectOptions);
//        try {
//            KmlLayer layer = new KmlLayer(mMap, R.raw.tw_color, getApplicationContext());
//
//            layer.addLayerToMap();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style));

//        try {
////            mLists.put("police_station", new DataSet(readItems(R.raw.police)));
//            mLists.put("police_station", new DataSet(readItems(R.raw.police)));
//        } catch (JSONException e) {
//            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
//        }
//
//        // Check if need to instantiate (avoid setData etc twice)
//        if (mProvider == null) {
////            mProvider = new HeatmapTileProvider.Builder()
////                    .weightedData(mLists.get("police_station").getData())
////                    .radius(30)
//////                    .gradient(ALT_HEATMAP_GRADIENT)
////                    .opacity(0.3)
////                    .build();
////            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
//
//            // Render links
////            attribution.setMovementMethod(LinkMovementMethod.getInstance());
//        } else {
////            mProvider.setData(mLists.get(dataset).getData());
////            mOverlay.clearTileCache();
//        }


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
