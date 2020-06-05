package util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import sun.awt.OSInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;

/**
 * @author JQH
 * @since 上午 10:28 19/12/27
 */
public class VCodeUtil {
    private static int WHITE = 0xffffffff;
    private static int BLACK = 0xff000000;

    public static Tesseract getTesseract() {//Tesseract对象不能并发调用，因此每次调用的时候生成一个Tesseract对象
        Tesseract tesseract = new Tesseract();
        if(OSInfo.getOSType() == OSInfo.OSType.LINUX)
            tesseract.setDatapath("/usr/tesseract/tesseract-4.1.1/tessdata");
        else
            tesseract.setDatapath("E:/IntelliJIDEAProjects/tesseract/tessdata");
        tesseract.setTessVariable("user_defined_dpi", "300");
        tesseract.setLanguage("vcode");
        return tesseract;
    }

    public static String getVCodeRes(BufferedInputStream imgStream) {
        try {
            BufferedImage img = ImageIO.read(imgStream);
            if(img == null)
                return null;
            int width = img.getWidth();
            int height = img.getHeight();
            int minX = img.getMinX();
            int minY = img.getMinY();
            for (int i = minX; i < width; i++)
                for (int j = minY; j < height; j++)
                    if(isBackGround(img, i, j))
                        img.setRGB(i, j, WHITE);
            for (int i = minX; i < width; i++) {
                for (int j = minY; j < height; j++) {
                    if(i - minX <= 3 || width - i <=3 || j - minY <= 3 || height - j <= 3) {
                        img.setRGB(i, j, WHITE);
                        continue;
                    }
                    int[] rgb = getRGBByPixel(img.getRGB(i, j));
                    int[] upside = getRGBByPixel(img.getRGB(i, j - 1));
                    if (upside[0] - rgb[0] > 150
                            || (rgb[0] < 175 && (upside[1] - rgb[1] > 50 || upside[2] - rgb[2] > 50))) {
                        img.setRGB(i, j, getPixelByRGB(upside));
                    }
                }
            }
            for (int i = minX + 3; i < width - 3; i++)
                for (int j = minY + 3; j < height - 3; j++)
                    if(isSpot(img, i, j))
                        img.setRGB(i, j, WHITE);
            for (int i = minX; i < width; i++)
                for (int j = minY; j < height; j++) {
                    int[] rgb = getRGBByPixel(img.getRGB(i, j));
                    if(rgb[0] + rgb[1] + rgb[2] < 550)
                        img.setRGB(i, j, BLACK);
                    else
                        img.setRGB(i, j, WHITE);
                }
            return getOCRResult(img);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getOCRResult(BufferedImage img) throws TesseractException {
        String str = getTesseract().doOCR(img);
        StringBuilder builder = new StringBuilder();
        for(int j = 0; j < str.length(); ++j)
            if(str.charAt(j) != '\n')
                builder.append(str.charAt(j));
        str = builder.toString();
        if(str.length() == 4)
            return str;
        return null;
    }

    private static boolean isSpot(BufferedImage img, int i, int j) {
        int cnt = 0;
        if(img.getRGB(i + 1, j) != WHITE && img.getRGB(i + 2, j) != WHITE )
            ++cnt;
        if(img.getRGB(i - 1, j) != WHITE && img.getRGB(i - 2, j) != WHITE)
            ++cnt;
        if(img.getRGB(i, j + 1) != WHITE && img.getRGB(i, j + 2) != WHITE)
            ++cnt;
        if(img.getRGB(i, j - 1) != WHITE && img.getRGB(i, j - 2) != WHITE)
            ++cnt;
        return cnt < 2;
    }

    private static boolean isBackGround(BufferedImage img, int x, int y) {
        int[] rgb = getRGBByPixel(img.getRGB(x, y));
        if(isGBGray(rgb, x < img.getMinX() + 40 ? 200 : 210))
            return true;
        if(y < img.getHeight() - 1 && isDark(getRGBByPixel(img.getRGB(x, y + 1)))
                && isGBGray(rgb, 175))
            return true;
        int width = img.getWidth();
        for(int i = 0; i < 4 && x + i < width; ++i)
            if(!isGBGray(getRGBByPixel(img.getRGB(x + i, y)), 180))
                return false;
        return true;
    }

    private static boolean isGBGray(int rgb[], int degree) {
        return rgb[1] > degree && rgb[2] > degree;
    }

    private static boolean isDark(int rgb[]) {
        return rgb[0] < 135;
    }

    private static int[] getRGBByPixel(int pixel) {
        int[] rgb = new int[3];
        rgb[0] = (pixel & 0xff0000) >> 16;
        rgb[1] = (pixel & 0xff00) >> 8;
        rgb[2] = (pixel & 0xff);
        return rgb;
    }

    private static int getPixelByRGB(int[] rgb) {
        int pixel = 0;
        pixel |= rgb[0] << 16;
        pixel |= rgb[1] << 8;
        pixel |= rgb[2];
        return pixel;
    }
}
