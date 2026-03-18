package com.dillon.lw.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class UserHistoryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_USERS = 10;

    private UserHistoryService() {
    }

    /* ---------- 读取 ---------- */

    public static List<UserHistory> loadUsers() {
        String json = AppPrefs.prefs().get(AppPrefs.KEY_HISTORY_USERS, "");
        if (json.isEmpty()) return new ArrayList<>();

        try {
            return MAPPER.readValue(json,
                    new TypeReference<List<UserHistory>>() {
                    });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /* ---------- 记录 ---------- */

    public static void recordLogin(UserHistory user, boolean success) {
        List<UserHistory> users = loadUsers();

        users.removeIf(u -> Objects.equals(u.getUserId(), user.getUserId()));

        user.setLastLoginTime(System.currentTimeMillis());
        user.setLastSuccess(success);

        users.add(0, user);

        users = keepLatestUsers(users);

        save(users);
        saveCurrentUserMarker(user, success);
    }

    /* ---------- 删除 ---------- */

    public static void removeUser(String userId) {
        save(loadUsers().stream()
                .filter(u -> !Objects.equals(u.getUserId(), userId))
                .collect(Collectors.toList()));
    }

    /* ---------- 内部 ---------- */

    private static List<UserHistory> keepLatestUsers(List<UserHistory> users) {
        if (users.size() <= MAX_USERS) {
            return users;
        }
        return users.subList(0, MAX_USERS);
    }

    /**
     * 保存“当前账号 + 上次登录是否成功”标记，供登录页回填使用。
     */
    private static void saveCurrentUserMarker(UserHistory user, boolean success) {
        AppPrefs.prefs().put(AppPrefs.KEY_CURRENT_USER, user.getUserId());
        AppPrefs.prefs().putBoolean(AppPrefs.KEY_LAST_LOGIN_OK, success);
    }

    private static void save(List<UserHistory> users) {
        try {
            AppPrefs.prefs().put(
                    AppPrefs.KEY_HISTORY_USERS,
                    MAPPER.writeValueAsString(users)
            );
        } catch (Exception ignored) {
        }
    }
}
