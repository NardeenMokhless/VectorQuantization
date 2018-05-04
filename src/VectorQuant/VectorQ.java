package VectorQuant;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.imageio.ImageIO;

/**
 * Created by Nardeen on 03-Dec-17.
 */

class block {
    public int [][] bk;
    public int code;

    block()
    {
        int [] [] bk = new int[0][0];
        code = -1;
    }
    block(int [] [] comp)
    {
        bk = comp;
        code = -1;
    }
    public void printBlock()
    {
        for(int i=0;i<bk.length;i++) {
            for (int j = 0; j < bk.length; j++)
            {
                System.out.print(bk[i][j] + " ");
            }
            System.out.println();
        }
    }
}
public class VectorQ
{
    public static class ImageClass {

        public static int[][] readImage(String path){


            BufferedImage img;
            try {
                img = ImageIO.read(new File(path));

                int hieght=img.getHeight();
                int width=img.getWidth();

                int[][] imagePixels=new int[hieght][width];
                for(int x=0;x<width;x++){
                    for(int y=0;y<hieght;y++){

                        int pixel=img.getRGB(x, y);

                        int red=(pixel  & 0x00ff0000) >> 16;
                        int grean=(pixel  & 0x0000ff00) >> 8;
                        int blue=pixel  & 0x000000ff;
                        int alpha=(pixel & 0xff000000) >> 24;
                        imagePixels[y][x]=red;
                    }
                }

                return imagePixels;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }

        }

        public static void writeImage(int[][] imagePixels,String outPath){

            BufferedImage image = new BufferedImage(imagePixels.length, imagePixels[0].length, BufferedImage.TYPE_INT_RGB);
            for (int y= 0; y < imagePixels.length; y++) {
                for (int x = 0; x < imagePixels[y].length; x++) {
                    int value =-1 << 24;
                    value= 0xff000000 | (imagePixels[y][x]<<16) | (imagePixels[y][x]<<8) | (imagePixels[y][x]);
                    image.setRGB(x, y, value);

                }
            }

            File ImageFile = new File(outPath);
            try {
                ImageIO.write(image, "jpg", ImageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
    static Vector<block> blocks = new Vector<block>();
    static Vector<block> codeBook = new Vector<block>();
    public static void printVectorBlocks()
    {
        for (int i=0;i<blocks.size();i++)
            blocks.elementAt(i).printBlock();
    }
    public static void DivideIntoBlocks(int[][] Image,int vectorSize)
    {
        int dv = Image.length/vectorSize;
        int jumpi = 0,jumpj = 0;
        while (jumpi != dv*vectorSize)
        {
            while (jumpj != dv*vectorSize)
            {
                int[][] v = new int[vectorSize][vectorSize];
                for (int i = 0 + jumpi,x=0; i < vectorSize + jumpi; i++,x++) {
                    for (int j = 0 + jumpj,y=0; j < vectorSize + jumpj; j++,y++) {
                        v[x][y] = Image[i][j];
                    }
                }
                block b = new block(v);
                blocks.add(b);
                jumpj += vectorSize;
            }
            jumpj = 0;
            jumpi +=vectorSize;
        }
    }
    public static int chooseRange(block b,Vector<Vector<block>> v)
    {
        int ind = -1,total =100000,tot=0;
        for(int k=0;k<v.size();k++) {
            for (int i = 0; i < b.bk.length; i++) {
                for (int j = 0; j < b.bk.length; j++) {
                    tot += Math.abs(b.bk[i][j]-(v.get(k).get(0).bk[i][j]));
                }
            }
            if(tot<total)
            {
                total = tot;
                ind = k;
                tot = 0;
            }
        }
        return ind;
    }
    public static int searchForBlock(block b,Vector<Vector<block>> v)
    {
        int ind =-1;
        for (int i = 0;i < v.size();i++)
            for (int j = 1;j < v.get(i).size();j++)
                if(v.get(i).get(j).bk == b.bk)
                    ind = i;
        return ind;
    }
    public static int searchInCodeBook(int code)
    {
        int ind =-1;
        for (int i = 0;i < codeBook.size();i++)
                if(codeBook.elementAt(i).code==code)
                    ind = i;
        return ind;
    }
    public static int[][] compressImage(int noOfVectors)
    {
        int value = (int) Math.sqrt(blocks.size());
        int[][] compBlock = new int[value][value];
        Vector<Vector<block>> tree = new Vector<Vector<block>>();

        //get mid point
        int vectorSize = blocks.elementAt(0).bk.length;
        int [][] mid = new int[vectorSize][vectorSize];
        for (int i=0;i<vectorSize;i++)
            for (int j=0;j<vectorSize;j++)
                mid[i][j]=0;
        block b = new block(mid);

        for (int k=0;k<blocks.size();k++)
            for (int i=0;i<vectorSize;i++)
                for (int j=0;j<vectorSize;j++)
                    mid[i][j]+=blocks.elementAt(k).bk[i][j];

        for (int i=0;i<vectorSize;i++)
            for (int j=0;j<vectorSize;j++)
                mid[i][j] /= blocks.size();

        //splitting into 2 points
        int [][] mid1 = new int[vectorSize][vectorSize];
        int [][] mid2 = new int[vectorSize][vectorSize];
        for (int i=0;i<vectorSize;i++){
            for (int j=0;j<vectorSize;j++){
                mid1[i][j]=mid[i][j]-1;
                mid2[i][j]=mid[i][j]+1;
            }
        }
        block b1= new block(mid1),b2 = new block(mid2);
        Vector<block> v1=new Vector<block>(), v2=new Vector<block>();
        v1.add(b1);     v2.add(b2);
        tree.add(v1);   tree.add(v2);

        int loop = (int)(Math.log10(noOfVectors) / Math.log10(2.));
        int flag = 0;
        for (int k =0;k<loop;k++)
        {
            //choose range
            for(int i=0;i<blocks.size();i++)
            {
                block bl = blocks.elementAt(i);
                int ind = chooseRange(blocks.elementAt(i),tree);
                tree.elementAt(ind).add(bl);
            }
            //update mid point
            int sz = tree.size();
            Vector<block> midP = new Vector<block>();
            for(int a=0;a<sz;a++ ) {
                mid = new int[vectorSize][vectorSize];
                for (int i = 0; i < vectorSize; i++)
                    for (int j = 0; j < vectorSize; j++)
                        mid[i][j] = 0;

                for (int c = 1; c < tree.get(a).size(); c++) {
                    for (int i = 0; i < vectorSize; i++)
                    {
                        for (int j = 0; j < vectorSize; j++)
                        {
                            mid[i][j] += tree.get(a).get(c).bk[i][j];
                        }
                    }
                }
                for (int i = 0; i < vectorSize; i++)
                    for (int j = 0; j < vectorSize; j++)
                        mid[i][j] /= ((tree.get(a).size())-1);

                b = new block(mid);
                midP.add(b);

                //update the mid point in the tree
                tree.get(a).get(0).bk= mid;
            }
            //splitting into 2 points
            if(k != loop -1) {
                tree = new Vector<Vector<block>>();
                for (int r = 0; r < midP.size(); r++) {
                    mid1 = new int[vectorSize][vectorSize];
                    mid2 = new int[vectorSize][vectorSize];
                    for (int i = 0; i < vectorSize; i++) {
                        for (int j = 0; j < vectorSize; j++) {
                            mid1[i][j] = midP.elementAt(r).bk[i][j] - 1;
                            mid2[i][j] = midP.elementAt(r).bk[i][j] + 1;
                        }
                    }
                    b1 = new block(mid1);
                    b2 = new block(mid2);
                    v1 = new Vector<block>();
                    v2 = new Vector<block>();
                    v1.add(b1);
                    v2.add(b2);
                    tree.add(v1);
                    tree.add(v2);
                }
            }
            else if(flag == 0){
                loop ++;
                flag = 1;
            }
        }
        // save in codeBook
        for(int i=0;i<tree.size();i++)
        {
            block codeB = new block();
            codeB = tree.get(i).get(0);
            codeB.code = i;
            codeBook.add(codeB);
        }
        //compress
        int ind=0;
        for(int i=0;i<compBlock.length;i++){
            for(int j=0;j<compBlock.length;j++){
                compBlock[i][j]=searchForBlock(blocks.elementAt(ind),tree);
                ind++;
            }}

        for(int i=0;i<compBlock.length;i++) {
            for (int j = 0; j < compBlock.length; j++) {
                System.out.print(compBlock[i][j] + " ");
            }
            System.out.println();
        }
        return compBlock;
    }
    public static int [][] decompressImage(int[][] compBlock)
    {
        int vectorSize= blocks.elementAt(0).bk.length;
        int ImageLength = (int) Math.sqrt(blocks.size()*vectorSize*vectorSize);
        int [][] decompBlock = new int[ImageLength][ImageLength];
        int dv = ImageLength/vectorSize;
        int jumpi = 0,jumpj = 0;
        int a=0,b=0;
        while (jumpi != dv*vectorSize)
        {
            while (jumpj != dv*vectorSize)
            {
                int ind = searchInCodeBook(compBlock[a][b]);
                int [][] cd = codeBook.elementAt(ind).bk;
                for (int i = 0 + jumpi,x=0; i < vectorSize + jumpi; i++,x++) {
                    for (int j = 0 + jumpj,y=0; j < vectorSize + jumpj; j++,y++) {
                         decompBlock[i][j] = cd[x][y];
                    }
                }
                jumpj += vectorSize;
                b++;
            }
            jumpj = 0;
            b=0;
            jumpi +=vectorSize;
            a++;
        }
        for(int i=0;i<decompBlock.length;i++) {
            for (int j = 0; j < decompBlock.length; j++) {
                System.out.print(decompBlock[i][j] + " ");
            }
            System.out.println();
        }
        return decompBlock;
    }
}
