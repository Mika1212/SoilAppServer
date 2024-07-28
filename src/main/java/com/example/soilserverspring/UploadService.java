package com.example.soilserverspring;

import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


public class UploadService {

    public static double getAverageRedWholeImage(String path) {
        BufferedImage image;
        try {
            File imgFile = new File(path);
            if (!imgFile.exists()) {
                System.out.println("Image file NOT FOUND at: " + imgFile.getAbsolutePath());
            } else if (!imgFile.canRead()) {
                    System.out.println("No READ perms for file at: " + imgFile.getAbsolutePath());
                } else {
                    System.out.println("Loading valid image file from: " + imgFile.getAbsolutePath());
                }

            image = ImageIO.read(new File(path));

            int width = image.getWidth();
            int height = image.getHeight();

            double value = 0.0;
            for(int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int red = color.getRed();
                    value += red;
                }
            }
            return value/(width*height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double[] getMinMaxRedWholeImage(String path) {
        BufferedImage image;
        try {
            File imgFile = new File(path);
            if (!imgFile.exists()) {
                System.out.println("Image file NOT FOUND at: " + imgFile.getAbsolutePath());
            } else if (!imgFile.canRead()) {
                System.out.println("No READ perms for file at: " + imgFile.getAbsolutePath());
            } else {
                System.out.println("Loading valid image file from: " + imgFile.getAbsolutePath());
            }

            image = ImageIO.read(new File(path));

            int width = image.getWidth();
            int height = image.getHeight();

            double valueMin = 255;
            double valueMax = 0.0;

            for(int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int red = color.getRed();
                    if (red > valueMax) {
                        valueMax = red;
                    }
                    if (red < valueMin) {
                        valueMin = red;
                    }
                }
            }
            return new double[] {valueMin, valueMax};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new double[2];
    }

    public static String[] getMunsellHVCAndHSL(String path) {
        BufferedImage image;
        try {
            File imgFile = new File(path);
            if (!imgFile.exists()) {
                System.out.println("Image file NOT FOUND at: " + imgFile.getAbsolutePath());
            } else if (!imgFile.canRead()) {
                System.out.println("No READ perms for file at: " + imgFile.getAbsolutePath());
            } else {
                System.out.println("Loading valid image file from: " + imgFile.getAbsolutePath());
            }

            image = ImageIO.read(new File(path));

            int width = image.getWidth();
            int height = image.getHeight();

            double valueR = 0.0;
            double valueG = 0.0;
            double valueB = 0.0;

            for(int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    Color color = new Color(image.getRGB(x, y));
                    valueR += color.getRed();
                    valueG += color.getGreen();
                    valueB += color.getBlue();
                }
            }
            double R = valueR/(width*height);
            double G = valueG/(width*height);
            double B = valueB/(width*height);

            Color col = new Color((int) R, (int) G, (int) B);

            float[] HSLValues = new HSLConvertor(col).getHSL();

            R = R/256;
            G = G/256;
            B = B/256;

            String lines = null;
            String[] MunsellValues = new String[3];
            try {
                ProcessBuilder builder = new ProcessBuilder("python",
                        System.getProperty("user.dir") + "\\PythonScripts\\Munsell.py", "" + R, "" + G, "" + B);

                Process process = builder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader readers = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                System.out.println(readers.readLine());
                System.out.println(readers.readLine());
                System.out.println(readers.readLine());
                System.out.println(readers.readLine());
                while ((lines = reader.readLine()) != null) {
                    String[] lineTemp = lines.split(" ");
                    MunsellValues[0] = lineTemp[0];
                    MunsellValues[1] = lineTemp[1].split("/")[0];
                    MunsellValues[2] = lineTemp[1].split("/")[1];
                    System.out.println("lines: " + lines);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(Arrays.toString(MunsellValues));
            return new String[] {
                    MunsellValues[0],
                    MunsellValues[1],
                    MunsellValues[2],
                    "" + Math.round(HSLValues[0] * 100d) / 100d,
                    "" + Math.round(HSLValues[1] * 100d) / 100d,
                    "" + Math.round(HSLValues[2] * 100d) / 100d
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[5];
    }

    public static String[] imageUploadedAllMethods(String path, boolean munsellValues) {
        double averageRed = getAverageRedWholeImage(path);
        double[] valueMinMax = getMinMaxRedWholeImage(path);

        System.out.println("average = " + (averageRed));
        System.out.println("Max R = " + valueMinMax[1]);

        double ansMin = Method.method(valueMinMax[0]);
        if (ansMin > 10) {
            ansMin = 10;
        }
        double ansMax = Method.method(valueMinMax[1]);
        if (ansMax < 0) {
            ansMax = 0;
        }
        double ansAverage = Method.method(averageRed);
        if (ansAverage > 10) {
            ansAverage = 10;
        }

        String[] ans = {"" + ansMin, "" + ansMax, "" + ansAverage};
        if (munsellValues) {
            String[] Munsell = getMunsellHVCAndHSL(path);
            ans = ArrayUtils.addAll(ans, Munsell);
        }
        return ans;
    }

    public static String[] imageUploadedAllMethodsWithColorChecker(
            String colorCheckerPath,
            String imagePath,
            boolean munsellValues) {
        double averageRed = getAverageRedWholeImage(imagePath);
        double[] colorCheckerRed = getMinMaxRedWholeImage(colorCheckerPath);
        System.out.println("colorChecker = " + colorCheckerRed[1]);
        System.out.println("averageRed = " + averageRed);
        System.out.println("Result = " + (averageRed - (colorCheckerRed[1] - 175)));

        double ansMin = Method.method(colorCheckerRed[0]);
        if (ansMin > 10) {
            ansMin = 10;
        }
        double ansMax = Method.method(colorCheckerRed[1]);
        if (ansMax < 0) {
            ansMax = 0;
        }
        double ansAverage = Method.method(averageRed - (colorCheckerRed[1] - 175));
        if (ansAverage > 10) {
            ansAverage = 10;
        }
        String[] ans = {"" + ansMin, "" + ansMax, "" + ansAverage};

        if (munsellValues) {
            String[] Munsell = getMunsellHVCAndHSL(imagePath);
            ans = ArrayUtils.addAll(ans, Munsell);
        }
        return ans;
    }

}
