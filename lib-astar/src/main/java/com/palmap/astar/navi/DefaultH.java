package com.palmap.astar.navi;

/**
 * Created by wyx on 9/2/15.
 */
public class DefaultH implements H {
    @Override
    public double H(AStarVertex current, AStarVertex target) {
        return Math.abs(target.getVertex().getAltitude()
                - current.getVertex().getAltitude())
                + current.getVertex().getShape().distance(target.getVertex().getShape());
    }
}
