package com.palmap.astar.navi;

/**
 * Created by wyx on 9/1/15.
 */
public interface G {
    double G(AStarVertex current, AStarPath path, boolean onSameFloor);
}
