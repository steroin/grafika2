/**
 * Created by Sergiusz on 22.10.2016.
 */
public class SegmentRectangle extends Segment{
    private int startX;
    private int startY;
    private int width;
    private int height;

    public SegmentRectangle(int startX, int startY, int width, int height){
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
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
        return SegmentType.RECTANGLE;
    }

    @Override
    public String serialize() {
        return "r;"+getStartX()+";"+getStartY()+";"+getWidth()+";"+getHeight();
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
        return x>=getStartX() && x<=getStartX()+getWidth() && y>=getStartY() && y<=getStartY()+getHeight();
    }
}
