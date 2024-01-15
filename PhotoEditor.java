//package photoEditor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PhotoEditor extends JFrame {
        private BufferedImage originalImage;
        private BufferedImage editedImage;
        private ArrayList<BufferedImage> adjustedImages=new ArrayList<>();
        private BufferedImage zoomImage;
        private JPanel drawingPanel;
        private JPanel boxPanel;
        private Rectangle boundingBox;
        private int coordinateX,coordinateY; 
        private JLabel imageLabel;
       
        public PhotoEditor(){
          setTitle("Photo Editor App (Paskil Addition)");
          Image desktopIcon=Toolkit.getDefaultToolkit().getImage("paskilDesktop.png");
          setIconImage(desktopIcon);
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          setSize(800,600);
          
          setLocationRelativeTo(null);
          
          createMenuBar();
          
          imageLabel=new JLabel(new ImageIcon("paskil.png"));
          add(imageLabel,BorderLayout.CENTER);
          
          JButton brigthnessAdjustmentButton = buttonAlignment("Brigthness Adjustment");
          brigthnessAdjustmentButton.addActionListener(e -> {
            if(originalImage!=null){
                if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                    JFrame frame=new JFrame("Brightness Intensity");
                    JPanel panel=new JPanel();
                    JLabel label= new JLabel();
                    JSlider slider=new JSlider(0,50,10);
                    JButton button= new JButton("Ok");

                    slider.setPaintTicks(true);
                    slider.setMajorTickSpacing(49);
                    slider.setPaintTicks(true);

                    slider.setFont(new Font("Arial",Font.PLAIN,15));
                    label.setFont(new Font("Arial",Font.PLAIN,25));
                    label.setText("Scale factor value= "+(double)(slider.getValue()/10));

                    panel.add(slider);
                    panel.add(label);
                    frame.add(panel);
                    frame.setSize(400,150);
                    frame.setVisible(true);
                    frame.add(button, BorderLayout.SOUTH);

                    slider.addChangeListener(new ChangeListener(){ 
                       @Override
                       public void stateChanged(ChangeEvent e){
                       if(!slider.getValueIsAdjusting()){
                          double factor= slider.getValue()/10.0;
                          String factorInput = Double.toString(factor);
                          label.setText("Scale factor value= "+ (double)(slider.getValue()/10.0));
                          double scaleFactor = Double.parseDouble(factorInput); 
                          adjustBrightnessEffect(scaleFactor);
                          }}});

                        button.addActionListener(new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e){
                                frame.dispose();
                            }
                        });
                }
                else{
                    JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                    
                }
            }
          });
          
          JButton blurButton = buttonAlignment("Blur");
          blurButton.addActionListener(e ->{
            if(originalImage!=null){
                if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                    JFrame frame=new JFrame("Blur Intensity");
                    JPanel panel=new JPanel();
                    JLabel label= new JLabel();
                    JSlider slider=new JSlider(0,50,10);
                    JButton button= new JButton("Ok");

                    slider.setPaintTicks(true);
                    slider.setMajorTickSpacing(49);
                    slider.setPaintTicks(true);

                    slider.setFont(new Font("Arial",Font.PLAIN,15));
                    label.setFont(new Font("Arial",Font.PLAIN,25));
                    label.setText("Blur intensity value= "+(double)(slider.getValue()/10));

                    panel.add(slider);
                    panel.add(label);
                    frame.add(panel);
                    frame.setSize(400,150);
                    frame.setVisible(true);
                    frame.add(button, BorderLayout.SOUTH);

                    slider.addChangeListener(new ChangeListener(){ 
                      @Override
                      public void stateChanged(ChangeEvent e){
                      if(!slider.getValueIsAdjusting()){
                        double intensity= slider.getValue()/10.0;
                        String intensityInput = Double.toString(intensity);
                        label.setText("Scale value= "+ (double)(slider.getValue()/10.0));
                        double blurIntensity = Double.parseDouble(intensityInput);
                        blurEffect(blurIntensity);
                        }}});

                        button.addActionListener(new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e){
                                frame.dispose();
                            }
                        });
                }
                else{
                    JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                                        
                }
            }
          });
          
          JButton monochromeButton= buttonAlignment("Monochrome");
          monochromeButton.addActionListener(e ->{
            if(originalImage!=null){
                if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                   monochromeFilter();   
                }
                else{
                    JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                      
                }
            }
          });
          
          JButton cropButton =buttonAlignment ("Crop");
          cropButton.addActionListener(e ->{
            if(originalImage!=null){
                if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                   String cropInputX =JOptionPane.showInputDialog(this,"Enter the horizontal value (Original horizontal value:"+editedImage.getWidth()+")");
                    String cropInputY=JOptionPane.showInputDialog(this,"Enter the vertical value (Original vertical value:"+editedImage.getHeight()+")");

                    try{
                        int x=Integer.parseInt(cropInputX);
                        int y=Integer.parseInt(cropInputY);
                        cropImageEffect(x,y);
                    }
                    catch(NumberFormatException ex){
                      JOptionPane.showMessageDialog(this,"Invalid input. Please enter a valid numeric value","Eror",JOptionPane.ERROR_MESSAGE);  
                    }   
                }
                else{
                    JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                      
                }
            }
          });
          
          JButton resizeButton=buttonAlignment("Resize");
          resizeButton.addActionListener(e ->{
            if(originalImage!=null){
                if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                   String resizeInputX =JOptionPane.showInputDialog(this,"Enter the horizontal value (Original horizontal value:"+editedImage.getWidth()+")");
                   String resizeInputY=JOptionPane.showInputDialog(this,"Enter the vertical value (Original vertical value:"+editedImage.getHeight()+")");
              
                    try{
                        int x=Integer.parseInt(resizeInputX);
                        int y=Integer.parseInt(resizeInputY);
                        resizeImageEffect(x,y);
                    }
                    catch(NumberFormatException ex){
                      JOptionPane.showMessageDialog(this,"Invalid input. Please enter a valid numeric value","Error",JOptionPane.ERROR_MESSAGE);  
                    }  
                      }
                else{
                    JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                      
                }
            }
          });
          
          JButton saveButton=buttonAlignment("Save");
          saveButton.addActionListener(e->{
              if(originalImage!=null){
                saveImage();                  
              }
          });
          
          JButton undoButton=buttonAlignment("Undo");
          undoButton.addActionListener(e->{
              if(originalImage!=null){
                undoImage();   
              }
          });
          
          JButton revertButton=buttonAlignment("Revert");
          revertButton.addActionListener(e ->{
              if(originalImage!=null){
                  revertImage();                  
              }
           });
          
          JPanel buttonPanel=new JPanel();
          buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
          
          buttonPanel.add(Box.createVerticalStrut(130));
          addCenterAlignedButton(buttonPanel,brigthnessAdjustmentButton );
          addCenterAlignedButton(buttonPanel,blurButton );
          addCenterAlignedButton(buttonPanel,monochromeButton );
          addCenterAlignedButton(buttonPanel,cropButton );
          addCenterAlignedButton(buttonPanel,resizeButton );
          addCenterAlignedButton(buttonPanel,saveButton);
          addCenterAlignedButton(buttonPanel,undoButton);
          addCenterAlignedButton(buttonPanel,revertButton); 
          
          add(buttonPanel,BorderLayout.EAST);
          
          setVisible(true);
          
        }
        
        private void addCenterAlignedButton(JPanel panel,JButton button){
           button.setAlignmentX(CENTER_ALIGNMENT); 
           panel.add(Box.createVerticalStrut(10));
           panel.add(button);
        }
        
        private JButton buttonAlignment(String text){
            JButton button= new JButton(text);
            button.setBorderPainted(true);
            button.setFocusPainted(true);
            button.setContentAreaFilled(true);
            button.setMargin(new Insets(5,10,5,10));
            return button;
        }
        
        
        private void createMenuBar(){
            JMenuBar menuBar= new JMenuBar();
            JMenu fileMenu= new JMenu("File");
            JMenu toolsMenu=new JMenu("Tools");
            JMenu filtersMenu=new JMenu("Filters");
            JMenu boundsMenu=new JMenu("Bound");
            
            JMenuItem importItem=new JMenuItem("Import");
            importItem.addActionListener(e -> importImage());
            
            JMenuItem exportItem=new JMenuItem("Export");
            exportItem.addActionListener(e ->{
                if(originalImage!=null){
                    exportImage();                    
                }
            });
            
            JMenuItem drawTool=new JMenuItem("Draw");
            drawTool.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                    String colourInput=JOptionPane.showInputDialog(this,"Enter the colour of the line to be drawn (Available colours-> Black, White, Red, Green, Blue, Yellow, Pink)");
                    String sizeInput=JOptionPane.showInputDialog(this,"Enter the size of the line to be drawn (Available size- 1, 2, 3, 4, 5)");
                    float lineThickness=0;
                    Color colourSelected=null;

                    try{
                        colourSelected=(Color)Color.class.getField(colourInput.toUpperCase()).get(null);
                    }catch(Exception ex){
                        ex.printStackTrace(); 
                        JOptionPane.showMessageDialog(this, "The colour entered is not available","Error",JOptionPane.ERROR_MESSAGE);
                    }

                    try{
                        switch (sizeInput) {
                            case "1":
                                lineThickness=1;
                                break;
                            case "2":
                                lineThickness=2;
                                break;
                            case "3":
                                lineThickness=3;
                                break;
                            case "4":
                                lineThickness=4;
                                break;
                            case "5":
                                lineThickness=5;
                                break;                        
                            default:
                                JOptionPane.showMessageDialog(this, "The size entered is not available","Error",JOptionPane.ERROR_MESSAGE);
                                break;
                        }
                    }
                    catch(Exception ex){
                        ex.printStackTrace();                    
                    }

                    if(lineThickness!=0 && colourSelected!=null){
                        new Draw(colourSelected,lineThickness);
                        imageLabel.setVisible(false);
                    }
                    }else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                    
                    }
                }
            });
            
            JMenuItem zoomInTool=new JMenuItem("Zoom In");
            zoomInTool.addActionListener(e ->{
            if(originalImage!=null){
                if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                    int width=editedImage.getWidth();
                    int height=editedImage.getHeight();
                    zoomImageEffect(width+100,height+100,zoomImage);
                    saveEffect();
                    }
                else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                    
                    }
            }
            });
            
            JMenuItem zoomOutTool=new JMenuItem("Zoom out");
            zoomOutTool.addActionListener(e->{
            if(originalImage!=null){
                if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                    int width=editedImage.getWidth();
                    int height=editedImage.getHeight();
                    zoomImageEffect(width-100,height-100,zoomImage);
                    saveEffect();
                    }
                else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);                    
                    }
            }
            });
            
            JMenuItem inversionFilter = new JMenuItem("Invert");
            inversionFilter.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        inversionFilter();                        
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem sepiaFilter = new JMenuItem("Sepia");
            sepiaFilter.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        sepiaFilter();                        
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem edgeDetectFilter = new JMenuItem("Edge Detection");
            edgeDetectFilter.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        edgeDetectFilter();                        
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem cyberpunkFilter = new JMenuItem("Cyberpunk");
            cyberpunkFilter.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        cyberpunkFilter();                        
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem brightBound=new JMenuItem("Brightness");
            brightBound.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        new SelectiveBrightnessAdjustment();
                        imageLabel.setVisible(false );                        
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem blurBound=new JMenuItem("Blur");
            blurBound.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        new SelectiveBlurEffect();
                         imageLabel.setVisible(false );                      
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem monochromeBound=new JMenuItem("Monochrome");
            monochromeBound.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        new SelectiveMonochromeEffect();
                        imageLabel.setVisible(false );                     
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }               
                    });
            
            JMenuItem sepiaBound=new JMenuItem("Sepia");
            sepiaBound.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        new SelectiveSepiaEffect();
                        imageLabel.setVisible(false );                       
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem inversionBound=new JMenuItem("Inversion");
            inversionBound.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        new SelectiveInversionEffect();
                        imageLabel.setVisible(false );                      
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem edgeDetectBound=new JMenuItem("Edge Detection");
            edgeDetectBound.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        new SelectiveEdgeDetectEffect();
                        imageLabel.setVisible(false );                      
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            JMenuItem cyberpunkBound=new JMenuItem("Cyberpunk");
            cyberpunkBound.addActionListener(e->{
                if(originalImage!=null){
                    if(!panelAddedStatus(getContentPane(),drawingPanel)&&!panelAddedStatus(getContentPane(),boxPanel)){
                        new SelectiveCyberpunkEffect();
                        imageLabel.setVisible(false );                     
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "The changes made is not save. Please save the changes","Notice",JOptionPane.INFORMATION_MESSAGE);
                    }                    
                }
                    });
            
            fileMenu.add(importItem);
            fileMenu.add(exportItem);
            toolsMenu.add(drawTool);
            toolsMenu.add(zoomInTool);
            toolsMenu.add(zoomOutTool);
            filtersMenu.add(inversionFilter);
            filtersMenu.add(sepiaFilter);
            filtersMenu.add(edgeDetectFilter);
            filtersMenu.add(cyberpunkFilter);
            boundsMenu.add(brightBound);
            boundsMenu.add(blurBound);
            boundsMenu.add(monochromeBound);
            boundsMenu.add(sepiaBound);
            boundsMenu.add(inversionBound);
            boundsMenu.add(edgeDetectBound);
            boundsMenu.add(cyberpunkBound);
            
            menuBar.add(fileMenu);
            menuBar.add(toolsMenu);
            menuBar.add(filtersMenu);
            menuBar.add(boundsMenu);
            
            setJMenuBar(menuBar);
        }
        
        private void updateImageLabel(){
            ImageIcon imageIcon= new ImageIcon(editedImage);
            imageLabel.setIcon(imageIcon);
            imageLabel.revalidate();
        }
         
        private boolean panelAddedStatus(Container container, Component panelToCheck){
            Component[] components=container.getComponents();
            for(Component component:components){
                if(component.equals(panelToCheck)){
                    return true;
                }                
            }
            return false;
        }
        
        private void removeDrawingPanel() {
        if (drawingPanel != null) {
            remove(drawingPanel);
            drawingPanel = null;
        }
    }
        private void removeBoxPanel() {
        if (boxPanel != null) {
            remove(boxPanel);
            boxPanel = null;
        }
    }
        private void saveEffect(){
            BufferedImage tempImage=copyImage(editedImage);
            adjustedImages.add(tempImage);
            zoomImage = copyImage(editedImage);
            updateImageLabel();
        }
        
        private void saveImage(){
            if(panelAddedStatus(getContentPane(),drawingPanel)){    
                removeDrawingPanel(); 
                saveEffect();                    
                imageLabel.setVisible(true);
            }
            else if(panelAddedStatus(getContentPane(),boxPanel)){
                removeBoxPanel();
                saveEffect();
                imageLabel.setVisible(true);
            }
            else{
                saveEffect();
                imageLabel.setVisible(true);
            }            
        }
        
        private void undoEffect(){
            if(!adjustedImages.isEmpty() && adjustedImages.size()!=1){
                adjustedImages.remove(adjustedImages.size() - 1);
                editedImage = copyImage(adjustedImages.get(adjustedImages.size() - 1));
                zoomImage = copyImage(adjustedImages.get(adjustedImages.size() - 1));
                updateImageLabel();
            }else if(adjustedImages.size()==1){
                editedImage = copyImage(originalImage);
                zoomImage = copyImage(originalImage);
                updateImageLabel();
            }           
        }
        
        private void undoImage(){
            if(panelAddedStatus(getContentPane(),drawingPanel)){    
                removeDrawingPanel();
                undoEffect();
            }
            else if(panelAddedStatus(getContentPane(),boxPanel)){
                removeBoxPanel();
                undoEffect();
            }
            else{
                undoEffect();
            }
        }
        
        private void revertImage(){  
            if(panelAddedStatus(getContentPane(),drawingPanel)){   
                removeDrawingPanel();
                editedImage=copyImage(originalImage);
                zoomImage=copyImage(originalImage);
                adjustedImages.clear();
                adjustedImages.add(originalImage);
                updateImageLabel();                                                       
            }
            else if(panelAddedStatus(getContentPane(),boxPanel)){
                removeBoxPanel();
                editedImage=copyImage(originalImage);
                zoomImage=copyImage(originalImage);
                adjustedImages.clear();
                adjustedImages.add(originalImage);
                updateImageLabel(); 
            }
            else{
                editedImage=copyImage(originalImage);
                zoomImage=copyImage(originalImage);
                adjustedImages.clear();
                adjustedImages.add(originalImage);
                updateImageLabel();
            }             
        }
        
        private BufferedImage copyImage(BufferedImage image){            
            ColorModel cm=image.getColorModel();
            boolean isAlphaPrm= cm.isAlphaPremultiplied();
            WritableRaster raster=image.copyData(null);
            return new BufferedImage(cm,raster,isAlphaPrm,null);            
        }
        
        private void importImage(){
            JFileChooser fileChooser=new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images","jpg","png"));
            int result=fileChooser.showOpenDialog(this);
            if(result==JFileChooser.APPROVE_OPTION){
                File selectedFile=fileChooser.getSelectedFile();
                
                try{
                    BufferedImage testImage=ImageIO.read(selectedFile);
                    if(testImage==null){
                        JOptionPane.showMessageDialog(this,"Invalid Image File Selected","Error",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    originalImage=testImage;
                    editedImage=copyImage(originalImage);
                    adjustedImages.add(copyImage(originalImage));
                    zoomImage=copyImage(originalImage);                   
                    updateImageLabel();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,"Error Loading the Image","Error",JOptionPane.ERROR_MESSAGE);
                }
                
            }
        }
        
        private void exportImage(){
          JFileChooser fileChooser=new JFileChooser();
          fileChooser.setFileFilter(new FileNameExtensionFilter("images","jpg","png"));
          
          int result = fileChooser.showSaveDialog(this);
          if(result==JFileChooser.APPROVE_OPTION){
              
              File selectedFile= fileChooser.getSelectedFile();
              
              try{
                  ImageIO.write(editedImage,"jpg",selectedFile);
              }
              catch(Exception ex){
                  ex.printStackTrace();
                  JOptionPane.showMessageDialog(this,"Error Saving The Image","Error",JOptionPane.ERROR_MESSAGE);
              }
          }
        }
        
        private void zoomImageEffect(int width,int height,BufferedImage sourceImage){            
            BufferedImage imageToZoom=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            Graphics2D imageZoomOut= imageToZoom.createGraphics();
            imageZoomOut.drawImage(sourceImage,0,0,width,height,null);
            imageZoomOut.dispose();
            editedImage=imageToZoom;
            updateImageLabel();
        }
          
       
        private void adjustBrightnessEffect(double scaleFactor){
            int width = editedImage.getWidth();
            int height = editedImage.getHeight();

            BufferedImage adjustedImage = copyImage(editedImage);

            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int rgb = (adjustedImages.get(adjustedImages.size()-1)).getRGB(x, y);

                    int alpha = (rgb>> 24) & 0xFF;
                    int red = (int) (((rgb >> 16) & 0xFF) * scaleFactor);
                    int green = (int) (((rgb >> 8) & 0xFF) * scaleFactor);
                    int blue = (int) ((rgb & 0xFF) * scaleFactor);

                    red = Math.min(255, Math.max(0, red));
                    green = Math.min(255, Math.max(0, green));
                    blue = Math.min(255, Math.max(0, blue));

                    int adjustedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    adjustedImage.setRGB(x, y, adjustedRGB);
                }
            }

            editedImage = adjustedImage;
            updateImageLabel();        
    }

        
        private void blurEffect(double blurIntensity){
            int width=editedImage.getWidth();
            int height=editedImage.getHeight();
                
            int kernelSize=(int)(5*blurIntensity);
            if(kernelSize%2==0){
                kernelSize++;
            }
                
            int[][] kernel = new int[kernelSize][kernelSize];
            int kernelSum=kernelSize*kernelSize;
                
            for(int i=0;i<kernelSize;++i){
                for(int j=0;j<kernelSize;++j){
                    kernel[i][j]=1;
                }
            }
                
            BufferedImage adjustedImage=copyImage(editedImage);
                
            for(int x=0;x<width;++x){
                for(int y=0;y<height;++y){
                    int red=0,green=0,blue=0;
                        
                    for(int i=-kernelSize/2;i<=kernelSize/2;++i){
                        for(int j=-kernelSize/2;j<=kernelSize/2;++j){
                            int pixelX=Math.min(Math.max(x+i,0), width-1);
                            int pixelY=Math.min(Math.max(y+j, 0), height-1);
                                
                            int rgb=(adjustedImages.get(adjustedImages.size()-1)).getRGB(pixelX, pixelY);
                                
                            red+=((rgb>>16)&0xFF)*kernel[i+kernelSize/2][j+kernelSize/2];
                            green+=((rgb>>8)&0xFF)*kernel[i+kernelSize/2][j+kernelSize/2];
                            blue+=(rgb & 0xFF)*kernel[i+kernelSize/2][j+kernelSize/2];
                                         
                        }
                    }
                    red/=kernelSum;
                    green/=kernelSum;
                    blue/=kernelSum;
                        
                    int newRGB=(red<<16)|(green<<8)|blue;
                    adjustedImage.setRGB(x,y,newRGB);
                }
            }
            editedImage=adjustedImage;
            updateImageLabel();
            
        }
        
        private void monochromeFilter(){
            for(int x=0;x<editedImage.getWidth();++x){
                for(int y=0;y<editedImage.getHeight();++y){
                    int rgb=(adjustedImages.get(adjustedImages.size()-1)).getRGB(x, y);
                    int monochrome=(int)(0.3*((rgb>>16)&0xff)+0.59*((rgb>>8)&0xff)+0.11*(rgb&0xff));                        
                    editedImage.setRGB(x, y, (monochrome<<16)|(monochrome<<8)|monochrome);
                }
            }
            updateImageLabel();          
        }
        
        private void sepiaFilter() {
            for(int x=0;x<editedImage.getWidth();x++){
                for(int y=0;y<editedImage.getHeight();y++){
                    int rgb = (adjustedImages.get(adjustedImages.size()-1)).getRGB(x, y);
                    int r = (rgb>>16) & 0xFF;
                    int g = (rgb>>8) & 0xFF;
                    int b = rgb & 0xFF;
                        
                    int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                    int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                    int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                        
                    tr = Math.min(255,tr);
                    tg = Math.min(255,tg);
                    tb = Math.min(255,tb);
                        
                    editedImage.setRGB(x, y, (tr <<16) | (tg << 8) | tb);
                }
            }                
            updateImageLabel();            
        }
         
        private void inversionFilter() {               
            for(int x=0;x<editedImage.getWidth();x++){
                for(int y=0;y<editedImage.getHeight();y++){
                    int rgb = (adjustedImages.get(adjustedImages.size()-1)).getRGB(x, y);
                    int r = 255 - ((rgb>>16) & 0xFF);
                    int g = 255 - ((rgb>>8) & 0xFF);
                    int b = 255 - (rgb & 0xFF);
                        
                    editedImage.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }                
            updateImageLabel();
        }
        
        private void edgeDetectFilter() {               
            int[][] sobelx = {{-1,0,1},{-2,0,2},{-1,0,1}};
            int[][] sobely = {{1,2,1},{0,0,0},{-1,-2,-1}};
                
            BufferedImage grayImage = new BufferedImage(originalImage.getWidth(),originalImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = grayImage.getGraphics();
            g.drawImage(editedImage, 0, 0, null);
            g.dispose();
                
            for(int x=1;x<editedImage.getWidth()-1;x++){
                for(int y=1;y<editedImage.getHeight()-1;y++){
                        
                    int gx =0,gy=0;
                    for(int i=-1 ; i<=1 ; i ++) {
                        for(int j =-1 ; j<=1 ; j++) {
                            int gray = (adjustedImages.get(adjustedImages.size()-1)).getRGB(x+i,y+j)& 0xFF;
                            gx += gray*sobelx[i+1][j+1];
                            gy += gray*sobely[i+1][j+1];
                        }
                    }
                    int magnitude = (int) Math.sqrt(gx *gx +gy *gy);
                    int newRgb = (magnitude << 16) | (magnitude << 8) | magnitude;
                    editedImage.setRGB(x,y,newRgb);
                }
            }
            updateImageLabel();    
        }
        
        private void cyberpunkFilter() {
            float redFactor = 1.2f;   
            float greenFactor = 1.0f;
            float blueFactor = 1.5f;
                
            for(int x=0;x<editedImage.getWidth();x++){
                for(int y=0;y<editedImage.getHeight();y++){
                    int rgb = (adjustedImages.get(adjustedImages.size()-1)).getRGB(x, y);
                    int r = (int) (((rgb>>16) & 0xFF) *redFactor);
                    int g = (int) (((rgb>>8) & 0xFF) *greenFactor);
                    int b = (int) ((rgb & 0xFF) *blueFactor);
                        
                    r = Math.min(255, Math.max(0, r));
                    g = Math.min(255, Math.max(0, g));
                    b = Math.min(255, Math.max(0, b));
                        
                    editedImage.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }                
            updateImageLabel();
        }  
        
        private void cropImageEffect(int x, int y){
            BufferedImage subImg=editedImage.getSubimage(0,0,x,y);
            editedImage=subImg;
            updateImageLabel();
        }
          
        private void resizeImageEffect(int xResize, int yResize){
            BufferedImage imageResize=new BufferedImage(xResize,yResize,editedImage.getType());  
            Graphics2D imageToResize= imageResize.createGraphics();
            imageToResize.drawImage(editedImage,0,0,xResize,yResize,null);
            imageToResize.dispose();
            editedImage=imageResize;
            updateImageLabel();
        }                   
          
        public class Draw{
            private Graphics2D lineDrawn;
            private int initialX, initialY;        
            
            public Draw(Color colorSelected,float lineThickness){
                lineDrawn=editedImage.createGraphics();
                lineDrawn.setColor(colorSelected); 
                lineDrawn.setStroke(new BasicStroke(lineThickness));
                drawingPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0, 0, this);
                        g2d.dispose();
                    }
                };                
                drawingPanel.setSize(editedImage.getWidth(), editedImage.getHeight());    
                
                drawingPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        initialX=e.getX();
                        initialY=e.getY();
                        saveEffect();
                    }
                 });
                drawingPanel.addMouseMotionListener(new MouseAdapter(){
                    @Override
                    public void mouseDragged(MouseEvent e){
                        int endX=e.getX();
                        int endY=e.getY();
                        lineDrawn.drawLine(initialX,initialY,endX,endY);
                        initialX=endX;
                        initialY=endY;
                        drawingPanel.repaint();
                    }
                }); 
                add(drawingPanel);
        }
    }
          
        public class SelectiveBrightnessAdjustment{
            public SelectiveBrightnessAdjustment(){
                boxPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0, 0, this);
                        g2d.dispose();
                    }
                };
                boxPanel.setSize(editedImage.getWidth(), editedImage.getHeight()); 
                  
                boxPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                            coordinateX=e.getX();
                            coordinateY=e.getY();
                            boundingBox=new Rectangle(coordinateX,coordinateY,70,50);
                            adjustBrightness(boundingBox,1.5);
                            saveEffect();
                            boxPanel.repaint();                        
                    }
                }); 
                add(boxPanel);
            }
              
            private void adjustBrightness(Rectangle boundingBox, double scaleFactor){
                for(int x=boundingBox.x;x<boundingBox.x+boundingBox.width;++x){ 
                    for(int y=boundingBox.y;y<boundingBox.y+boundingBox.height;++y){
                        int rgb=editedImage.getRGB(x,y);
                        
                        int alpha=(rgb>>24)& 0xFF;
                        int red=(int)(((rgb>>16)& 0xFF)*scaleFactor);
                        int green=(int)(((rgb>>8)& 0xFF)*scaleFactor);
                        int blue=(int)((rgb& 0xFF)*scaleFactor);
                        
                        red=Math.min(255,Math.max(0, red));
                        green=Math.min(255,Math.max(0, green));
                        blue=Math.min(255,Math.max(0, blue));
                        
                        int adjustedRGB=(alpha<<24)|(red<<16)|(green<<8)|blue;
                        editedImage.setRGB(x, y,adjustedRGB);                          
                    }
                }
            }
        }
        
        public class SelectiveBlurEffect{
            public SelectiveBlurEffect(){
                boxPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0,0, this);
                        g2d.dispose();
                    }
                };
                boxPanel.setSize(editedImage.getWidth(), editedImage.getHeight());
                  
                boxPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        coordinateX=e.getX();
                        coordinateY=e.getY();
                        boundingBox=new Rectangle(coordinateX,coordinateY,70,50);
                        adjustBlur(boundingBox,1.5);
                        saveEffect();
                        boxPanel.repaint();
                    }
                }); 
                add(boxPanel);
            }
              
            private void adjustBlur(Rectangle boundingBox, double blurIntensity){
                int kernelSize=(int)(5*blurIntensity);
                if(kernelSize%2==0){
                    kernelSize++;
                }
                
                int[][] kernel = new int[kernelSize][kernelSize];
                int kernelSum=kernelSize*kernelSize;
                
                for(int i=0;i<kernelSize;++i){
                    for(int j=0;j<kernelSize;++j){
                        kernel[i][j]=1;
                    }
                }
                
                for(int x=boundingBox.x;x<boundingBox.x+boundingBox.width;++x){    
                    for(int y=boundingBox.y;y<boundingBox.y+boundingBox.height;++y){
                        int red=0,green=0,blue=0;
                        
                        for(int i=-kernelSize/2;i<=kernelSize/2;++i){
                            for(int j=-kernelSize/2;j<=kernelSize/2;++j){
                                int pixelX=Math.min(Math.max(x+i,0),boundingBox.x+boundingBox.width-1);
                                int pixelY=Math.min(Math.max(y+j, 0),boundingBox.y+boundingBox.height-1);
                                
                                int rgb=editedImage.getRGB(pixelX, pixelY);
                                
                                red+=((rgb>>16)&0xFF)*kernel[i+kernelSize/2][j+kernelSize/2];
                                green+=((rgb>>8)&0xFF)*kernel[i+kernelSize/2][j+kernelSize/2];
                                blue+=(rgb & 0xFF)*kernel[i+kernelSize/2][j+kernelSize/2];
                                         
                            }
                        }
                        red/=kernelSum;
                        green/=kernelSum;
                        blue/=kernelSum;
                        
                        int newRGB=(red<<16)|(green<<8)|blue;
                        editedImage.setRGB(x,y,newRGB);
                    }
                }
            }
            
        }
        
        public class SelectiveMonochromeEffect{
            public SelectiveMonochromeEffect(){
                boxPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0, 0, this);
                        g2d.dispose();
                    }
                };
                boxPanel.setSize(editedImage.getWidth(), editedImage.getHeight());
                  
                boxPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        coordinateX=e.getX();
                        coordinateY=e.getY();
                        boundingBox=new Rectangle(coordinateX,coordinateY,70,50);
                        adjustMonochrome(boundingBox);
                        saveEffect();
                        boxPanel.repaint();
                    }
                });
                add(boxPanel); 
            }
              
            private void adjustMonochrome(Rectangle boundingBox){
                for(int x=boundingBox.x;x<boundingBox.x+boundingBox.width;++x){
                for(int y=boundingBox.y;y<boundingBox.y+boundingBox.height;++y){
                    int rgb=editedImage.getRGB(x, y);
                    int monochrome=(int)(0.3*((rgb>>16)&0xff)+0.59*((rgb>>8)&0xff)+0.11*(rgb&0xff));                        
                    editedImage.setRGB(x, y, (monochrome<<16)|(monochrome<<8)|monochrome);
                }
            }
            }            
        }
        
        public class SelectiveSepiaEffect{
            public SelectiveSepiaEffect(){
                boxPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0, 0, this);
                        g2d.dispose();
                    }
                };
                boxPanel.setSize(editedImage.getWidth(), editedImage.getHeight());
                  
                boxPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        coordinateX=e.getX();
                        coordinateY=e.getY();
                        boundingBox=new Rectangle(coordinateX,coordinateY,70,50);
                        adjustSepia(boundingBox);
                        saveEffect();
                        boxPanel.repaint();
                    }
                });
                add(boxPanel);
            }
            
            private void adjustSepia(Rectangle boundingBox){
                for(int x=boundingBox.x;x<boundingBox.x+boundingBox.width;++x){   
                    for(int y=boundingBox.y;y<boundingBox.y+boundingBox.height;++y){
                        int rgb = editedImage.getRGB(x, y);
                        int r = (rgb>>16) & 0xFF;
                        int g = (rgb>>8) & 0xFF;
                        int b = rgb & 0xFF;
                        
                        int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                        int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                        int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                        
                        tr = Math.min(255,tr);
                        tg = Math.min(255,tg);
                        tb = Math.min(255,tb);
                        
                        editedImage.setRGB(x, y, (tr <<16) | (tg << 8) | tb);
                    }
                }
            }                       
        }
        
        public class SelectiveInversionEffect{
            public SelectiveInversionEffect(){
                boxPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0, 0, this);
                        g2d.dispose();
                    }
                };
                boxPanel.setSize(editedImage.getWidth(), editedImage.getHeight());
                  
                boxPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        coordinateX=e.getX();
                        coordinateY=e.getY();
                        boundingBox=new Rectangle(coordinateX,coordinateY,70,50);
                        adjustInversion(boundingBox);
                        saveEffect();
                        boxPanel.repaint();
                    }
                });
                add(boxPanel); 
            }
            
            private void adjustInversion(Rectangle boundingBox){
                for(int x=boundingBox.x;x<boundingBox.x+boundingBox.width;++x){
                    for(int y=boundingBox.y;y<boundingBox.y+boundingBox.height;++y){
                        int rgb = editedImage.getRGB(x, y);
                        int r = 255 - ((rgb>>16) & 0xFF);
                        int g = 255 - ((rgb>>8) & 0xFF);
                        int b = 255 - (rgb & 0xFF);
                        
                        editedImage.setRGB(x, y, (r << 16) | (g << 8) | b);
                    }
                }                
            }              
        }
        
        public class SelectiveEdgeDetectEffect{
            public SelectiveEdgeDetectEffect(){
                boxPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0, 0, this);
                        g2d.dispose();
                    }
                };
                boxPanel.setSize(editedImage.getWidth(), editedImage.getHeight());
                  
                boxPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        coordinateX=e.getX();
                        coordinateY=e.getY();
                        boundingBox=new Rectangle(coordinateX,coordinateY,70,50);
                        adjustEdgeDetect(boundingBox);
                        saveEffect();
                        boxPanel.repaint();
                    }
                });
                add(boxPanel);
            }
            
            private void adjustEdgeDetect(Rectangle boundingBox){
                int[][] sobelx = {{-1,0,1},{-2,0,2},{-1,0,1}};
                int[][] sobely = {{1,2,1},{0,0,0},{-1,-2,-1}};
                
                BufferedImage grayImage = new BufferedImage(originalImage.getWidth(),originalImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
                Graphics g = grayImage.getGraphics();
                g.drawImage(editedImage, 0, 0, null);
                g.dispose();
                
                for(int x=boundingBox.x;x<boundingBox.x+boundingBox.width;++x){
                    for(int y=boundingBox.y;y<boundingBox.y+boundingBox.height;++y){
                        
                        int gx =0,gy=0;
                        for(int i=-1 ; i<=1 ; i ++) {
                            for(int j =-1 ; j<=1 ; j++) {
                                int gray = grayImage.getRGB(x+i, y+j) & 0xFF;
                                gx += gray*sobelx[i+1][j+1];
                                gy += gray*sobely[i+1][j+1];
                            }
                        }
                        int magnitude = (int) Math.sqrt(gx *gx +gy *gy);
                        int newRgb = (magnitude << 16) | (magnitude << 8) | magnitude;
                        editedImage.setRGB(x,y,newRgb);
                    }
                }                               
            }            
        }
        
        public class SelectiveCyberpunkEffect{
            public SelectiveCyberpunkEffect(){
                boxPanel=new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g){
                        super.paintComponent(g);
                        Graphics2D g2d=(Graphics2D)g.create();
                        g2d.drawImage(editedImage, 0, 0, this);
                        g2d.dispose();
                    }
                };
                boxPanel.setSize(editedImage.getWidth(), editedImage.getHeight());
                  
                boxPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        coordinateX=e.getX();
                        coordinateY=e.getY();
                        boundingBox=new Rectangle(coordinateX,coordinateY,70,50);
                        adjustCyberpunk(boundingBox);
                        saveEffect();
                        boxPanel.repaint();
                    }
                });
                add(boxPanel);
            }
            
            private void adjustCyberpunk(Rectangle boundingBox){
                float redFactor = 1.2f;   
                float greenFactor = 1.0f;
                float blueFactor = 1.5f;
                
                for(int x=boundingBox.x;x<boundingBox.x+boundingBox.width;++x){
                    for(int y=boundingBox.y;y<boundingBox.y+boundingBox.height;++y){
                        int rgb = editedImage.getRGB(x, y);
                        int r = (int) (((rgb>>16) & 0xFF) *redFactor);
                        int g = (int) (((rgb>>8) & 0xFF) *greenFactor);
                        int b = (int) ((rgb & 0xFF) *blueFactor);
                        
                        r = Math.min(255, Math.max(0, r));
                        g = Math.min(255, Math.max(0, g));
                        b = Math.min(255, Math.max(0, b));
                        
                        editedImage.setRGB(x, y, (r << 16) | (g << 8) | b);
                    }
                }                               
            }             
        }
    
    public static void main(String[] args) {        
        SwingUtilities.invokeLater(()->new PhotoEditor());
    }    
}
