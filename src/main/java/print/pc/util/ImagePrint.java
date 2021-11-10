package print.pc.util;

import java.awt.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import static java.awt.print.Printable.*;

public class ImagePrint {
    public static void main(String[] args) {
        //imagePrint("C:/Users/Administrator/Pictures/print14.png");
    }

    public static void imagePrint(String path, int x, int w) {
        int width = 475;//new Double((120 / 25.4) * 72).intValue();//475
        int height = new Double((180 / 25.4) * 72).intValue();//510

        // 通俗理解就是书、文档
        Book book = new Book();

        // 把 PageFormat 和 Printable 添加到书中，组成一个页面
        book.append((graphics, pageFormat, pageIndex) -> {//通过一个匿名内部内实现Printable接口，不懂的自行查看jdk8的新特性
            try {
                Image image = ImageIO.read(new File(path));
                graphics.drawImage(image,x,12, width-w, height,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return PAGE_EXISTS;//返回0（PAGE_EXISTS）则执行打印，返回1（NO_SUCH_PAGE）则不执行打印
        }, pf(width, height));
        // 获取打印服务对象
        print(book);
    }

    private static PageFormat pf(int pageWidth, int pageHeight){
        // 设置成竖打
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);
        // 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
        Paper p = new Paper();
        p.setSize(pageWidth, pageHeight);//纸张大小
        p.setImageableArea(0,0, pageWidth, pageHeight);//打印区域
        pf.setPaper(p);
        return pf;
    }

    private static void print(Book book){
        PrinterJob job = PrinterJob.getPrinterJob();
        // 设置打印类
        job.setPageable(book);
        try {
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
}