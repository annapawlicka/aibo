package utils;

import population.PredictorPopulation;

import java.io.BufferedWriter;

public class FilesFunctions {

    public static void writeErrors(BufferedWriter out, double err_pe, int na_hist) {
        try {
            out.write("Active predictor error: " + (err_pe) + "\n");
            out.write("Active predictors:      " + (na_hist) + "\n");
            out.write("\n");

        } catch (Exception e) {
            System.err.println("Buffer Error: " + e.getMessage());
        }
    }

    public static void writeFitness(BufferedWriter out, int fitnessScore, int timeStep) {
        try {
            //out.write("Epoch: "+ timeStep);
            out.write("Actor fitness: " + (fitnessScore) + "\n");
            out.write("\n");

        } catch (Exception e) {
            System.err.println("Buffer Error: " + e.getMessage());
        }
    }

    public static void storePredictors(BufferedWriter out, PredictorPopulation pop) {


        try {
            for (int i = 0; i < pop.getPredictors().length; i++) {
                out.write("Predictor " + i + "\n");

                double[] input = new double[pop.getInputSize()];
                for (int j = 0; j < pop.getInputSize(); j++) {
                    input[j] = pop.getPredictors()[j].inputs[j];
                }

                double[] output = new double[pop.getInputSize()];
                for (int j = 0; j < pop.getInputSize(); j++) {
                    output[j] = pop.getPredictors()[j].getPredictions()[j];
                }

                for (int j = 0; j < pop.getInputSize(); j++) {
                    out.write("Input:      " + input[j] + "\n");
                    out.write("Output:     " + output[j] + "\n");
                    out.write("Prediction: " + pop.getPredictors()[i].getPredictions()[j] + "\n");
                }

            }
        } catch (Exception e) {
            System.err.println("Buffer Error: " + e.getMessage());
        }
    }
}

