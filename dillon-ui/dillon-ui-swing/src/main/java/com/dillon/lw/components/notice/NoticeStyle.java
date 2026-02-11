package com.dillon.lw.components.notice;

import javax.swing.*;
import java.awt.*;

/**
 * 通知样式解析器，统一管理不同消息类型的颜色与图标。
 */
final class NoticeStyle {
    final Color foreground;
    final Color background;
    final Color border;
    final String iconPath;

    private NoticeStyle(Color foreground, Color background, Color border, String iconPath) {
        this.foreground = foreground;
        this.background = background;
        this.border = border;
        this.iconPath = iconPath;
    }

    static NoticeStyle resolve(int type) {
        switch (type) {
            case WMessage.SUCCESS:
                return new NoticeStyle(
                        uiColor("ColorPalette.notice.success.fg", 0x67C23A),
                        uiColor("ColorPalette.notice.success.bg", 0xf0f9eb),
                        uiColor("ColorPalette.notice.success.bd", 0xc2e7b0),
                        "icons/success.svg"
                );
            case WMessage.WARNING:
                return new NoticeStyle(
                        uiColor("ColorPalette.notice.warn.fg", 0xE6A23C),
                        uiColor("ColorPalette.notice.warn.bg", 0xfdf6ec),
                        uiColor("ColorPalette.notice.warn.bd", 0xf5dab1),
                        "icons/warning.svg"
                );
            case WMessage.ERROR:
                return new NoticeStyle(
                        uiColor("ColorPalette.notice.error.fg", 0xF56C6C),
                        uiColor("ColorPalette.notice.error.bg", 0xfef0f0),
                        uiColor("ColorPalette.notice.error.bd", 0xfbc4c4),
                        "icons/error.svg"
                );
            default:
                return new NoticeStyle(
                        uiColor("ColorPalette.notice.info.fg", 0x409EFF),
                        uiColor("ColorPalette.notice.info.bg", 0xecf5ff),
                        uiColor("ColorPalette.notice.info.bd", 0xb3d8ff),
                        "icons/info.svg"
                );
        }
    }

    private static Color uiColor(String key, int fallback) {
        Color color = UIManager.getColor(key);
        return color != null ? color : new Color(fallback);
    }
}

