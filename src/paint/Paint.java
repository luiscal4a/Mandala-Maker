/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

/*

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Luis
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Paint implements ActionListener{

    private JButton btn_color;
    private JButton btn_stroke,btn_save,btn_background,btn_clear;
    public static Color newColor = Color.BLACK;
    public static float newStroke = 6f;
    
    STDrawPanel drawPanel = new STDrawPanel();
    STMouseAdapter mAdapter = new STMouseAdapter(drawPanel);

    public void createAndShowUI() {
        
        drawPanel.addMouseListener(mAdapter);
        drawPanel.addMouseMotionListener(mAdapter);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        JPanel jpanel = new JPanel();
        btn_background = new JButton("Background Color");
        btn_background.addActionListener(this);
        btn_color = new JButton("Color");
        btn_color.addActionListener(this);
        btn_stroke = new JButton("Stroke");
        btn_stroke.addActionListener(this);
        btn_clear = new JButton("Clear");
        btn_clear.addActionListener(this);
        btn_save = new JButton("Save Image");
        btn_save.addActionListener(this);
        jpanel.add(btn_background);
        jpanel.add(btn_color);
        jpanel.add(btn_stroke);
        jpanel.add(btn_clear);
        jpanel.add(btn_save);
        jpanel.setBackground(Color.DARK_GRAY);
        jpanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                150
        ));

        container.add(drawPanel);
        container.add(jpanel);

        JFrame frame = new JFrame("Drawing");
        frame.getContentPane().add(container);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Paint().createAndShowUI();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_color)
            newColor = JColorChooser.showDialog(null, "Choose a colour", newColor);
        else if (e.getSource() == btn_stroke) {

            JOptionPane optionPane = new JOptionPane();
            JSlider slider = new JSlider();
            slider.setMajorTickSpacing(10);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setMaximum(50);
            slider.setValue(25);
            optionPane.setInputValue(new Integer(slider.getValue()));
            ChangeListener changeListener = new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    JSlider theSlider = (JSlider) changeEvent.getSource();
                    if (!theSlider.getValueIsAdjusting()) {
                        optionPane.setInputValue(new Integer(theSlider.getValue()));
                    }
                }
            };
            slider.addChangeListener(changeListener);
            optionPane.setMessage(new Object[]{"Select a value: ", slider});
            optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
            optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = optionPane.createDialog(null, "My Slider");
            dialog.setVisible(true);
            int newStrokeInt=(int) optionPane.getInputValue();
            newStroke=(float)newStrokeInt;
            System.out.println("Input: " + newStroke);

        }
        
        else if(e.getSource()==btn_save){
            Graphics2D g2 = STDrawPanel.bimg_final.createGraphics();
            g2.drawImage(STDrawPanel.bimg_bckg, 0, 0, null);
            g2.drawImage(STDrawPanel.bimg_draw, 0, 0, null);
            g2.dispose();
            saveImage(STDrawPanel.bimg_final);
            STDrawPanel.bimg_final= new BufferedImage(STDrawPanel.bimg_bckg.getWidth(), STDrawPanel.bimg_bckg.getHeight(), BufferedImage.TYPE_INT_ARGB);
         }
        
        else if(e.getSource()==btn_background){
            Color bckColor = JColorChooser.showDialog(null, "Choose a colour", STDrawPanel.BACKGROUND_COLOR);
            STDrawPanel.bimg_bckg= new BufferedImage(STDrawPanel.bimg_bckg.getWidth(), STDrawPanel.bimg_bckg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = STDrawPanel.bimg_bckg.createGraphics();
            g2.setColor(bckColor);
            g2.fillRect(0, 0, STDrawPanel.bimg_bckg.getWidth(), STDrawPanel.bimg_bckg.getHeight());
            g2.dispose();
            drawPanel.repaint();
        }
        
        else if(e.getSource()==btn_clear){
           STDrawPanel.bimg_bckg= new BufferedImage(STDrawPanel.bimg_bckg.getWidth(), STDrawPanel.bimg_bckg.getHeight(), BufferedImage.TYPE_INT_ARGB);
           STDrawPanel.bimg_draw= new BufferedImage(STDrawPanel.bimg_bckg.getWidth(), STDrawPanel.bimg_bckg.getHeight(), BufferedImage.TYPE_INT_ARGB);
           STDrawPanel.bimg_final= new BufferedImage(STDrawPanel.bimg_bckg.getWidth(), STDrawPanel.bimg_bckg.getHeight(), BufferedImage.TYPE_INT_ARGB);
           drawPanel.repaint();
        }
    }
    
    public static void saveImage(BufferedImage off_Image){
        
    }

}

@SuppressWarnings("serial")
class STDrawPanel extends JPanel {

    private static final int ST_WIDTH = 500;
    private static final int ST_HEIGHT = 500;
    protected static Color BACKGROUND_COLOR = Color.white;
    private static final float STROKE_WIDTH = 6f;
    private static final Stroke STROKE = new BasicStroke(STROKE_WIDTH,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public static BufferedImage bimg_draw = new BufferedImage(ST_WIDTH, ST_HEIGHT,
            BufferedImage.TYPE_INT_ARGB);
    public static BufferedImage bimg_bckg = new BufferedImage(ST_WIDTH, ST_HEIGHT,
            BufferedImage.TYPE_INT_ARGB);
    public static BufferedImage bimg_final = new BufferedImage(ST_WIDTH, ST_HEIGHT,
            BufferedImage.TYPE_INT_ARGB);
    private Color color = Color.black;
    private ArrayList<Point> points = new ArrayList<Point>();
    private int colorIndex = 0;

    public STDrawPanel() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int int_centerx=this.getWidth()/2;
        int int_centery=this.getHeight()/2;
        
        g.setColor(Color.RED);
        g.drawLine(0, 0, int_centerx, int_centery);
        g.drawLine(this.getWidth(), 0, int_centerx, int_centery);
        g.drawLine(0, this.getHeight(), int_centerx, int_centery);
        g.drawLine(this.getWidth(), this.getHeight(), int_centerx, int_centery);
        
        g.drawImage(bimg_bckg, 0, 0, this);
        g.drawImage(bimg_draw, 0, 0, null);
        Graphics2D g2 = (Graphics2D) g;
        drawCurve(g2);
    }

    private void addCurveToBufferedImage() {
        Graphics2D g2 = bimg_draw.createGraphics();
        drawCurve(g2);
        g2.dispose();
    }

    private void drawCurve(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(Paint.newStroke,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Paint.newColor);
        if (points != null && points.size() > 1) {
            for (int i = 0; i < points.size() - 1; i++) {
                int x1 = points.get(i).x;
                int y1 = points.get(i).y;
                int x2 = points.get(i + 1).x;
                int y2 = points.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ST_WIDTH, ST_HEIGHT);
    }

    public void curveStart(Point point) {
        points.clear();
        points.add(point);
    }

    public void curveEnd(Point point) {
        points.add(point);
        addCurveToBufferedImage();
        points.clear();
        repaint();

        colorIndex++;
        //colorIndex %= colors.length;
        //setColor(colors[colorIndex]);
        setColor(Paint.newColor);
    }

    public void curveAdd(Point point) {
        points.add(point);
        repaint();
    }

    public void setColor(Color color) {
        this.color = Paint.newColor;
    }
}

class STMouseAdapter extends MouseAdapter {

    private STDrawPanel drawPanel;

    public STMouseAdapter(STDrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        drawPanel.curveStart(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        drawPanel.curveEnd(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        drawPanel.curveAdd(e.getPoint());
    }
}



