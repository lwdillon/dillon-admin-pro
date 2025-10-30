
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.dillon.lw.utils;

import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Gets an icon from this class' package.
 *
 * @author wenli
 * @date 2024/05/09
 */
public class IconLoader {
    public static ImageIcon icon(String filename) {
        return new ImageIcon(IconLoader.class.getResource(resolveFile(filename)));
    }

    public static BufferedImage image(String filename) {
        try {
            return ImageIO.read(IconLoader.class.getResource(resolveFile(filename)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * utility method to automagically resolve images that moved into their
     * appropriate iconset subfolders for legacy code
     *
     * @param filename
     * @return the resolved filename
     */
    private static String resolveFile(String filename) {
        // iterate over each location, return on first hit
        for (String path : new String[]{"", "famfam/", "fugue/"}) {
            if (IconLoader.class.getResource(path + filename) != null) {
                return path + filename;
            }
        }

        // if push comes to shove, we let the calling method deal w/ the
        // consequences, exactly as it was before
        return filename;
    }

    /**
     * 让svg图标
     *
     * @param path 路径
     * @param w    w
     * @param h    h
     * @return {@link FlatSVGIcon}
     */
    public static FlatSVGIcon getSvgIcon(String path, int w, int h) {
        if (StrUtil.isBlank(path)||  IconLoader.class.getResourceAsStream("/"+path) == null) {
            path = "icons/item.svg";
        }
        FlatSVGIcon flatSVGIcon = new FlatSVGIcon(path, w, h);
        flatSVGIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
            return UIManager.getColor("Label.foreground") == null ? new Color(0xf8f8f8) : UIManager.getColor("Label.foreground");
        }));
        return flatSVGIcon;
    }
}
