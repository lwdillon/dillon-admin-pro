package com.dillon.lw.components;

import com.formdev.flatlaf.ui.FlatArrowButton;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.combobox.TreeExComboBox;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class WTreeComboBox extends TreeExComboBox {


    public WTreeComboBox() {
        super();

    }

    public WTreeComboBox(Object[] objects) {

        super(objects);

    }

    public WTreeComboBox(Vector<?> vector) {
        super(vector);
    }

    public WTreeComboBox(Hashtable<?, ?> hashtable) {
        super(hashtable);
    }

    public WTreeComboBox(TreeNode treeNode) {
        super(treeNode);
    }

    public WTreeComboBox(TreeNode treeNode, boolean b) {
        super(treeNode, b);
    }

    public WTreeComboBox(TreeModel treeModel) {
        super(treeModel);
    }


    @Override
    public AbstractButton createButtonComponent() {

        return new  FlatArrowButton( SwingConstants.SOUTH, UIManager.getString("Component.arrowType"), UIManager.getColor("ComboBox.buttonArrowColor"), UIManager.getColor("ComboBox.buttonDisabledArrowColor"), UIManager.getColor("ComboBox.buttonHoverArrowColor"), (Color) null, UIManager.getColor("ComboBox.buttonPressedArrowColor"), (Color) null );
    }
    @Override
    public PopupPanel createPopupComponent() {

        PopupPanel popupPanel=   super.createPopupComponent();
        popupPanel.setBackground(UIManager.getColor("App.background"));

        return popupPanel;
    }

}
