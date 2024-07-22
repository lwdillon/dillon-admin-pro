import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class LeftTitleRightArrowJTree {

    public static void main(String[] args) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Node 1");
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("Node 2");
        root.add(node1);
        root.add(node2);

        JTree tree = new JTree(root);
        tree.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT); // 设置组件方向为左到右
        tree.setRootVisible(false); // 隐藏根节点

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setLeafIcon(null); // 禁用叶子节点图标
        renderer.setClosedIcon(null); // 禁用闭合节点图标
        renderer.setOpenIcon(null); // 禁用展开节点图标

        // 设置右对齐以将箭头图标显示在右边
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        renderer.setHorizontalTextPosition(SwingConstants.LEFT);

        JFrame frame = new JFrame("Left Title, Right Arrow JTree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(tree));
        frame.pack();
        frame.setVisible(true);
    }
}
