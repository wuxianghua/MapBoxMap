package com.org.mylibrary.indoor.navigate;

import com.palmap.astar.navi.AStar;
import com.palmap.astar.navi.AStarPath;
import com.vividsolutions.jts.geom.Coordinate;

import java.util.List;

/**
 * Created by wtm on 2017/10/9.
 */
public interface INavigateManager<Route> {

    enum NavigateState {
        OK(0),
        SWITCH_NAVIGATE_SUCCESS(1),
        CLIP_NAVIGATE_SUCCESS(2),
        NAVIGATE_REQUEST_ERROR(3),
        NAVIGATE_REQUEST_TIMEOUT(4),
        NAVIGATE_UNKNOWN_ERROR(5),
        NAVIGATE_NOT_FOUND(6),
        CLIP_NAVIGATE_ERROR(7),
        PLANARGRAPH_ERROR(8);
        int state;
        public static INavigateManager.NavigateState getState(int state) {
            INavigateManager.NavigateState[] var1 = values();
            for(INavigateManager.NavigateState s : var1) {
                if(s.state == state) {
                    return s;
                }
            }
            return OK;
        }
        NavigateState(int state) {
            this.state = state;
        }
        public int getState() {
            return this.state;
        }
    }

    interface Listener<T>{
        void OnNavigateComplete(NavigateState state,List<AStarPath> routes,T route);
    }

    void setNavigateListener(Listener<Route> listener);

    void switchPlanarGraph(long id);

    void navigation(double fromX, double fromY, long fromPlanargraph, double toX, double toY, long toPlanargraph);

    void navigation(double fromX, double fromY, long fromPlanargraph, double toX, double toY, long toPlanargraph, long currentPlanargraph);

    void clear();

    int getLength();

    int getSize();

    long[] getKeys();

    double getMinDistanceByPoint(Coordinate coordinate);

    void clipFeatureCollectionByCoordinate(Coordinate coordinate);

    void clipFeatureCollectionByCoordinate(Coordinate coordinate, long planarGraph);

    Coordinate getPointOfIntersectioanByPoint(Coordinate coordinate);

    long[] getAllPlanarGraphId();

    double getEachLineLength(long id, int index);

    double getFloorLineLength(long id);

    double getTotalLineLength();

    void destructor();
}
