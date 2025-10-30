package com.dillon.lw.fx.view.home;

import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.vo.ActiveProjects;
import com.dillon.lw.fx.vo.Roects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Random;

public class DashboardViewModel extends BaseViewModel {

    private ObservableList<Roects> roects = FXCollections.observableArrayList();
    private ObservableList<ActiveProjects> activeProjects = FXCollections.observableArrayList();


    public DashboardViewModel() {

        Random random = new Random();
        for (int i = 1; i < 11; i++) {
            Roects roectsTmp = new Roects();
            roectsTmp.setProjectName("App design and development");
            roectsTmp.setNo(i + "");
            roectsTmp.setStartDate("2023-04-09");
            roectsTmp.setDueDate("2023-09-09");
            roectsTmp.setStatus("Work in Progress");
            roectsTmp.setClients("Halette Boivin");
            roects.add(roectsTmp);

            ActiveProjects activeProjectsTmp = new ActiveProjects();
            activeProjectsTmp.setProjectName("Brand Logo Design");
            activeProjectsTmp.setProjectLead("Bessie Cooper");
            activeProjectsTmp.setProgress(random.nextDouble(1));
            activeProjectsTmp.setAssignee("");
            activeProjectsTmp.setStatus("status");
            activeProjectsTmp.setDueDate("2023-09-09");
            activeProjects.add(activeProjectsTmp);

        }
    }

    public ObservableList<Roects> getRoects() {
        return roects;
    }

    public ObservableList<ActiveProjects> getActiveProjects() {
        return activeProjects;
    }


}
