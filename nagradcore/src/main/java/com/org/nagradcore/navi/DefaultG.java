package com.org.nagradcore.navi;

import com.org.nagradcore.utils.CategoryUtils;

/**
 * Created by wyx on 9/2/15.
 */
public class DefaultG implements G {
    @Override
    public double G(AStarVertex current, AStarPath path, double floorHeightDiff) {
        if (path instanceof AStarLanePath) {
            return path.getWeight();
        } else if (path instanceof AStarConnectionPath) {
            long categoryId = ((AStarConnectionPath) path).getConnection().getCategoryId();
            if (floorHeightDiff == 0) {
                return 10000;
            }
            if (floorHeightDiff > 13) {
                if (!CategoryUtils.isElevator(categoryId)) {
                    return 10000;
                }
            } else {
                if (CategoryUtils.isStair(categoryId)) {
                    return 10000;
                }
                if (CategoryUtils.isElevator(categoryId)) {
                    if (current.getParent() != null) {
                        AStarPath lastPath = current.getParent().findPath(current);
                        if (lastPath instanceof AStarConnectionPath) {
                            long lastPathCategoryId = ((AStarConnectionPath) lastPath).getConnection().getCategoryId();
                            if (CategoryUtils.isElevator(lastPathCategoryId)) return 30;
                        }
                    }
                    return 190;
                }
            }
            return 60;
        }
        return 0;
    }
}
