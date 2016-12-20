import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_SHIFT;

/**
 * Created by Sergiusz on 25.10.2016.
 */
public class Controller {
    private GUI gui;
    private Canvas canvas;
    private ActionMode actionMode;
    private Color color;
    private int startX;
    private int startY;
    private boolean drawingPolygon;
    private ArrayList<Integer> polygonTempX;
    private ArrayList<Integer> polygonTempY;
    private boolean blockClick;
    private boolean shiftPressed;
    private boolean editing;
    private Segment movingSegment;
    private String currentFileName;

    public Controller(GUI gui, Canvas canvas) {
        this.gui = gui;
        this.canvas = canvas;
        polygonTempX = new ArrayList<>();
        polygonTempY = new ArrayList<>();
        blockClick = false;
        shiftPressed = false;
        editing = false;
        movingSegment = null;
        currentFileName = null;

        gui.addEllipseButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.activateButton(0);
                actionMode = ActionMode.DRAW_ELLIPSE;
                resetActions();
            }
        });

        gui.addRectangleButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.activateButton(1);
                actionMode = ActionMode.DRAW_RECTANGLE;
                resetActions();
            }
        });
        gui.addPolygonButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.activateButton(2);
                actionMode = ActionMode.DRAW_POLYGON;
                resetActions();
            }
        });

        gui.addMoveButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.activateButton(3);
                actionMode = ActionMode.MOVE;
                resetActions();
            }
        });

        gui.addColorChangeListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int c = gui.getSelectedColor();
                if (c == 0) color = new Color(255, 0, 0, 127);
                else if (c == 1) color = new Color(0, 255, 0, 127);
                else if (c == 2) color = new Color(0, 0, 255, 127);
                else if (c == 3) color = new Color(255, 255, 0, 127);
                else if (c == 4) color = new Color(127, 127, 127, 127);
                gui.setColor(color);
                resetActions();
            }
        });

        gui.addOpenFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int val = chooser.showOpenDialog(null);
                if(val==JFileChooser.APPROVE_OPTION){
                    File imageFile = chooser.getSelectedFile();
                    String path =  imageFile.getAbsolutePath();
                    String name = path.substring(0, path.lastIndexOf('.'));

                    File segmentFile = new File(name);
                    currentFileName = name;
                    if(segmentFile.exists() && !segmentFile.isDirectory()){
                        System.out.println("fire");
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(segmentFile));
                            String line = br.readLine();

                            while(line!=null){
                                canvas.addSegment(Segment.deserialize(line));
                                line = br.readLine();
                            }
                            br.close();
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else canvas.setSegments(new ArrayList<>());

                    gui.loadImage(imageFile);
                    resetActions();
                    refreshCanvas();
                    refreshTable();
                }
            }
        });

        gui.addSaveFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*JFileChooser chooser = new JFileChooser();
                int val = chooser.showSaveDialog(null);
                if(val==JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                        Iterator<Segment> it = canvas.getSegments().iterator();
                        while(it.hasNext()){
                            Segment s = it.next();
                            bw.write(s.serialize());
                            bw.newLine();
                        }
                        bw.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                refreshCanvas();
                refreshTable();*/
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(currentFileName)));
                    Iterator<Segment> it = canvas.getSegments().iterator();
                    while(it.hasNext()){
                        Segment s = it.next();
                        bw.write(s.serialize());
                        bw.newLine();
                    }
                    bw.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                refreshCanvas();
                refreshTable();
            }
        });

        /* gui.addLoadFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int val = chooser.showOpenDialog(null);
                if(val==JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line = br.readLine();

                        while(line!=null){
                            canvas.addSegment(Segment.deserialize(line));
                            line = br.readLine();
                        }
                        br.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                refreshCanvas();
                refreshTable();
            }
        });*/

        gui.addCanvasMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(actionMode==ActionMode.DRAW_POLYGON && e.getClickCount() == 2 && polygonTempX.size()>2 && polygonTempY.size()>2){
                    Segment s = new SegmentPolygon(toArray(polygonTempX), toArray(polygonTempY));
                    canvas.addSegment(s);
                    canvas.setTempSegment(null);
                    refreshTable();
                    drawingPolygon = false;
                    polygonTempX = new ArrayList<>();
                    polygonTempY = new ArrayList<>();
                    blockClick = true;
                    refreshCanvas();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int newX = e.getX();
                int newY = e.getY();
                gui.focusCanvas();

                if(shiftPressed && getSelectedRow()!=-1) {
                    Segment s = canvas.getSegment(getSelectedRow());

                    if(!drawingPolygon){
                        startX = newX;
                        startY = newY;
                    }
                    if (s instanceof SegmentPolygon) {
                        SegmentPolygon polygon = (SegmentPolygon) s;
                        if(!polygon.hasPoint(newX, newY))polygon.addPoint(newX, newY);
                        refreshTable();
                        refreshCanvas();
                    }
                }
                else if(actionMode==ActionMode.MOVE){
                    //tutaj startX i startY wyjatkowo nie oznaczaja pozycji poczatkowej tylko pozycje
                    //kursora w stosunku do punktu poczatkowego segmentu

                    selectSegmentAt(newX, newY);
                    if(getSelectedRow()!=-1) {
                        Segment s = canvas.getSegment(getSelectedRow());
                        if (s instanceof SegmentEllipse) {
                            SegmentEllipse ellipse = (SegmentEllipse) s;
                            startX = newX - ellipse.getStartX();
                            startY = newY - ellipse.getStartY();
                        } else if (s instanceof SegmentRectangle) {
                            SegmentRectangle rectangle = (SegmentRectangle) s;
                            startX = newX - rectangle.getStartX();
                            startY = newY - rectangle.getStartY();
                        } else if (s instanceof SegmentPolygon) {
                            SegmentPolygon polygon = (SegmentPolygon) s;
                            startX = newX - polygon.getFirstX();
                            startY = newY - polygon.getFirstY();
                        }
                    }
                }
                else if(!shiftPressed){
                    if (actionMode == ActionMode.DRAW_POLYGON && !blockClick) drawingPolygon = true;

                    if (drawingPolygon) {
                        if (!pointExists(newX, newY)) {
                            polygonTempX.add(newX);
                            polygonTempY.add(newY);
                        }
                    } else {
                        startX = newX;
                        startY = newY;
                        selectSegmentAt(newX, newY);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int newX = e.getX();
                int newY = e.getY();
                editing = false;

                if(actionMode==ActionMode.DRAW_ELLIPSE && !shiftPressed){
                    if(Math.abs(newX - startX)/2>0 && Math.abs(newY - startY)/2>0) {
                        Segment s = Segment.fromPoints(SegmentType.ELLIPSE, new int[]{startX, newX}, new int[]{startY, newY});
                        canvas.addSegment(s);
                        canvas.setTempSegment(null);
                        refreshTable();
                    }
                }
                else if(actionMode==ActionMode.DRAW_RECTANGLE && !shiftPressed){

                    if(Math.abs(newX - startX)/2>0 && Math.abs(newY - startY)/2>0) {
                        Segment s = Segment.fromPoints(SegmentType.RECTANGLE, new int[]{startX, newX}, new int[]{startY, newY});
                        canvas.addSegment(s);
                        canvas.setTempSegment(null);
                        refreshTable();
                    }
                }
                refreshCanvas();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
                gui.setCoordinatesLabel("[0;0]");
            }
        });
        gui.addCanvasMouseMoveListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gui.setCoordinatesLabel("["+e.getX()+";"+e.getY()+"]");
                int newX = e.getX();
                int newY = e.getY();

                if(shiftPressed && getSelectedRow()!=-1){
                    Segment s = canvas.getSegment(getSelectedRow());
                    if (s instanceof SegmentEllipse) {
                        SegmentEllipse ellipse = (SegmentEllipse) s;
                        if(!editing){
                            editing = true;
                            startX = ellipse.getStartX();
                            startY = ellipse.getStartY();
                        }
                        s.adjustToPoints(new int[]{startX, newX}, new int[]{startY, newY});
                     }
                    else if (s instanceof SegmentRectangle) {
                        SegmentRectangle rectangle = (SegmentRectangle) s;
                        if(!editing){
                            editing = true;
                            startX = rectangle.getStartX();
                            startY = rectangle.getStartY();
                        }
                        s.adjustToPoints(new int[]{startX, newX}, new int[]{startY, newY});
                     }
                    refreshTable();
                }
                else if(actionMode==ActionMode.MOVE && getSelectedRow()!=-1){
                    Segment s = canvas.getSegment(getSelectedRow());
                    int x = newX-startX;
                    int y = newY-startY;
                    s.moveTo(x, y);
                    refreshTable();
                }
                else if(!shiftPressed){
                    if (actionMode == ActionMode.DRAW_ELLIPSE) {
                        Segment ellipse = Segment.fromPoints(SegmentType.ELLIPSE, new int[]{startX, newX}, new int[]{startY, newY});
                        canvas.setTempSegment(ellipse);
                    } else if (actionMode == ActionMode.DRAW_RECTANGLE) {
                        Segment rectangle = Segment.fromPoints(SegmentType.RECTANGLE, new int[]{startX, newX}, new int[]{startY, newY});
                        canvas.setTempSegment(rectangle);
                    }
                }
                refreshCanvas();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                gui.setCoordinatesLabel("["+e.getX()+";"+e.getY()+"]");
                if(actionMode==ActionMode.DRAW_POLYGON) {
                    SegmentPolygon polygon = new SegmentPolygon(toArray(polygonTempX), toArray(polygonTempY));
                    polygon.addPoint(e.getX(), e.getY());
                    canvas.setTempSegment(polygon);
                    blockClick = false;
                }
                refreshCanvas();
            }
        });

        gui.addFrameMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {


            }

            @Override
            public void mousePressed(MouseEvent e) {
                resetActions();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        gui.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_DELETE){
                    System.out.println("fire");
                    if(getSelectedRow()!=-1) {
                        int[] rows = gui.getTable().getSelectedRows();

                        for (int i = rows.length - 1; i >= 0; i--) {
                            canvas.removeSegment(rows[i]);
                        }
                        refreshTable();
                        refreshCanvas();
                    }
                }
                else if(e.getKeyCode()==KeyEvent.VK_SHIFT)shiftPressed = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SHIFT)shiftPressed = false;
            }
        });
    }



    private int getSelectedRow(){
        if(gui.getTable().getSelectedRowCount()>1)return -1;
        return gui.getTable().getSelectedRow();
    }
    public static void main(String[] args){
        Controller c = new Controller(new GUI(), new Canvas());
        c.start();
    }

    public void start(){
        gui.setVisible(true);
    }

    private void refreshCanvas(){
        try {
            ArrayList<Segment> segments = canvas.getSegments();

            Iterator<Segment> it = segments.iterator();
            gui.prepareCanvas();

            while (it.hasNext()) {
                Segment s = it.next();

                if (s instanceof SegmentEllipse) {
                    SegmentEllipse ellipse = (SegmentEllipse) s;
                    gui.addShape(segmentToShape(ellipse));
                }
                else if (s instanceof SegmentRectangle) {
                    SegmentRectangle rectangle = (SegmentRectangle) s;
                    gui.addShape(segmentToShape(rectangle));
                }
                else if (s instanceof SegmentPolygon) {
                    SegmentPolygon polygon = (SegmentPolygon) s;
                    gui.addShape(segmentToShape(polygon));
                }
            }

            Segment tempSegment = canvas.getTempSegment();

                if (tempSegment instanceof SegmentEllipse) {
                    SegmentEllipse ellipse = (SegmentEllipse) tempSegment;
                    gui.setTempShape(segmentToShape(ellipse));
                }
                else if (tempSegment instanceof SegmentRectangle) {
                    SegmentRectangle rectangle = (SegmentRectangle) tempSegment;
                    gui.setTempShape(segmentToShape(rectangle));
                }
                else if (tempSegment instanceof SegmentPolygon) {
                    SegmentPolygon polygon = (SegmentPolygon) tempSegment;
                    gui.setTempShape(segmentToShape(polygon));
                }
                else gui.setTempShape(null);

        }catch(Exception e){
        }

        gui.repaint();
    }

    private int[] toArray(ArrayList<Integer> list){
        int[] ret = new int[list.size()];

        Iterator<Integer> it = list.iterator();
        int i = 0;
        while(it.hasNext()){
            ret[i] = it.next();
            i++;
        }
        return ret;
    }

    private void addTableEntry(String[] s){
        gui.addTableRow(s);
    }

    private String[] getTableEntry(Segment s){
        String id = "0";
        String type = null;
        String details = null;

        if(s.getType()==SegmentType.ELLIPSE){
            SegmentEllipse ellipse = (SegmentEllipse) s;
            type = "okrąg";
            details = "p = ["+(ellipse.getStartX()+ellipse.getWidth()/2)+";"+(ellipse.getStartY()+ellipse.getHeight()/2)+"], r1 = "+ellipse.getWidth()/2+", r2 = "+ellipse.getHeight()/2;
        }
        else if(s.getType()==SegmentType.RECTANGLE){
            SegmentRectangle rectangle = (SegmentRectangle) s;
            type = "prostokąt";
            details = "p1 = ["+rectangle.getStartX()+";"+rectangle.getStartY()+"], p2 = ["+(rectangle.getStartX()+rectangle.getWidth())+";"+(rectangle.getStartY()+rectangle.getHeight())+"]";
        }
        else if(s.getType()==SegmentType.POLYGON){
            SegmentPolygon polygon = (SegmentPolygon) s;
            int[] x = polygon.getXArray();
            int[] y = polygon.getYArray();

            type = "wielokąt";
            StringBuilder str = new StringBuilder("{");

            for(int i=0;i<x.length;i++){
                str.append("["+x[i]+";"+y[i]+"]");
                if(i!=x.length-1)str.append(", ");
            }
            str.append("}");
            details = str.toString();
        }

        return new String[]{id, type, details};
    }

    private boolean pointExists(int x, int y){
        for(int i=0;i<polygonTempX.size();i++){
            if(polygonTempX.get(i)==x && polygonTempY.get(i)==y)return true;
        }
        return false;
    }

    private void refreshTable(){
        JTable tab = gui.getTable();
        DefaultTableModel model = (DefaultTableModel)tab.getModel();
        int[] rows = tab.getSelectedRows();
        int before = tab.getRowCount();

        for(int i=tab.getRowCount()-1;i>=0;i--)model.removeRow(i);

        Iterator<Segment> it = canvas.getSegments().iterator();

        int after = canvas.getSegments().size();

        while(it.hasNext()){
            addTableEntry(getTableEntry(it.next()));
        }

        if(before==after){
            for(int i=0;i<rows.length;i++)tab.getSelectionModel().addSelectionInterval(rows[i], rows[i]);
        }
        gui.renumberTable();
    }

    private Shape segmentToShape(Segment s){
        Shape ret = null;
        if (s instanceof SegmentEllipse) {
            SegmentEllipse ellipse = (SegmentEllipse) s;
            ret = new Ellipse2D.Float(ellipse.getStartX(), ellipse.getStartY(), ellipse.getWidth(), ellipse.getHeight());
        }
        else if (s instanceof SegmentRectangle) {
            SegmentRectangle rectangle = (SegmentRectangle) s;
            ret = new Rectangle(rectangle.getStartX(), rectangle.getStartY(), rectangle.getWidth(), rectangle.getHeight());
        }
        else if (s instanceof SegmentPolygon) {
            SegmentPolygon polygon = (SegmentPolygon) s;
            ret = new Polygon(polygon.getXArray(), polygon.getYArray(), polygon.getXArray().length);
        }

        return ret;
    }

    private void resetActions(){
        polygonTempX = new ArrayList<>();
        polygonTempY = new ArrayList<>();
        blockClick = false;
        shiftPressed = false;
        editing = false;
        movingSegment = null;
        drawingPolygon = false;
        canvas.setTempSegment(null);
        refreshCanvas();
    }

    private void selectSegmentAt(int x, int y){
        Iterator<Segment> it = canvas.getSegments().iterator();

        if(!it.hasNext())return;

        int count = 0;
        int row = -1;
        while(it.hasNext()){
            Segment s = it.next();
            if(s.isIn(x, y)){
                row = count;
            }
            count++;
        }

        if(row==-1)gui.getTable().removeRowSelectionInterval(0, gui.getTable().getRowCount()-1);
        else gui.getTable().setRowSelectionInterval(row, row);
    }
}
