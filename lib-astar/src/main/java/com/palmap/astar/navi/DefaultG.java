package com.palmap.astar.navi;

/**
 * Created by wyx on 9/2/15.
 */
public class DefaultG implements G {
    @Override
    public double G(AStarVertex current, AStarPath path, boolean onSameFloor) {
        if (path instanceof AStarLanePath)
            return path.getWeight();
        else if (path instanceof AStarConnectionPath) {
            if(onSameFloor)
                return 40;
            return 20;
        }
        return 0;
    }
}
