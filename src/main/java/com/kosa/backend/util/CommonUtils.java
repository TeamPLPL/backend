package com.kosa.backend.util;

public class CommonUtils {

    public static double calculateAchievementRate(int currentAmount, int targetAmount) {

        double achievementRate = (double) currentAmount / targetAmount * 100;

        return Math.round(achievementRate * 10.0) / 10.0;
    }
}
