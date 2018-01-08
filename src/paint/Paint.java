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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Line;
import javax.imageio.ImageIO;
import javax.swing.*;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Paint implements ActionListener{

    private JButton btn_color;
    private JButton btn_stroke,btn_save,btn_background,btn_clear,btn_simmetry;
    private JButton[] btn_strokes=new JButton[5];
    private String[] btn_strokesText={"Stroke Size", "Basic Stroke","Wobble Stroke","Composite Stroke", "Dot Stroke"};
    JComboBox petList;
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
        jpanel.setLayout(new GridLayout(0,3));
        btn_background = new JButton("Background Color");
        btn_background.addActionListener(this);
        btn_color = new JButton("Color");
        btn_color.addActionListener(this);
        //btn_stroke = new JButton("Stroke");
        //btn_stroke.addActionListener(this);
        btn_clear = new JButton("Clear");
        btn_clear.addActionListener(this);
        btn_save = new JButton("Save Image");
        btn_save.addActionListener(this);
        btn_simmetry = new JButton("Simmetry factor");
        btn_simmetry.addActionListener(this);
        
        for(int i=0;i<btn_strokes.length;i++){
            btn_strokes[i]=new JButton(btn_strokesText[i]);
        }
        
        petList = new JComboBox(btn_strokesText);
        petList.setRenderer(new MyComboBoxRenderer("Stroke Options"));
        petList.setSelectedIndex(-1); 
        petList.addActionListener(this);
        
        jpanel.add(btn_background);
        jpanel.add(btn_color);
        jpanel.add(petList);
        jpanel.add(btn_simmetry);
        jpanel.add(btn_clear);
        jpanel.add(btn_save);
        jpanel.setBackground(Color.DARK_GRAY);
        jpanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                200
        ));

        container.add(drawPanel);
        container.add(jpanel);

        JFrame frame = new JFrame("Mandala Maker");
        frame.getContentPane().add(container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Paint().createAndShowUI();
                     UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                }
            }
        });
    }


    //@Override;
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_color)
            newColor = JColorChooser.showDialog(null, "Choose a colour", newColor);

        else if (e.getSource() == petList) {
            JComboBox cb = (JComboBox) e.getSource();
            int int_index = cb.getSelectedIndex();
            if (int_index == 0) {
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
                int newStrokeInt = (int) optionPane.getInputValue();
                newStroke = (float) newStrokeInt;
                System.out.println("Input: " + newStroke);
               
                STDrawPanel.strk_strokes[0] = new BasicStroke(Paint.newStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                STDrawPanel.strk_strokes[1] = new WobbleStroke(5, Paint.newStroke/2);
                STDrawPanel.strk_strokes[2] = new CompositeStroke(new BasicStroke(Paint.newStroke), new BasicStroke(0.5f));
                STDrawPanel.strk_strokes[3] = new ShapeStroke(new Shape[]{new Ellipse2D.Float(0, 0, Paint.newStroke, Paint.newStroke)}, Paint.newStroke*4);
                STDrawPanel.strk_currentStroke = STDrawPanel.strk_strokes[STDrawPanel.int_strokeIndex];
            } else{
                STDrawPanel.strk_currentStroke = STDrawPanel.strk_strokes[int_index - 1];
                STDrawPanel.int_strokeIndex=int_index - 1;
            }
                
            
            String petName = (String) cb.getSelectedItem();
            System.out.println(cb.getSelectedIndex());
        }
        /*else if (e.getSource() == btn_stroke) {

            

        }*/
        
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
        
        else if(e.getSource()==btn_simmetry){
            int ans = Integer.parseInt( JOptionPane.showInputDialog("Select the simmetry factor(2-25)"));
                if(ans>=2&&ans<=25){
                    STDrawPanel.int_simmetryFactor=ans;
                    STDrawPanel.arl_line.clear();
                    drawPanel.repaint();
                }
        }
        
    }
    
    public static void saveImage(BufferedImage off_Image){
         File dir2 = new File("PATH_TO_YOUR_DIRECTORY");
            
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.showOpenDialog(null);
            
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                dir2 = new File(chooser.getSelectedFile()+".png");
            }

            try {
                ImageIO.write(off_Image, "png", dir2);
            } catch (IOException ex) {
                Logger.getLogger(Paint.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

}

@SuppressWarnings("serial")
class STDrawPanel extends JPanel {

    private static final int ST_WIDTH = 500;
    private static final int ST_HEIGHT = 500;
    public static int int_simmetryFactor=8;
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
    public static ArrayList<Line> arl_line = new ArrayList<Line>();
    private int colorIndex = 0;
    public static Stroke[] strk_strokes={new BasicStroke(Paint.newStroke,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    ,new WobbleStroke(10,10)
    ,new CompositeStroke( new BasicStroke( 10f ), new BasicStroke( 0.5f ) )
    ,new ShapeStroke(new Shape[]{new Ellipse2D.Float(0, 0, 4, 4)}, 20.0f)};
    
    public static Stroke strk_currentStroke=strk_strokes[0];
    public static int int_strokeIndex=0;
            
            /*g2.setStroke(new BasicStroke(Paint.newStroke,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));*/
        /*g2.setStroke(
		new TextStroke("Hello friend",new Font("Arial", Font.PLAIN, 20),false,false)
	);*/
        //g2.setStroke( new CompositeStroke( new BasicStroke( 10f ), new BasicStroke( 0.5f ) ) );
    Point center=null;

    public STDrawPanel() {
        repaint();
    }
    
    
    /*private void createLines(){
        arl_line.clear();
        int int_centerX = this.getWidth() / 2;
        int int_centerY = this.getHeight() / 2;
        int int_pointX=this.getWidth() / 2,int_pointY=0;
        int int_angle=(int)(360/int_simmetryFactor);
        //int int_angle=(int)((int_angleDeg*Math.PI)/180);
        arl_line.add(new Line(int_centerX,int_centerY,int_pointX,int_pointY));

        for (int i = 0; i < int_simmetryFactor - 1; i++) {
            int newX = (int) (int_centerX + (int_pointX - int_centerX) * Math.cos(int_angle) - (int_pointY - int_centerY) * -Math.sin(int_angle));
            int newY = (int) (int_centerY + (int_pointX - int_centerX) * -Math.sin(int_angle) + (int_pointY - int_centerY) * Math.cos(int_angle));
            int_angle+=int_angle;
            arl_line.add(new Line(int_centerX,int_centerY,newX,newY));
        }
    }*/
    
    private void createLines(){
        arl_line.clear();
        /*int int_centerX = this.getWidth() / 2;
        int int_centerY = this.getHeight() / 2;*/
        //int ,int_pointY=0;
        center=new Point(this.getWidth() / 2,this.getHeight() / 2);
        Point point=new Point(0,0);
        
        double x1 = point.x - center.x;
        double y1 = point.y - center.y;
        
        double doub_angle=Math.toRadians(360/int_simmetryFactor);
        //int int_angle=(int)((int_angleDeg*Math.PI)/180);
        arl_line.add(new Line(center.x,center.y,point.x,point.y));

        for (int i = 1; i < int_simmetryFactor; i++) {
            double x2 = x1 * Math.cos(doub_angle*i) - y1 * Math.sin(doub_angle*i);
            double y2 = x1 * Math.sin(doub_angle*i) + y1 * Math.cos(doub_angle*i);

            point.x = (int) (x2 + center.x);
            point.y = (int) (y2 + center.y);
            
            arl_line.add(new Line(center.x,center.y,point.x,point.y));
        }
    }
    
    
    
    private void newPoint(Point point, Point point2, Graphics2D g2){
        /*int int_centerX = this.getWidth() / 2;
        int int_centerY = this.getHeight() / 2;*/
        //int ,int_pointY=0;
        //Point center=new Point(this.getWidth() / 2,this.getHeight() / 2);
        
        double x1 = point.x - center.x;
        double y1 = point.y - center.y;
        double x2 = point2.x - center.x;
        double y2 = point2.y - center.y;
        
        double doub_angle=Math.toRadians(360/int_simmetryFactor);
        //int int_angle=(int)((int_angleDeg*Math.PI)/180);
        //arl_line.add(new Line(center.x,center.y,point.x,point.y));

        for (int i = 1; i < int_simmetryFactor; i++) {
            double x3 = x1 * Math.cos(doub_angle*i) - y1 * Math.sin(doub_angle*i);
            double y3 = x1 * Math.sin(doub_angle*i) + y1 * Math.cos(doub_angle*i);
            
            double x4 = x2 * Math.cos(doub_angle*i) - y2 * Math.sin(doub_angle*i);
            double y4 = x2 * Math.sin(doub_angle*i) + y2 * Math.cos(doub_angle*i);

            point.x = (int) (x3 + center.x);
            point.y = (int) (y3 + center.y);
            
            point2.x = (int) (x4 + center.x);
            point2.y = (int) (y4 + center.y);
           
            g2.drawLine(point.x, point.y, point2.x, point2.y);
        }
    }
    
    /*public float pDistance(float x, float y, float x1, float y1, float x2, float y2) {

      float A = x - x1; // position of point rel one end of line
      float B = y - y1;
      float C = x2 - x1; // vector along line
      float D = y2 - y1;
      float E = -D; // orthogonal vector
      float F = C;

      float dot = A * E + B * F;
      float len_sq = E * E + F * F;

      return (float) (Math.abs(dot) / Math.sqrt(len_sq));
    }*/

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*int int_centerx=this.getWidth()/2;
        int int_centery=this.getHeight()/2;*/
        g.drawImage(bimg_bckg, 0, 0, this);
        if(arl_line.size()==0)createLines();
        
        g.setColor(Color.black);
        for(int i=0;i<arl_line.size();i++){
            g.drawLine((int)arl_line.get(i).getStartX(), (int)arl_line.get(i).getStartY(), (int)arl_line.get(i).getEndX(), (int)arl_line.get(i).getEndY());
        }
        /*g.drawLine(0, 0, int_centerx, int_centery);
        g.drawLine(this.getWidth(), 0, int_centerx, int_centery);
        g.drawLine(0, this.getHeight(), int_centerx, int_centery);
        g.drawLine(this.getWidth(), this.getHeight(), int_centerx, int_centery);*/
        
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
        g2.setStroke(strk_currentStroke);
        
                
        
        g2.setColor(Paint.newColor);
        if (points != null && points.size() > 1) {
            for (int i = 0; i < points.size() - 1; i++) {
                int x1 = points.get(i).x;
                int y1 = points.get(i).y;
                int x2 = points.get(i + 1).x;
                int y2 = points.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
                newPoint(new Point(x1,y1),new Point(x2,y2),g2);
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
        /*for(int i=0;i<arl_line.size();i++){
            System.out.println("Line "+i+" distance: "
                    +pDistance(point.x,point.y,(int)arl_line.get(i).getStartX(),(int)arl_line.get(i).getStartY(),(int)arl_line.get(i).getEndX(),(int)arl_line.get(i).getEndY()));
        }*/
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

 class CompositeStroke implements Stroke {
		private Stroke stroke1, stroke2;

		public CompositeStroke( Stroke stroke1, Stroke stroke2 ) {
			this.stroke1 = stroke1;
			this.stroke2 = stroke2;
		}

		public Shape createStrokedShape( Shape shape ) {
			return stroke2.createStrokedShape( stroke1.createStrokedShape( shape ) );
		}
	}


class MyComboBoxRenderer extends JLabel implements ListCellRenderer {
  private String _title;

  public MyComboBoxRenderer(String title) {
    _title = title;
  }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean hasFocus) {
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        if (index == -1 && value == null) {
            setText(_title);
        } else
            setText(value.toString());
        return this;
    }
}


