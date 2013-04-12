package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class StringUtil {

    public static String arrayToString(double[] a) {
        StringBuilder b = new StringBuilder();
        b.append("[ ");
        for (int i = 0; i < a.length; i++) {
            b.append(a[i] + " ");
        }
        b.append("]");
        return b.toString();
    }

    public static String arrayToString(double[][] a) {
        StringBuilder b = new StringBuilder();
        b.append("[ ");
        for (int i = 0; i < a.length; i++) {
            b.append("[ ");
            for (int j = 0; j < a[i].length; j++) {
                b.append(a[i][j] + " ");
            }
            if (i != a.length - 1) b.append("]\n");
        }
        b.append("]");
        return b.toString();
    }

    public static String arrayToString(int[] a) {
        StringBuilder b = new StringBuilder();
        b.append("[ ");
        for (int i = 0; i < a.length; i++) {
            b.append(a[i] + " ");
        }
        b.append("]");
        return b.toString();
    }

    public static String arrayToString(int[][] a) {
        StringBuilder b = new StringBuilder();
        b.append("[ ");
        for (int i = 0; i < a.length; i++) {
            b.append("[ ");
            for (int j = 0; j < a[i].length; j++) {
                b.append(a[i][j] + " ");
            }
            if (i != a.length - 1) b.append("]\n");
        }
        b.append("]");
        return b.toString();
    }

    public static void arrayToFile(FileWriter f, double[] a, int epoch, int ts) {
        try {
            BufferedWriter out = new BufferedWriter(f);
            out.write("Epoch " + epoch + ", time step " + ts + ".\n");
            out.write("[ ");
            for (int i = 0; i < a.length; i++) {
                out.write(a[i] + " ");
            }
            out.write("]\n");
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void arrayToFile(FileWriter f, double[][] a, int epoch, int ts) {
        try {
            // Create file
            BufferedWriter out = new BufferedWriter(f);
            out.write("Epoch " + epoch + ", time step " + ts + ".\n");
            for (int i = 0; i < a.length; i++) {
                out.write("[ ");
                for (int j = 0; j < a[i].length; j++) {
                    out.write(a[i][j] + " ");
                }
                out.write("]");
            }
            out.write("]");
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
