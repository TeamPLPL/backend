package com.kosa.backend.util;

import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;

public class CommonUtils {
    public static double calculateAchievementRate(int currentAmount, int targetAmount) {

        double achievementRate = (double) currentAmount / targetAmount * 100;

        return Math.round(achievementRate * 10.0) / 10.0;
    }

    // 현재 사용자 (로그인 안되어 있을 시 null) 리턴
    public static User getCurrentUser(CustomUserDetails cud, UserService userService) {
        if(cud == null) { return null; }
        String userEmail = cud.getUsername();
        return userService.getUser(userEmail);
    }
}
