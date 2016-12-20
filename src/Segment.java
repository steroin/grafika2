/**
 * Created by Sergiusz on 22.10.2016.
 */
public abstract class Segment {
    public abstract SegmentType getType();
    public abstract String serialize();
    public abstract void adjustToPoints(int[] x, int[] y);
    public abstract void moveTo(int x, int y);
    public abstract boolean isIn(int x, int y);

    public static Segment fromPoints(SegmentType type, int[] x, int[] y){
        Segment newSegment = null;

        if(type==SegmentType.ELLIPSE){
            if(x.length==2 && y.length==2){
                int width = Math.abs(x[0]-x[1]);
                int height = Math.abs(y[0]-y[1]);
                int _x = x[1]<x[0]?x[1]:x[0];
                int _y = y[1]<y[0]?y[1]:y[0];
                newSegment = new SegmentEllipse(_x, _y, width, height);
            }
        }
        else if(type==SegmentType.RECTANGLE){
            if(x.length==2 && y.length==2){
                int width = Math.abs(x[0]-x[1]);
                int height = Math.abs(y[0]-y[1]);
                int _x = x[1]<x[0]?x[1]:x[0];
                int _y = y[1]<y[0]?y[1]:y[0];
                newSegment = new SegmentRectangle(_x, _y, width, height);
            }
        }
        else if(type==SegmentType.POLYGON){
            if(x.length==2 && y.length==2){
                newSegment = new SegmentPolygon(x,y);
            }
        }
            return newSegment;
    }
    public static Segment deserialize(String s){

        String[] arr = s.split(";");
        Segment ret = null;

        if(arr[0].equals("e")){
            ret = new SegmentEllipse(Integer.parseInt(arr[1]),
                    Integer.parseInt(arr[2]),
                    Integer.parseInt(arr[3]),
                    Integer.parseInt(arr[4]));
        }
        else if(arr[0].equals("r")){
            ret = new SegmentRectangle(Integer.parseInt(arr[1]),
                    Integer.parseInt(arr[2]),
                    Integer.parseInt(arr[3]),
                    Integer.parseInt(arr[4]));
        }
        else if(arr[0].equals("p")){
            int num = Integer.parseInt(arr[1]);
            int[] x = new int[num];
            int[] y = new int[num];

            for(int i=0;i<num;i++)x[i] = Integer.parseInt(arr[i+2]);
            for(int i=0;i<num;i++)y[i] = Integer.parseInt(arr[i+num+2]);
            ret = new SegmentPolygon(x, y);
        }
        return ret;
    }
}
