package com.org.nagradcore.utils;

import com.org.nagradcore.model.category.CategoryLevel;

/**
 * Created by mac on 15/9/22.
 */
public class CategoryUtils {

    private static final long LEVEL_1_WEIGHT = 10000000L;

    private static final long LEVEL_2_WEIGHT = 1000000L;

    private static final long LEVEL_3_WEIGHT = 1000L;

    private static final long LEVEL_4_WEIGHT = 1L;

    public static long getLevel1Code(long categoryCode) {
        return categoryCode / LEVEL_1_WEIGHT * LEVEL_1_WEIGHT;
    }

    public static long getLevel2Code(long categoryCode) {
        return categoryCode / LEVEL_2_WEIGHT * LEVEL_2_WEIGHT;
    }

    public static long getLevel3Code(long categoryCode) {
        return categoryCode / LEVEL_3_WEIGHT * LEVEL_3_WEIGHT;
    }

    public static long getLevel4Code(long categoryCode) {
        return categoryCode / LEVEL_4_WEIGHT * LEVEL_4_WEIGHT;
    }

    /**
     * get the next same level category code to the param.
     * for example,
     * return 11000200 if input is 11000100,
     * return 11000102 if input is 11000101
     *
     * @return
     */
    public static long getNextCategoryCode(int categoryCode) {
        if (categoryCode % LEVEL_1_WEIGHT == 0) {
            return (categoryCode / LEVEL_1_WEIGHT + 1) * LEVEL_1_WEIGHT;
        } else if (categoryCode % LEVEL_2_WEIGHT == 0) {
            return (categoryCode / LEVEL_2_WEIGHT + 1) * LEVEL_2_WEIGHT;
        } else if (categoryCode % LEVEL_3_WEIGHT == 0) {
            return (categoryCode / LEVEL_3_WEIGHT + 1) * LEVEL_3_WEIGHT;
        } else if (categoryCode % LEVEL_4_WEIGHT == 0) {
            return (categoryCode / LEVEL_4_WEIGHT + 1) * LEVEL_4_WEIGHT;
        } else {
            return categoryCode + 1;
        }
    }

    public static int getCategoryLevel(long categoryCode) {
        if (categoryCode % 10000000 == 0) {
            return 1;
        } else if (categoryCode % 1000000 == 0) {
            return 2;
        } else if (categoryCode % 1000 == 0) {
            return 3;
        } else {
            return 4;
        }
    }

    public static CategoryLevel getLevel(long categoryCode) {
        if (categoryCode % 10000000 == 0) {
            return CategoryLevel.ONE;
        } else if (categoryCode % 1000000 == 0) {
            return CategoryLevel.TWO;
        } else if (categoryCode % 1000 == 0) {
            return CategoryLevel.THREE;
        } else {
            return CategoryLevel.FOUR;
        }
    }

    public static boolean isElevator(long categoryCode) {
        if (categoryCode == 24092000 || categoryCode == 24091000) return true;
        return false;
    }

    public static boolean isStair(long categoryCode) {
        if (categoryCode == 24097000 || categoryCode == 24098000 || categoryCode == 13164000) return true;
        return false;
    }
}
