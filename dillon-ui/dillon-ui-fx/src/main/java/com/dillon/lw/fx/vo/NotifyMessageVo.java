package com.dillon.lw.fx.vo;

import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class NotifyMessageVo  {
    private SimpleBooleanProperty selectd=new SimpleBooleanProperty();

    private Long id;

    private Long userId;

    private Byte userType;

    private Long templateId;

    private String templateCode;

    private String templateNickname;

    private String templateContent;

    private Integer templateType;

    private Map<String, Object> templateParams;

    private Boolean readStatus;

    private LocalDateTime readTime;

    private LocalDateTime createTime;

    public boolean isSelectd() {
        return selectd.get();
    }

    public SimpleBooleanProperty selectdProperty() {
        return selectd;
    }

    public void setSelectd(boolean selectd) {
        this.selectd.set(selectd);
    }
}
