package com.utilities;

/**
 * Created by xy on 11/05/16.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isValidDouble(Double zScore) {
        return !(zScore.equals(Double.NaN) || zScore.equals(Double.NEGATIVE_INFINITY) || zScore
                .equals(Double.POSITIVE_INFINITY));
    }
}
