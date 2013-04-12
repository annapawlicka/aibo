package utils;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 20/12/2012
 * Time: 23:11
 * Utilities is a collection of methods that implement statistical/mathematical functions, similar to ones
 * available in MATLAB.
 */

public class Util {

    /**
     * Normal probability density function
     * Y = normpdf(X,mu,sigma) computes the pdf at each of the values in X using
     * the normal distribution with mean mu and standard deviation sigma.
     * X, mu, and sigma can be vectors, matrices, or multidimensional arrays that all have the same size.
     * @param x
     * @param mean
     * @param sd
     * @return
     */
    public static double normPdf(double x, double mean, double sd) {
        double y = (1 / (sd * Math.sqrt(2 * Math.PI))) * Math.exp(-Math.pow(x - mean, 2) / (2 * sd * sd));
        //System.out.println("normPDF: "+y);
        return y;
    }

    /**
     * Product of array elements
     * @param a
     * @return
     */
    public static double prod(double[] a) {
        double p = 1;
        for (int i = 0; i < a.length; i++)
            if (a[i] != 0)
                p *= a[i];

        return p;
    }

    /**
     * Copy one array into another
     * @param a
     * @return
     */
    public static double[] copy(double[] a) {
        double[] b = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
        }
        return b;
    }

    /* Sum of array elements */
    public static double sum(double in[]) {
        double sum = 0;
        for (int i = 0; i < in.length; i++)
            sum += in[i];

        return sum;
    }

    /**
     * Modulus after division
     * M = mod(X,Y) if Y ~= 0, returns X - n.*Y where n = floor(X./Y).
     * If Y is not an integer and the quotient X./Y is within roundoff error of an integer, then n is that integer.
     * @param x
     * @param y
     * @return
     */
    public static double mod(double x, double y)
    {
        double result = x % y;
        if (result < 0)
        {
            result += y;
        }
        return result;
    }

    /**
     * Create an empty binary string of given length
     * @param x
     * @return
     */
    public static String getEmptyBinaryString(int x){

        String str = "0";
        for(int i=0; i<x-1; i++){
           str += "0";
        }
        return str;
    }


}
