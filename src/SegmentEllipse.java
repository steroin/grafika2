/**
 * Created by Sergiusz on 22.10.2016.
 */
public class SegmentEllipse extends Segment{
    private int startX;
    private int startY;
    private int width;
    private int height;

    public SegmentEllipse(int startX, int startY, int width, int height) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
    }

    public int getStartX(){
        return startX;
    }

    public void setStartX(int startX){
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public SegmentType getType() {
        return SegmentType.ELLIPSE;
    }

    @Override
    public String serialize() {
        return "e;"+getStartX()+";"+getStartY()+";"+height+";"+height;
    }

    @Override
    public void adjustToPoints(int[] x, int[] y) {
        if(x.length==2 && y.length==2){
            int width = Math.abs(x[0]-x[1]);
            int height = Math.abs(y[0]-y[1]);
            int _x = x[1]<x[0]?x[1]:x[0];
            int _y = y[1]<y[0]?y[1]:y[0];
            setStartX(_x);
            setStartY(_y);
            setWidth(width);
            setHeight(height);
        }
    }

    @Override
    public void moveTo(int x, int y) {
        setStartX(x);
        setStartY(y);
    }

    @Override
    public boolean isIn(int x, int y) {
        double radius1 = width/2;
        double radius2 = height/2;
        double middleX = getStartX()+radius1;
        double middleY = getStartY()+radius2;

        double dist = Math.sqrt(Math.abs(x-middleX)*Math.abs(y-middleY) + Math.abs(x-middleX)*Math.abs(y-middleY));
        double angle = Math.acos((middleX*x+middleY*y)/(Math.sqrt(x*x + y*y)*Math.sqrt(middleX*middleX+middleY*middleY)));
        return dist<=Math.cos(angle)*radius2+Math.sin(radius1);
    }
}
