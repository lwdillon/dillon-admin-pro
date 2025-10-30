package com.dillon.lw.fx.vo;

import lombok.Data;

@Data
public class ActiveProjects {

    private String projectName;
    private String projectLead;
    private String assignee;
    private String status;
    private Double progress;
    private String dueDate;
}
