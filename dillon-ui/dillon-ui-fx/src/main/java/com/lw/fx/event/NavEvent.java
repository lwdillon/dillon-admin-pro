/* SPDX-License-Identifier: MIT */

package com.lw.fx.event;


import com.lw.fx.view.general.Page;

public final class NavEvent extends Event {

    private final Class<? extends Page> page;

    public NavEvent(Class<? extends Page> page) {
        this.page = page;
    }

    public Class<? extends Page> getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "NavEvent{"
                + "page=" + page
                + "} " + super.toString();
    }
}
