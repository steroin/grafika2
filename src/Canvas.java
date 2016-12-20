import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Sergiusz on 21.10.2016.
 */
public class Canvas {
    private ArrayList<Segment> segments;
    private Segment tempSegment;

    public Canvas(){
        segments = new ArrayList<>();
        tempSegment = null;
    }

    public void addSegment(Segment segment){
        segments.add(segment);
    }
    public void removeSegment(int index){
        segments.remove(index);
    }

    public ArrayList<Segment> getSegments(){
        return segments;
    }

    public Segment getSegment(int index){
        return segments.get(index);
    }

    public Segment getTempSegment(){
        return tempSegment;
    }

    public void setSegments(ArrayList<Segment> list){
        segments = list;
    }

    public void setTempSegment(Segment segment){
        tempSegment = segment;
    }
}
