package com.dillon.lw.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * wpagination窗格
 *
 * @author liwen
 * @date 2022/06/24
 */
public class WPaginationPane extends JPanel {

    /**
     * 默认页面大小
     */
    private static final long DEFAULT_PAGE_SIZE = 10;

    private JLabel totalLabel;
    private JComboBox<Integer> pageLineCombox;
    private JTextField pageTextField;

    private JButton prePageButton;
    private JButton nextPageButton;

    private JButton preToPageButton;
    private JButton nextToPageButton;
    private JButton goButton;
    private JToolBar pageButBar;


    /**
     * 总记录数
     */
    private long total;

    /**
     * 页面大小
     */
    private long pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 页面索引
     */
    private long pageIndex = 1;

    public WPaginationPane() {
        preToPageButton = createButton(new FlatSVGIcon("icons/xianxingshenglve.svg", 20, 20));
        preToPageButton.setRolloverIcon(new FlatSVGIcon("icons/double-arrow-left.svg", 20, 20));
        preToPageButton.addActionListener(e -> setPageIndex(pageIndex - 5));
        nextToPageButton = createButton(new FlatSVGIcon("icons/xianxingshenglve.svg", 20, 20));
        nextToPageButton.addActionListener(e -> setPageIndex(pageIndex + 5));
        nextToPageButton.setRolloverIcon(new FlatSVGIcon("icons/double-arrow-right.svg", 20, 20));


        prePageButton = createButton(new FlatSVGIcon("icons/arrow-left.svg", 20, 20));
        prePageButton.addActionListener(e -> setPageIndex(getPageIndex() - 1));
        nextPageButton = createButton(new FlatSVGIcon("icons/arrow-right.svg", 20, 20));
        nextPageButton.addActionListener(e -> setPageIndex(getPageIndex() + 1));


        pageLineCombox = new JComboBox<Integer>(new Integer[]{10, 20, 30, 50, 100}) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 30);
            }
        };
        pageLineCombox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value + "条/页", index, isSelected, cellHasFocus);
            }
        });
        pageLineCombox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setPageSize((Integer) e.getItem());   //修改后
            }
        });


        JToolBar goPanel = new JToolBar();
        goPanel.setOpaque(false);
        goPanel.setLayout(new HorizontalLayout(5));
        goPanel.add(new JLabel("前往"));
        goPanel.add(pageTextField = new JTextField() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 30);
            }
        });
        pageTextField.setHorizontalAlignment(JTextField.CENTER);
        pageTextField.addActionListener(e -> {
            if (StringUtils.isBlank(pageTextField.getText())) {
                return;
            }
            setPageIndex(Integer.parseInt(pageTextField.getText()));
        });
        goPanel.add(new JLabel("页"));
        goPanel.add(goButton = createButton(new FlatSVGIcon("icons/enter.svg", 20, 20)));
        goButton.addActionListener(e -> {
            if (StringUtils.isBlank(pageTextField.getText())) {
                return;
            }
            setPageIndex(Integer.parseInt(pageTextField.getText()));
        });
        pageButBar = new JToolBar();
        pageButBar.setOpaque(false);
        pageButBar.setBackground(new Color(0,0,0,0));
        pageButBar.setLayout(new HorizontalLayout(5));
        pageButBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        toolBar.setLayout(new HorizontalLayout(5));
        toolBar.add(totalLabel = new JLabel(""));
        toolBar.add(pageLineCombox);
        toolBar.add(prePageButton);
        toolBar.add(pageButBar);
        toolBar.add(nextPageButton);
        toolBar.add(goPanel);

        this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        this.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
        this.add(toolBar);
        setTotal(0);
    }


    public void setTotal(long total) {
        this.total = total;
        totalLabel.setText("共 " + total + " 条");
        updatePageBar();
    }

    private void updatePageBar() {
        pageButBar.removeAll();
        long size = total % getPageSize() > 0 ? (total / getPageSize() + 1) : total / getPageSize();
        if (size == 0) {
            this.setVisible(false);
            return;
        }
        this.setVisible(true);
        ButtonGroup group = new ButtonGroup();
        if (size < 9) {

            for (int i = 0; i < size; i++) {

                PageButton pageButton = new PageButton((i + 1));
                group.add(pageButton);
                pageButBar.add(pageButton);
            }
        } else {

            if (pageIndex < 4) {
                for (int i = 1; i < 7; i++) {
                    PageButton pageButton = new PageButton(i);
                    group.add(pageButton);
                    pageButBar.add(pageButton);
                }
                pageButBar.add(nextToPageButton);
                PageButton pageButton = new PageButton(size);
                group.add(pageButton);
                pageButBar.add(pageButton);
            } else {

                if (size - pageIndex < 4) {

                    PageButton pageButton = new PageButton(1);
                    group.add(pageButton);
                    pageButBar.add(pageButton);
                    pageButBar.add(preToPageButton);
                    PageButton pageBut1 = new PageButton(size - 6);
                    PageButton pageBut2 = new PageButton(size - 5);
                    PageButton pageBut3 = new PageButton(size - 4);
                    PageButton pageBut4 = new PageButton(size - 3);
                    PageButton pageBut5 = new PageButton(size - 2);
                    PageButton pageBut6 = new PageButton(size - 1);
                    PageButton pageBut7 = new PageButton(size);
                    group.add(pageBut1);
                    group.add(pageBut2);
                    group.add(pageBut3);
                    group.add(pageBut4);
                    group.add(pageBut5);
                    group.add(pageBut6);
                    group.add(pageBut7);
                    pageButBar.add(pageBut1);
                    pageButBar.add(pageBut2);
                    pageButBar.add(pageBut3);
                    pageButBar.add(pageBut4);
                    pageButBar.add(pageBut5);
                    pageButBar.add(pageBut6);
                    pageButBar.add(pageBut7);

                } else {

                    PageButton pageBut1 = new PageButton(1);
                    PageButton pageBut2 = new PageButton(pageIndex - 2);
                    PageButton pageBut3 = new PageButton(pageIndex - 1);
                    PageButton pageBut4 = new PageButton(pageIndex);
                    PageButton pageBut5 = new PageButton(pageIndex + 1);
                    PageButton pageBut6 = new PageButton(pageIndex + 2);
                    PageButton pageBut7 = new PageButton(size);
                    group.add(pageBut1);
                    group.add(pageBut2);
                    group.add(pageBut3);
                    group.add(pageBut4);
                    group.add(pageBut5);
                    group.add(pageBut6);
                    group.add(pageBut7);
                    pageButBar.add(pageBut1);
                    pageButBar.add(preToPageButton);
                    pageButBar.add(pageBut2);
                    pageButBar.add(pageBut3);
                    pageButBar.add(pageBut4);
                    pageButBar.add(pageBut5);
                    pageButBar.add(pageBut6);
                    pageButBar.add(nextToPageButton);
                    pageButBar.add(pageBut7);
                }
            }

        }

        for (Component component : pageButBar.getComponents()) {
            if (component instanceof PageButton) {
                if (((PageButton) component).getPage() == getPageIndex()) {
                    ((PageButton) component).setSelected(true);
                }
            }
        }
        revalidate();

//        goToPage(getPageIndex(),getPageSize());
    }


    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        setPageIndex(1);
    }

    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        long size = total % getPageSize() > 0 ? (total / getPageSize() + 1) : total / getPageSize();
        if (pageIndex <= 0) {
            this.pageIndex = 1;
        } else if (pageIndex >= size) {
            this.pageIndex = size;
        } else {
            this.pageIndex = pageIndex;
        }
        nextPageButton.setEnabled(this.pageIndex != size);
        prePageButton.setEnabled(this.pageIndex != 1);
        updatePageBar();
    }

    protected void goToPage(long pageIndex, int pageSize) {

    }


    private JButton createButton(Icon icon) {
        return new JButton(icon) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 30);
            }
        };
    }

    private class PageButton extends JToggleButton {
        private long page;

        public PageButton(long page) {
            this.page = page;
            setText(page + "");

            addActionListener(e -> setPageIndex(page));
        }

        @Override
        public Color getForeground() {

            return super.getForeground();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 30);
        }

        public long getPage() {
            return page;
        }
    }
}
