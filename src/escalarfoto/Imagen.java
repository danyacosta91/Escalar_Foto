/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escalarfoto;

/**
 *
 * @author dany
 */
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Imagen {

    private int[][] pixeles;
    private int alto;
    private int ancho;
    private int bits;
    private int componentes;

    public Imagen(String ruta_img) {
        try {
            BufferedImage image = ImageIO.read(new File(ruta_img));
            this.alto = image.getHeight();
            this.ancho = image.getWidth();
            this.bits = image.getColorModel().getPixelSize();
            this.componentes = image.getColorModel().getNumComponents();
            this.pixeles = new int[this.alto][this.ancho];

            int pixel;
            for (int i = 0; i < this.alto; i++) {
                for (int j = 0; j < this.ancho; j++) {
                    pixel = image.getRaster().getSample(j, i, 0);
                    this.pixeles[i][j] = pixel;
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Imagen(int[][] pixeles, int bits) {
        if (pixeles == null) {
            return;
        }
        if (bits > 8) {
            bits = 8;
        }
        if (bits < 8) {
            bits = 1;
        }

        this.pixeles = new int[pixeles.length][pixeles[0].length];
        this.alto = pixeles.length;
        this.ancho = pixeles[0].length;
        this.bits = bits;
        this.componentes = 1;

        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[i].length; j++) {
                this.pixeles[i][j] = pixeles[i][j];
            }
        }
    }

    public int getAncho() {
        return this.ancho;
    }

    public int getAlto() {
        return this.alto;
    }

    public int getBits() {
        return this.bits;
    }

    public int getPixel(int i, int j) {
        if (i < 0 || i >= this.alto || j < 0 || j >= this.ancho) {
            return 255;
        }
        return this.pixeles[i][j];
    }

    public Imagen getVentana(int io, int jo, int i_ventana, int j_ventana) {
        int[][] ventana = new int[i_ventana][j_ventana];
        int cont1 = 0, cont2 = 0;

        for (int i = io; i < i_ventana; i++) {
            for (int j = jo; j < j_ventana; j++) {
                ventana[cont1][cont2] = this.getPixel(i, j);
                cont2++;
            }
            cont1++;
            cont2 = 0;
        }

        return new Imagen(ventana, this.bits);
    }

    public boolean escribeImagen(String ruta_img_salida) {
        try {
            BufferedImage imagen_salida;
            if (this.bits == 8) {
                imagen_salida = new BufferedImage(this.ancho, this.alto, BufferedImage.TYPE_BYTE_GRAY);
            } else {
                imagen_salida = new BufferedImage(this.ancho, this.alto, BufferedImage.TYPE_BYTE_BINARY);
            }

            WritableRaster salida = imagen_salida.getRaster();

            for (int i = 0; i < this.alto; i++) {
                for (int j = 0; j < this.ancho; j++) {
                    salida.setSample(j, i, 0, this.pixeles[i][j]);
                }
            }

            imagen_salida.setData(salida);
            ImageIO.write(imagen_salida, "jpg", new File(ruta_img_salida));
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    public int[][] getPixeles() {
        return this.pixeles;
    }

    public void negativo() {
        int max = 0;
        if (this.bits == 1) {
            max = 1;
        } else if (this.bits == 8) {
            max = 255;
        }

        for (int i = 0; i < this.alto; i++) {
            for (int j = 0; j < this.ancho; j++) {
                this.pixeles[i][j] = max - this.pixeles[i][j];
            }
        }
    }

    public boolean setNumBits(int bits) {
        if (bits != 1 && bits != 8) {
            return false;
        }
        this.bits = bits;
        return true;
    }

    public static String[] getAvailableFormats() {
        return ImageIO.getReaderFormatNames();
    }

    public BufferedImage loadBufferedImage(String fileName) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return image;

    }

    public BufferedImage scaleToSize(int nMaxWidth, int nMaxHeight, BufferedImage imgSrc) {
        int nHeight = imgSrc.getHeight();
        int nWidth = imgSrc.getWidth();
        double scaleX = (double) nMaxWidth / (double) nWidth;
        double scaleY = (double) nMaxHeight / (double) nHeight;
        return scale(scaleX, scaleY, imgSrc);
    }

    public BufferedImage scale(double scale, BufferedImage srcImg) {
        if (scale == 1) {
            return srcImg;
        }
        AffineTransformOp op = 
                new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), null);
        return op.filter(srcImg, null);
    }

    public BufferedImage scale(double scaleX, double scaleY, BufferedImage srcImg) {
        if (scaleX == 1 || scaleY == 1) {
            return srcImg;
        }
        AffineTransformOp op = 
                new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), null);
        return op.filter(srcImg, null);
    }
    
    public void saveImageToDisk(BufferedImage bi, String str, String format) {
        if (bi != null && str != null) {
            try {
                ImageIO.write(bi, format, new File(str));
            } catch (Exception e) {
            }
        }
    }
}
