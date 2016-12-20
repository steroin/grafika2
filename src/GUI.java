import com.sun.deploy.panel.JSmartTextArea;
import com.sun.deploy.panel.JreTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Sergiusz on 23.10.2016.
 */
public class GUI extends JFrame{

    private JPanel main = new JPanel(new GridBagLayout());
    private JPanel canvasContainer;
    private JScrollPane canvasScrollPane;
    private PaintCanvas canvas;
    private JMenuBar menu;
    private JMenu file;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem close;
    private JToolBar toolbar;
    private JPanel toolbarPane = new JPanel(new GridBagLayout());
    private JButton ellipseBut;
    private JButton rectangleBut;
    private JButton polygonBut;
    private JButton moveBut;
    private JComboBox colorList;
    private JLabel imageContainer = new JLabel();
    private Image img = null;
    private String coordinates = "X,Y: ";
    private JLabel coordLabel;
    private String mode = "Tryb: ";
    private JLabel modeLabel = new JLabel(mode);
    private JTable table;
    private JScrollPane tablePane;

    public GUI(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1024, 768);

        toolbar = createToolbar();
        menu = createMenu();

        createCanvas();

        table = createTable();
        coordLabel = createCoordinatesLabel();

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(main, BorderLayout.NORTH);
    }

    public void addShape(Shape s){
        canvas.addShape(s);
    }

    public void addTableRow(String[] tab){
        ((DefaultTableModel)table.getModel()).addRow(tab);
        renumberTable();
    }

    public void removeTableRow(int row){
        ((DefaultTableModel)table.getModel()).removeRow(row);
    }
    public void renumberTable(){
        DefaultTableModel model = ((DefaultTableModel)table.getModel());
        int rows = model.getRowCount();

        for(int i=0;i<rows;i++){
            model.setValueAt((i+1)+"", i, 0);
        }
    }

    public void setCoordinatesLabel(String label){
        coordLabel.setText(coordinates+label);
    }

    public void repaint(){
        canvas.repaint();
    }

    public Image getImage(){
        return img;
    }

    public int getSelectedColor(){
        return colorList.getSelectedIndex();
    }

    public void setColor(Color color){
        canvas.setColor(color);
    }

    public void loadImage(String path){
        loadImage(new File(path));
    }

    public void loadImage(File file){
        try {
            img = ImageIO.read(file);
        }
        catch(Exception e){}

        int maxWidth = (int) canvasScrollPane.getMaximumSize().getWidth();
        int maxHeight = (int) canvasScrollPane.getMaximumSize().getHeight();
        int imgWidth = img.getWidth(this)+3;
        int imgHeight = img.getHeight(this)+3;
        int width = imgWidth>maxWidth?maxWidth:imgWidth;
        int height = imgHeight>maxHeight?maxHeight:imgHeight;
        canvasScrollPane.setPreferredSize(new Dimension(width, height));
        canvas.setImage(img);
        canvasScrollPane.setVisible(true);
        canvasScrollPane.revalidate();
        canvasScrollPane.repaint();

        canvasContainer.revalidate();
        canvasContainer.repaint();
    }

    public void addEllipseButtonListener(ActionListener l){
        ellipseBut.addActionListener(l);
    }

    public void addRectangleButtonListener(ActionListener l){
        rectangleBut.addActionListener(l);
    }

    public void addPolygonButtonListener(ActionListener l){
        polygonBut.addActionListener(l);
    }
    public void addMoveButtonListener(ActionListener l){
        moveBut.addActionListener(l);
    }

    public void addColorChangeListener(ActionListener l){
        colorList.addActionListener(l);
    }

    public void addCanvasMouseListener(MouseListener l){
        canvas.addMouseListener(l);
    }

    public void addCanvasMouseMoveListener(MouseMotionListener l){
        canvas.addMouseMotionListener(l);
    }

    public void addFrameMouseListener(MouseListener l){
        main.addMouseListener(l);
    }

    public void addOpenFileListener(ActionListener l){
        open.addActionListener(l);
    }

    public void addSaveFileListener(ActionListener l){
        save.addActionListener(l);
    }

    public void addKeyListener(KeyListener l){
        canvas.setFocusable(true);
        canvas.addKeyListener(l);
        table.addKeyListener(l);
    }


    public void setTempShape(Shape shape){
        canvas.setTempShape(shape);
    }

    public JTable getTable(){
        return table;
    }

    public void prepareCanvas(){
        canvas.setShapes(new ArrayList<>());
        canvas.setTempShape(null);
    }

    public void focusCanvas(){
        canvas.requestFocus();
    }

    private JButton createButton(String name, ImageIcon icon){
        JButton but = new JButton(name);
        but.setIcon(icon);
        but.setMargin(new Insets(0, 0, 0, 0));
        Font font = but.getFont();
        but.setFont(new Font(font.getName(), font.getStyle(), 12));
        but.setVerticalTextPosition(SwingConstants.BOTTOM);
        but.setHorizontalTextPosition(SwingConstants.CENTER);
        but.setFocusPainted(false);
        but.setPreferredSize(new Dimension(80, 60));
        but.setOpaque(false);
        but.setContentAreaFilled(false);
        but.setBorderPainted(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 4, 0, 4);
        toolbarPane.add(but, c);
        return but;
    }

    private JComboBox createColorPicker() throws IOException {
        JComboBox colorPicker = new JComboBox();
        String[] colors = new String[]{
                "Czerwony",
                "Zielony",
                "Niebieski",
                "Żółty",
                "Szary"};
        ImageIcon[] icons = new ImageIcon[]{
                    new ImageIcon(ImageIO.read(getClass().getResource("red.png"))),
                new ImageIcon(ImageIO.read(getClass().getResource("green.png"))),
                new ImageIcon(ImageIO.read(getClass().getResource("blue.png"))),
                new ImageIcon(ImageIO.read(getClass().getResource("yellow.png"))),
                new ImageIcon(ImageIO.read(getClass().getResource("gray.png")))
            };

            for(int i=0;i<colors.length;i++)colorPicker.addItem(i);
        class MyRenderer extends DefaultListCellRenderer  {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                int selectedIndex = (int) value;
                label.setText(colors[selectedIndex]);
                label.setIcon(icons[selectedIndex]);
                return label;
            }
        }
        colorPicker.setRenderer(new MyRenderer());
        colorPicker.setPreferredSize(new Dimension(180, 60));
        toolbarPane.add(colorPicker);
        return colorPicker;
    }

    private JMenuBar createMenu(){
        JMenuBar menu = new JMenuBar();
        addMenus(menu);

        menu.setMinimumSize(new Dimension((int) menu.getMinimumSize().getWidth(), 24));
        GridBagConstraints cMenu = new GridBagConstraints();
        cMenu.fill = GridBagConstraints.BOTH;
        cMenu.weightx = 1.0;
        cMenu.gridx = 0;
        cMenu.gridy = 0;
        cMenu.gridwidth = 2;
        main.add(menu, cMenu);
        return menu;
    }

    private void addMenus(JMenuBar menuBar){
        file = new JMenu("Plik");
        open = new JMenuItem("Otwórz");
        save = new JMenuItem("Zapisz");
        close = new JMenuItem("Zamknij");

        file.add(open);
        file.add(save);
        file.add(close);
        menuBar.add(file);
    }

    private JToolBar createToolbar(){
        toolbarPane = new JPanel();
        JToolBar toolbar = new JToolBar();

        try {
            ellipseBut = createButton("Okrąg", new ImageIcon(ImageIO.read(getClass().getResource("ellipse.png"))));
            rectangleBut = createButton("Prostokąt", new ImageIcon(ImageIO.read(getClass().getResource("rectangle.png"))));
            polygonBut = createButton("Wielokąt", new ImageIcon(ImageIO.read(getClass().getResource("polygon.png"))));
            moveBut = createButton("Zaznaczenie", new ImageIcon(ImageIO.read(getClass().getResource("arrows.png"))));
            colorList = createColorPicker();
        } catch (IOException e) {
            e.printStackTrace();
        }

        toolbar.setFloatable(false);
        toolbar.setLayout(new BorderLayout());
        toolbar.add(toolbarPane, BorderLayout.NORTH);

        toolbar.setPreferredSize(new Dimension(toolbar.getPreferredSize().width, 68));
        GridBagConstraints cToolbar = new GridBagConstraints();
        cToolbar.fill = GridBagConstraints.BOTH;
        cToolbar.weightx = 1.0;
        cToolbar.gridx = 0;
        cToolbar.gridy = 1;
        cToolbar.gridwidth = 2;
        main.add(toolbar, cToolbar);
        return toolbar;
    }

    private void createCanvas(){
        canvasContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        canvasScrollPane = new JScrollPane();
        canvas = new PaintCanvas();

        canvasScrollPane = new JScrollPane();
        canvasScrollPane.setMaximumSize(new Dimension(990, 550));

        canvasContainer.setMinimumSize(new Dimension(1000, 560));
        canvasContainer.setPreferredSize(new Dimension(1000, 560));

        canvasScrollPane.setViewportView(canvas);

        canvasContainer.add(canvasScrollPane);

        canvasContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));

        GridBagConstraints cCanvas = new GridBagConstraints();
        cCanvas.fill = GridBagConstraints.BOTH;
        cCanvas.gridx = 0;
        cCanvas.gridy = 2;
        canvasScrollPane.setVisible(false);
        main.add(canvasContainer, cCanvas);
    }

    private JTable createTable(){
        JTable table = new JTable(new DefaultTableModel(new Object[]{"ID", "Typ", "Parametry"}, 0)){
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };

        table.setFillsViewportHeight(true);
        tablePane = new JScrollPane(table);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);

        tablePane.setMinimumSize(new Dimension(200, 560));
        tablePane.setPreferredSize(new Dimension(200, 560));
        GridBagConstraints cTablePane = new GridBagConstraints();
        cTablePane.fill = GridBagConstraints.BOTH;
        cTablePane.gridx = 1;
        cTablePane.gridy = 2;
        main.add(tablePane, cTablePane);
        return table;
    }

    private JLabel createCoordinatesLabel(){
        JPanel wrapper = new JPanel(new BorderLayout());
        JLabel coordLabel = new JLabel(coordinates);
        wrapper.setMinimumSize(new Dimension(200, 20));
        wrapper.setPreferredSize(new Dimension(200, 20));

        GridBagConstraints cCoordLabel = new GridBagConstraints();
        cCoordLabel.gridx = 0;
        cCoordLabel.gridy = 3;
        cCoordLabel.anchor = GridBagConstraints.WEST;

        wrapper.add(coordLabel, BorderLayout.WEST);
        main.add(wrapper, cCoordLabel);
        return coordLabel;
    }

    public void activateButton(int button){
        JButton[] but = new JButton[]{ellipseBut, rectangleBut, polygonBut, moveBut};
        boolean activated = false;
        for(int i=0;i<but.length;i++){
            if(i==button)activated = true;
            else activated = false;

            but[i].setOpaque(activated);
            but[i].setContentAreaFilled(activated);
            but[i].setBorderPainted(activated);
        }
    }
}
