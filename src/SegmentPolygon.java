import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static java.util.Arrays.asList;

/**
 * Created by Sergiusz on 22.10.2016.
 */
public class SegmentPolygon extends Segment implements Iterable<int[]> {
    private ArrayList<Integer> pointsX;
    private ArrayList<Integer> pointsY;

    public SegmentPolygon(){
        pointsX = new ArrayList<>();
        pointsY = new ArrayList<>();
    }

    public SegmentPolygon(int[] x, int[] y){
        this();
        for(int i=0;i<x.length;i++)pointsX.add(x[i]);
        for(int i=0;i<y.length;i++)pointsY.add(y[i]);

    }

    public int getFirstX(){
        return pointsX.get(0);
    }

    public int getFirstY(){
        return pointsY.get(0);
    }

    public SegmentPolygon(int[][] points){
        this();

        for(int i=0;i<points.length;i++){
            this.pointsX.add(points[i][0]);
            this.pointsY.add(points[i][1]);
        }
    }

    public void addPoint(int x, int y){
        pointsX.add(x);
        pointsY.add(y);
    }

    @Override
    public Iterator<int[]> iterator() {
        return new Iterator<int[]>() {
            private Iterator<Integer> primalX = pointsX.iterator();
            private Iterator<Integer> primalY = pointsY.iterator();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return primalX.hasNext() && primalY.hasNext();
            }

            @Override
            public int[] next() {
                if(!hasNext())return null;
                index+=2;
                int[] next = new int[2];
                next[0] = primalX.next();
                next[1] = primalY.next();
                return next;
            }
        };
    }

    public int[] getXArray(){
        int size = pointsX.size();
        int ret[] = new int[size];

        for(int i=0;i<size;i++){
            ret[i] = pointsX.get(i);
        }
        return ret;
    }
    public int[] getYArray(){
        int size = pointsY.size();
        int ret[] = new int[size];

        for(int i=0;i<size;i++){
            ret[i] = pointsY.get(i);
        }
        return ret;
    }

    public boolean hasPoint(int x, int y){
        for(int i=0;i<pointsX.size();i++){
            if(pointsX.get(i)==x && pointsY.get(i)==y)return true;
        }
        return false;
    }
    @Override
    public SegmentType getType() {
        return SegmentType.POLYGON;
    }

    @Override
    public String serialize() {
        String ret = "p;"+pointsX.size()+";";
        for(int i=0;i<pointsX.size();i++)ret+=pointsX.get(i)+";";
        for(int i=0;i<pointsY.size();i++)ret+=pointsY.get(i)+";";
        return ret;
    }

    @Override
    public void adjustToPoints(int[] x, int[] y) {
        pointsX = new ArrayList<>();
        pointsY = new ArrayList<>();

        if(x.length==y.length){
            for(int i=0;i<x.length;i++){
                pointsX.add(x[i]);
                pointsY.add(y[i]);
            }
        }
    }

    @Override
    public void moveTo(int x, int y) {
        int moveByX = x-pointsX.get(0);
        int moveByY = y-pointsY.get(0);

        if(pointsX.size()==pointsY.size()){
            for(int i=0;i<pointsX.size();i++){
                pointsX.set(i, pointsX.get(i)+moveByX);
                pointsY.set(i, pointsY.get(i)+moveByY);
            }
        }
    }

    @Override
    public boolean isIn(int x, int y) {
        int intersecions = 0;
        double a = x==0?1:((double) y/x);
        double b = x==0?y:0;
        //System.out.println("Nasza funkcja: y = "+a+"*x + "+b);

        if(pointsX.size()==pointsY.size()) {
            for (int i = 0; i < pointsX.size(); i++) {
                double firstX = pointsX.get(i);
                double firstY = pointsY.get(i);
                double secondX = i==pointsX.size()-1?pointsX.get(0):pointsX.get(i+1);
                double secondY = i==pointsY.size()-1?pointsY.get(0):pointsY.get(i+1);

                if(firstX==secondX){
                    if(a*firstX+b>=Math.min(firstY, secondY) && a*firstX+b<=Math.max(firstY, secondY) && x<=firstX)intersecions++;
                    //System.out.println("bok pionowy");
                }
                else{
                    double newA = (firstY-secondY)/(firstX-secondX);
                    double newB = firstY-(newA*firstX);

                    //System.out.println("Druga funkcja: y = "+newA+"*x + "+newB);

                    if(a==newA){
                        if(b==newB && x<=Math.max(firstX, secondX))intersecions++;
                    }
                    else{
                        double newX = (newB-b)/(a-newA);
                        //System.out.println("Punkt przecięcia: "+newX);
                        if(x<=newX && a*newX+b>=Math.min(firstY, secondY) && a*newX+b<=Math.max(firstY, secondY))intersecions++;
                    }
                }

            }
        }

        //System.out.println("checking: "+intersecions);
        return intersecions%2==1;
    }

}
