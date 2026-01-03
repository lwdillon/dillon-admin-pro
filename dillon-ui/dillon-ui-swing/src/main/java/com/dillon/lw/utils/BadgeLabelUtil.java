package com.dillon.lw.utils;

import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.store.AppStore;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;

public class BadgeLabelUtil {

    public static JLabel getBadgeLabel(DictTypeEnum dictType, Object dictDataValue) {
        DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(dictType).get(dictDataValue + "");
        JLabel redBadge = new JLabel(dict.getLabel());
        switch (dict.getColorType()) {
            case "primary":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "primary");
                break;
            case "success":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "success");
                break;
            case "info":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "info");
                break;
            case "warning":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "warning");
                break;
            case "danger":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "danger");
                break;
            default:
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "info");
        }
        return redBadge;

    }

    public static JLabel getBadgeLabel(String text, String colortype) {
        JLabel redBadge = new JLabel(text);
        switch (colortype) {
            case "primary":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "primary");
                break;
            case "success":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "success");
                break;
            case "info":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "info");
                break;
            case "warning":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "warning");
                break;
            case "danger":
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "danger");
                break;
            default:
                redBadge.putClientProperty(FlatClientProperties.STYLE_CLASS, "info");
        }
        return redBadge;

    }
}
