package com.dillon.lw.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public final class UserHistoryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_USERS = 10;

    private UserHistoryService() {}

    /* ---------- 读取 ---------- */

    public static List<UserHistory> loadUsers() {
        String json = AppPrefs.prefs().get(AppPrefs.KEY_HISTORY_USERS, "");
        if (json.isEmpty()) return new ArrayList<>();

        try {
            return MAPPER.readValue(json,
                    new TypeReference<List<UserHistory>>() {});
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

        if (users.size() > MAX_USERS) {
            users = users.subList(0, MAX_USERS);
        }

        save(users);

        // 记录当前用户
        AppPrefs.prefs().put(AppPrefs.KEY_CURRENT_USER, user.getUserId());
        AppPrefs.prefs().putBoolean(AppPrefs.KEY_LAST_LOGIN_OK, success);
    }

    /* ---------- 删除 ---------- */

    public static void removeUser(String userId) {
        save(loadUsers().stream()
                .filter(u -> !Objects.equals(u.getUserId(), userId))
                .collect(Collectors.toList()));
    }

    /* ---------- 内部 ---------- */

    private static void save(List<UserHistory> users) {
        try {
            AppPrefs.prefs().put(
                    AppPrefs.KEY_HISTORY_USERS,
                    MAPPER.writeValueAsString(users)
            );
        } catch (Exception ignored) {}
    }
}