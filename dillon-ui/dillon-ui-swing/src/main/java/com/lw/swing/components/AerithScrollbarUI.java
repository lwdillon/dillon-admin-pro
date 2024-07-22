package com.lw.swing.components;


import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class AerithScrollbarUI extends MetalScrollBarUI {

    private Color barColor = new Color(1f, 1f, 1f, .5f);
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.setOpaque(false);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        decreaseButton = new AerithScrollButton(orientation, scrollBarWidth,
            isFreeStanding);
        decreaseButton.setVisible(false);
        return decreaseButton;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        increaseButton = new AerithScrollButton(orientation, scrollBarWidth, isFreeStanding);
        increaseButton.setVisible(false);
        return increaseButton;
    }
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
      
    	Graphics2D g2 = (Graphics2D) g;
    	
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.SrcOver.derive(.65f));
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
        	
        	g2.translate(thumbBounds.x, thumbBounds.y );
            
            int width = thumbBounds.width ;
            int height = thumbBounds.height - 1;
            
            RoundRectangle2D casing = new RoundRectangle2D.Double(0, 0, width, height, width, width);
            g2.setColor(barColor);
            g2.fill(casing);
            g2.translate(-thumbBounds.x , -thumbBounds.y);
            
        } else {
        	
            g2.translate(thumbBounds.x , thumbBounds.y);
            
            int width = thumbBounds.width;
            
            int height = thumbBounds.height-1;
            
            RoundRectangle2D casing = new RoundRectangle2D.Double(0, 0, width, height, height, height);
            g2.setColor(Color.WHITE);

            Paint paint = g2.getPaint();
            g2.setColor(barColor);
//            g2.setPaint(new GradientPaint(0, 0, new Color(0x818a9b), 0, height, new Color(0x3a4252)));
            g2.fill(casing);
            g2.setPaint(paint);
            
            g2.translate(-thumbBounds.x , -thumbBounds.y );            
        }
        g2.dispose();
    }

    public Color getBarColor() {
        return barColor;
    }

    public void setBarColor(Color barColor) {
        this.barColor = barColor;
    }
}
