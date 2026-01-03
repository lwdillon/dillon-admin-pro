package com.dillon.lw.utils;

import cn.hutool.core.util.ObjectUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

/**
 * @author wenli
 * @date 2024/05/09
 */
public class TreeUtils {

    public static void expandAll(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root));
    }

    private static void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode child = node.getChildAt(i);
                expandAll(tree, parent.pathByAddingChild(child));
            }
        }

        tree.expandPath(parent);
    }

    /**
     * 根据字符串返回对应节点
     */
    public static DefaultMutableTreeNode searchNode(JTree tree, Object nodeStr) {
        DefaultMutableTreeNode node = null;
        Enumeration e = ((DefaultMutableTreeNode) tree.getModel().getRoot()).breadthFirstEnumeration();  //获取root下所有节点
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if (ObjectUtil.isNotNull(node.getUserObject()) && node.getUserObject().toString().equals(nodeStr)) {
                return node;
            }
        }
        return null;
    }

    /***
     * 展开选中节点的所有子节点
     */
    public static void expandTreeNode(JTree tree,DefaultMutableTreeNode selectedNode){
        if(selectedNode.isLeaf()){
            return;
        }
        tree.expandPath(new TreePath(selectedNode.getPath()));
        for (int i = 0; i < selectedNode.getChildCount(); i++) {
            expandTreeNode(tree,(DefaultMutableTreeNode)selectedNode.getChildAt(i));
        }
    }


    /**
     * 设置树的展开状态，使其默认只展开二级节点。该方法会遍历树的节点，展开根节点的子节点，并折叠子节点的子节点，从而实现只展开二级节点的效果。
     * @param tree
     * @param root
     */
    public static void expandOnlySecondLevel(JTree tree, DefaultMutableTreeNode root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            if (node.getChildCount() > 0) {
                tree.expandPath(new TreePath(node.getPath()));
                for (int j = 0; j < node.getChildCount(); j++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(j);
                    if (childNode.getChildCount() > 0) {
                        tree.collapsePath(new TreePath(childNode.getPath()));
                    }
                }
            }
        }
    }
}
