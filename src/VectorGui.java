import VectorQuant.VectorQ;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Nardeen on 04-Dec-17.
 */
public class VectorGui extends VectorQ {
    private JTextField textField1;
    private JTextField textField2;
    private JButton runButton;
    private JPanel panel1;

    public VectorGui() {
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int vectorSize = Integer.parseInt(textField1.getText());
                int noOfVectors = Integer.parseInt(textField2.getText());

                int[][] pixels = ImageClass.readImage("D:\\FCI\\3.1\\Multimedia\\Assignments\\VectorQuant\\cameraMan.jpg");
                //int [][] pixels = {{1,2,7,9,4,11},{3,4,6,6,12,12},{4,9,15,14,9,9}, {10,10,20,18,8,8},{4,3,17,16,1,4},{4,5,18,18,5,6}};

                VectorQ v = new VectorQ();
                v.DivideIntoBlocks(pixels,vectorSize);
                int [][] comp = v.compressImage(noOfVectors);
                System.out.println();
                int [][] decomp = v.decompressImage(comp);
                ImageClass.writeImage(decomp, "D:\\FCI\\3.1\\Multimedia\\Assignments\\VectorQuant\\cameraMan_out.jpg");

                File compFile = new File("D:\\FCI\\3.1\\Multimedia\\Assignments\\VectorQuant\\c.txt");
                PrintWriter pff = null;
                try {
                    pff = new PrintWriter(compFile);
                } catch (FileNotFoundException ex) {
                    // ex.printStackTrace();
                }
                for (int i=0;i<comp.length;i++){
                    for(int j=0;j<comp.length;j++){
                        pff.print(comp[i][j]+" ");}
                    pff.println();
                }
                pff.close();

                File decompFile = new File("D:\\FCI\\3.1\\Multimedia\\Assignments\\VectorQuant\\d.txt");
                PrintWriter pf = null;
                try {
                    pf = new PrintWriter(decompFile);
                } catch (FileNotFoundException ex) {
                    // ex.printStackTrace();
                }
                for (int i=0;i<decomp.length;i++){
                    for(int j=0;j<decomp.length;j++){
                        pf.print(decomp[i][j]+" ");}
                }
                pf.close();
            }
        });

    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new VectorGui().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}