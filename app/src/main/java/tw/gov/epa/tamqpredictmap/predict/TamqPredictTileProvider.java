package tw.gov.epa.tamqpredictmap.predict;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by user on 2017/7/31.
 */

public class TamqPredictTileProvider implements TileProvider{

    private Collection<WeightedLatLng> mData;
    private int mRadius = 20;
    private double mOpacity;

    private TamqPredictTileProvider(TamqPredictTileProvider.Builder builder) {
        this.mData = builder.data;
        this.mRadius = builder.radius;
        this.mOpacity = builder.opacity;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        double tileWidth = 1.0D / Math.pow(2.0D, (double)zoom);
        double padding = tileWidth * (double)this.mRadius / 512.0D;
        double tileWidthPadded = tileWidth + 2.0D * padding;
        double bucketWidth = tileWidthPadded / (double)(512 + this.mRadius * 2);
        double minX = (double)x * tileWidth - padding;
        double maxX = (double)(x + 1) * tileWidth + padding;
        double minY = (double)y * tileWidth - padding;
        double maxY = (double)(y + 1) * tileWidth + padding;
        double xOffset = 0.0D;
        Object wrappedPoints = new ArrayList();
        Bounds tileBounds;
        if(minX < 0.0D) {
            tileBounds = new Bounds(minX + 1.0D, 1.0D, minY, maxY);
            xOffset = -1.0D;
        } else if(maxX > 1.0D) {
            tileBounds = new Bounds(0.0D, maxX - 1.0D, minY, maxY);
            xOffset = 1.0D;
        }
        return null;
    }

    private static Collection<WeightedLatLng> wrapData(Collection<LatLng> data) {
        ArrayList weightedData = new ArrayList();
        Iterator var2 = data.iterator();

        while(var2.hasNext()) {
            LatLng l = (LatLng)var2.next();
            weightedData.add(new WeightedLatLng(l));
        }

        return weightedData;
    }

    public static class Builder {
        private Collection<WeightedLatLng> data;
        private int radius = 20;
        private double opacity;

        public Builder() {
            this.opacity = 0.7D;
        }

        public TamqPredictTileProvider.Builder data(Collection<LatLng> val) {
            return this.weightedData(TamqPredictTileProvider.wrapData(val));
        }

        public TamqPredictTileProvider.Builder weightedData(Collection<WeightedLatLng> val) {
            this.data = val;
            if(this.data.isEmpty()) {
                throw new IllegalArgumentException("No input points.");
            } else {
                return this;
            }
        }

        public TamqPredictTileProvider.Builder radius(int val) {
            this.radius = val;
            if(this.radius >= 10 && this.radius <= 50) {
                return this;
            } else {
                throw new IllegalArgumentException("Radius not within bounds.");
            }
        }

        public TamqPredictTileProvider.Builder opacity(double val) {
            this.opacity = val;
            if(this.opacity >= 0.0D && this.opacity <= 1.0D) {
                return this;
            } else {
                throw new IllegalArgumentException("Opacity must be in range [0, 1]");
            }
        }

        public TamqPredictTileProvider build() {
            if(this.data == null) {
                throw new IllegalStateException("No input data: you must use either .data or .weightedData before building");
            } else {
                return new TamqPredictTileProvider(this);
            }
        }
    }
}
