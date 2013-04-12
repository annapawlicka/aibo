package ecAlgorithm;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 05/01/2013
 * Time: 12:53
 * Fitness Function - Granger Causality.
 * Fitness score is the value of g. Fitness score is a natural number - the higher the
 * score the fitter the individual.
 *
 * This fitness function is redundant now.
 */

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;

public class GrangerFitnessFunction {




    /**
     * Returns p-value for Granger causality test.
     *
     * @param y - predictable variable (motor actions - output)
     * @param x - predictor (sensorimotor input)
     * @param L - lag, should be 1 or greater.
     * @return p-value of Granger causality
     */
    public static double getGrangerCausality(double[] y, double[] x, int L) {

        OLSMultipleLinearRegression h0 = new OLSMultipleLinearRegression();
        OLSMultipleLinearRegression h1 = new OLSMultipleLinearRegression();

        double[][] laggedY = createLaggedSide(L, y);

        double[][] laggedXY = createLaggedSide(L, x, y);

        int n = laggedY.length;

        h0.newSampleData(strip(L, y), laggedY);
        h1.newSampleData(strip(L, y), laggedXY);

        double rs0[] = h0.estimateResiduals();
        double rs1[] = h1.estimateResiduals();


        double RSS0 = sqrSum(rs0);
        double RSS1 = sqrSum(rs1);

        double ftest = ((RSS0 - RSS1) / L) / (RSS1 / (n - 2 * L - 1));

        System.out.println(RSS0 + " " + RSS1);
        System.out.println("F-test " + ftest);

        FDistribution fDist = new FDistributionImpl(L, n - 2 * L - 1);
        try {
            double pValue = 1.0 - fDist.cumulativeProbability(ftest);
            System.out.println("P-value " + pValue);
            return pValue;
        } catch (MathException e) {
            throw new RuntimeException(e);
        }

    }

    private static double[][] createLaggedSide(int L, double[]... a) {
        int n = a[0].length - L;
        double[][] res = new double[n][L * a.length + 1];
        for (int i = 0; i < a.length; i++) {
            double[] ai = a[i];
            for (int l = 0; l < L; l++) {
                for (int j = 0; j < n; j++) {
                    res[j][i * L + l] = ai[l + j];
                }
            }
        }
        for (int i = 0; i < n; i++) {
            res[i][L * a.length] = 1;
        }
        return res;
    }

    public static double sqrSum(double[] a) {
        double res = 0;
        for (double v : a) {
            res += v * v;
        }
        return res;
    }


    public static double[] strip(int l, double[] a) {

        double[] res = new double[a.length - l];
        System.arraycopy(a, l, res, 0, res.length);
        return res;
    }

}
