package com.org.mylibrary.indoor.navigate.impl;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.org.mylibrary.indoor.navigate.INavigateManager;
import com.palmap.astar.navi.AStar;
import com.palmap.astar.navi.AStarPath;
import com.palmap.astar.navi.AStarVertex;
import com.palmap.astar.navi.DefaultG;
import com.palmap.astar.navi.DefaultH;
import com.palmap.astar.navi.PathService;
import com.palmap.astar.navi.VertexLoader;
import com.palmap.nagrand.support.util.DataConvertUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.palmap.core.util.UtilsKt.loadFromAsset;

/**
 * Created by wtm on 2017/10/9.
 */

public class MapBoxNavigateManager implements INavigateManager<FeatureCollection> {

    private static GeometryFactory geometryFactory = new GeometryFactory();

    private AStar aStar;

    private HandlerThread handlerThread;

    private Handler routeHandler;

    private Context context;

    private Listener<FeatureCollection> listener = DEFAULT_LISTENER;

    private static Listener<FeatureCollection> DEFAULT_LISTENER = new Listener<FeatureCollection>() {
        @Override
        public void OnNavigateComplete(NavigateState state, List<AStarPath> routes, FeatureCollection route) {

        }
    };

    public MapBoxNavigateManager(Context context,final String routeDataPath) {
        this.context = context ;
        handlerThread =  new HandlerThread("mapBoxNavigateManager");
        handlerThread.start();
        routeHandler = new Handler(handlerThread.getLooper());
        routeHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String pathJsonStr = loadFromAsset(MapBoxNavigateManager.this.context, routeDataPath);
                    JSONObject jsonObject = new JSONObject(pathJsonStr);
                    PathService pathService = new PathService(
                            jsonObject.getJSONArray("vertexes"),
                            jsonObject.getJSONObject("paths"),
                            jsonObject.getJSONObject("connections")
                    );
                    MapBoxNavigateManager.this.aStar = new AStar(new DefaultG(), new DefaultH(), new VertexLoader(pathService));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setNavigateListener(Listener<FeatureCollection> listener) {
        this.listener = listener == null ? DEFAULT_LISTENER : listener;
    }

    @Override
    public void switchPlanarGraph(long id) {

    }

    @Override
    public void navigation(double fromX, double fromY, long fromPlanargraph, double toX, double toY, long toPlanargraph) {
        if (!precondition()){
            return;
        }
        List<AStarPath> routes =  aStar.astar(
                geometryFactory.createPoint(new Coordinate(fromX,fromY)),
                fromPlanargraph,
                geometryFactory.createPoint(new Coordinate(toX,toY)),
                toPlanargraph
        );
        if (routes == null || routes.size() == 0) {
            this.listener.OnNavigateComplete(NavigateState.NAVIGATE_REQUEST_ERROR, null,null);
            return;
        }
        List<Feature> features = new ArrayList<>();
        for (AStarPath aStarPath : routes) {
            AStarVertex fromVertex = aStarPath.getFrom();
            AStarVertex toVertex = aStarPath.getTo();
            Point startPoint = (Point) fromVertex.getVertex().getShape();
            double[] startPosition = DataConvertUtils.INSTANCE.webMercator2LatLng(startPoint.getX(), startPoint.getY());
            Point endPoint = (Point) toVertex.getVertex().getShape();
            double[] endPosition = DataConvertUtils.INSTANCE.webMercator2LatLng(endPoint.getX(), endPoint.getY());
            List<Position> positionList = new ArrayList<>();
            positionList.add(Position.fromCoordinates(startPosition[1],startPosition[0]));
            positionList.add(Position.fromCoordinates(endPosition[1],endPosition[0]));
            LineString lineString = LineString.fromCoordinates(positionList);
            features.add(Feature.fromGeometry(lineString));
        }
        FeatureCollection routeFeatureCollection = FeatureCollection.fromFeatures(features);
        this.listener.OnNavigateComplete(NavigateState.OK,routes,routeFeatureCollection);
    }

    @Override
    public void navigation(double fromX, double fromY, long fromPlanargraph, double toX, double toY, long toPlanargraph, long currentPlanargraph) {
        if (!precondition()){
            return;
        }
    }

    private boolean precondition(){
        if (aStar == null){
            this.listener.OnNavigateComplete(NavigateState.NAVIGATE_REQUEST_ERROR,null,null);
            return false;
        }
        return true;
    }

    @Override
    public void clear() {

    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public long[] getKeys() {
        return new long[0];
    }

    @Override
    public double getMinDistanceByPoint(Coordinate coordinate) {
        return 0;
    }

    @Override
    public void clipFeatureCollectionByCoordinate(Coordinate coordinate) {

    }

    @Override
    public void clipFeatureCollectionByCoordinate(Coordinate coordinate, long planarGraph) {

    }

    @Override
    public Coordinate getPointOfIntersectioanByPoint(Coordinate coordinate) {
        return null;
    }

    @Override
    public long[] getAllPlanarGraphId() {
        return new long[0];
    }

    @Override
    public double getEachLineLength(long id, int index) {
        return 0;
    }

    @Override
    public double getFloorLineLength(long id) {
        return 0;
    }

    @Override
    public double getTotalLineLength() {
        return 0;
    }

    @Override
    public void destructor() {
        if (routeHandler!=null && handlerThread!=null){
            routeHandler.removeCallbacksAndMessages(null);
            routeHandler.post(new Runnable() {
                @Override
                public void run() {
                    handlerThread.quit();
                    routeHandler = null;
                    handlerThread = null;
                }
            });
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destructor();
    }
}
