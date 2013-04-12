package population;

import utils.Util;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 03/11/2012
 * Time: 19:47
 * A Predictor.
 * Each predictor makes a prediction on the basis of the input only, and assigns the prediction to its internal prediction data structure.
 * Prediction calculates difference between current sensorimotor state (at time t) and next state (t+1).
 */

public class Predictor extends AbstractIndividual {

    // Predictions and errors
    private double meanSqError;                 // Current mean squared error
    private double improvementValue;            // Improvement of the predictor
    private double overallError;                // Overall error
    private double[] predictions;               // Array of predictions


    public Predictor(int inputSize) {
        super(inputSize);
    }

    // Create a random individual and initialise conditions and weights */
    public void generateIndividual() {

        super.generateIndividual();
        predictions = new double[this.getInputSize()];
        overallError = 0;
        meanSqError = 0;
        improvementValue = 0;

    }

    /* Predict next sensorimotor state */
    public void predict(double[] input) {

        for (int j = 0; j < input.length; j++) {
            inputs[j] = input[j];
        }

        this.probabilityActive = 0; //TODO Set to 1?
        double a[] = new double[inputs.length]; // Array to store values for activation

        /* Calculate how close predictor's conditions match current input */
        for (int i = 0; i < a.length; i++) {
            a[i] = this.inputs[i] * Util.normPdf(input[i], this.condition_mean[i], this.condition_sd[i]);
        }

        this.probabilityActive += Util.prod(a);

        //if (this.probabilityActive > this.ACTIVE_THRESHOLD) {
        //    this.num_active++; // Increase number of times this predictor was active
            // Predict
            for (int i = 0; i < this.predictions.length; i++) {
                this.predictions[i] += inputs[i] * this.weights[i];
            }
        //}

    }

    /* Calculate error given predicted and actual sensorimotor state */
    public double calculateError(double[] outputs) {

        double error = 0;
        int numOutputs = 0;
        for (int i = 0; i < outputs.length; i++) {
            error += Math.pow(this.predictions[i] - outputs[i], 2);
            numOutputs++;
        }
        if (numOutputs == 0) {
            System.err.println("This predictor has not predicted anything");
        }
        // Before updating the mean predictor error, store the previous mpe for the improvement function
        double previousError = this.meanSqError;
        this.meanSqError = error = Math.sqrt(error) / numOutputs;
        this.improvementValue = -this.meanSqError + previousError;

        this.overallError = this.LAMBDA * this.overallError + (1 - this.LAMBDA) * error;

        return overallError;

    }

    /* Update weights and conditions of predictions */
    public void updatePrediction(double[] input, double[] output) {

		/* Update the predictor only if it is active */
        if (this.probabilityActive > this.ACTIVE_THRESHOLD) {

            for (int i = 0; i < this.weights.length; i++) {
                this.weights[i] += 1 * (-this.predictions[i] + output[i]) * input[i];
                if (this.weights[i] > 2) this.weights[i] = 2;
                if (this.weights[i] < -2) this.weights[i] = -2;
            }

            /* Also modify the condition mean to make it more similar to the conditions that were experienced. */
            for (int i = 0; i < this.condition_mean.length; i++) {
                this.condition_mean[i] -= 0.01 * (this.condition_mean[i] - input[i]);
            }
        }
    }

    /* Getters and setters */

    public void setProbabilityActive(int active) {
        probabilityActive = active;
    }

    public double[] getPredictions() {
        return predictions;
    }

    public void addPrediction(float p, int index) {
        predictions[index] = p;
    }

    public int getInputSize() {
        return sizeToPredict;
    }

    public double[] getOutputs() {
        return outputs;
    }

    public int getAge() {
        return age;
    }

    public double getError() {
        return overallError;
    }

    public void setAge(int n) {
        age = n;
    }

    public double getProbabilityActive() {
        return probabilityActive;
    }

    public void increaseAge() {
        this.age++;
    }

    public void addToConditionMean(int i, double a) {
        this.condition_mean[i] += a;
    }

    public double getConditionMean(int i) {
        return this.condition_mean[i];
    }

    public void setOverallError(double e) {
        this.overallError = e;
    }

    public double getOverallError() {
        return this.overallError;
    }

    public double getMPError() {
        return this.meanSqError;
    }

    public double getImprovement() {
        return this.improvementValue;
    }

}