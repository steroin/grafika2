import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sergiusz on 25.10.2016.
 */
public class PaintCanvas extends JPanel {
    private ArrayList<Shape> shapes;
    private Shape tempShape;
    private Color c;
    private Image img = null;

    public PaintCanvas(){
        this(new Color(255, 0, 0, 127));
    }
    public PaintCanvas(ArrayList<Shape> shapes){
        this(shapes, new Color(255, 0, 0, 127));
    }
    public PaintCanvas(Color c){
        this(new ArrayList<>(), c);
    }
    public PaintCanvas(ArrayList<Shape> segments, Color c){
        super();
        this.shapes = segments;
        tempShape = null;
        this.c = c;
    }

    @Override
    public void paint(Graphics g){

        super.paint(g);
        if(img!=null)g.drawImage(img, 0, 0, this);

        Iterator<Shape> it = shapes.iterator();

        g.setColor(c);
        while(it.hasNext()){
            Shape s = it.next();

            if(s instanceof Ellipse2D.Float){
                Ellipse2D.Float ellipse = (Ellipse2D.Float) s;
                g.fillOval((int) ellipse.getX(), (int)ellipse.getY(), (int)ellipse.getWidth(), (int)ellipse.getHeight());
            }
            else if(s instanceof Rectangle){
                Rectangle rectangle = (Rectangle) s;
                g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
            else if(s instanceof Polygon){
                Polygon polygon = (Polygon) s;
                g.fillPolygon(polygon.xpoints, polygon.ypoints, polygon.xpoints.length);
            }
        }
        g.setColor(Color.DARK_GRAY);
        if(tempShape instanceof Ellipse2D.Float){
            Ellipse2D.Float ellipse = (Ellipse2D.Float) tempShape;
            g.drawOval((int)ellipse.getX(), (int)ellipse.getY(), (int)ellipse.getWidth(), (int)ellipse.getHeight());
        }
        else if(tempShape instanceof Rectangle){
            Rectangle rectangle = (Rectangle) tempShape;
            g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        else if(tempShape instanceof Polygon){
            Polygon polygon = (Polygon) tempShape;
            g.drawPolygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
        }

    }


    public void setTempShape(Shape tempShape){
        this.tempShape = tempShape;
    }

    public void addShape(Shape s){
        shapes.add(s);
    }

    public void setShapes(ArrayList<Shape> list){
        shapes = list;
    }

    public void setColor(Color c){
        this.c = c;
    }

    public void setImage(Image img){
        this.img = img;

        if(img!=null)setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
    }
}
