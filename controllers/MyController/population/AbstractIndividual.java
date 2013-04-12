package population;

import utils.Util;

import java.util.Random;


/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 25/12/2012
 * Time: 17:54
 * AbstractIndividual is a class representing an individual (can be an actor or a predictor).
 */

public class AbstractIndividual {

    // Attributes
    public int sizeToPredict;                  // Number of outputs to be predicted
    public int age;
    public double probabilityActive;
    public int num_active = 0;

    // Conditions of activation
    public double[] condition_mean;
    public double[] condition_sd;
    public double[] weights;
    //public double[] conditions;                 // Array of optimal conditions that activate this predictor

    public double[] inputs;                    // Readings from sensors - inputs
    public double[] outputs;                   // Sensorimotor output

    public Random rand = new Random();
    public final double LAMBDA = 0.999;  /* 0.99 */
    public final double ACTIVE_THRESHOLD = 0.01; /* 0.01 */

    public double deleted = 0;

    public AbstractIndividual(int inputSize) {
        this.sizeToPredict = inputSize;
        outputs = new double[inputSize];
        inputs = new double[inputSize];
        //conditions = new double[inputSize];
        weights = new double[inputSize];
        age = 0;
        probabilityActive = 0;
    }

    /**
     * Create a random individual and initialise conditions and weights
     */
    public void generateIndividual() {

        // Initialise arrays
        for (int i = 0; i < sizeToPredict; i++) {
            this.outputs[i] = 0;
            this.inputs[i] = 0;
        }

        /* Conditions */
        this.condition_mean = new double[sizeToPredict];
        this.condition_sd = new double[sizeToPredict];
        for (int i = 0; i < this.condition_mean.length; i++) {
            this.condition_mean[i] = rand.nextDouble();
            this.condition_sd[i] = rand.nextDouble();
        }

        /* Transformation  - weights */
        this.weights = new double[sizeToPredict];
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = rand.nextDouble();
        }

    }


    /**
     * Method to create an offspring
     *
     * @param parent - parent whose information is being copied over to offspring
     */
    public void copy(AbstractIndividual parent) {

        this.deleted = 0;
        this.age = 0;
        this.num_active = 0;
        this.sizeToPredict = parent.sizeToPredict;
        this.sizeToPredict = parent.sizeToPredict;

        this.inputs = Util.copy(parent.inputs);
        this.outputs = Util.copy(parent.outputs);


    	/* Conditions */
        this.condition_mean = parent.condition_mean;
        this.condition_sd = parent.condition_sd;

    	/* Transformation */
        this.weights = Util.copy(parent.weights);

    }

    /**
     * Method to mutate an individual
     */
    public void mutate() {
        for (int i = 0; i < this.condition_mean.length; i++) {
            this.condition_mean[i] += 0.02 * (rand.nextGaussian() * 2 - 1);
            this.condition_sd[i] += 0.05 * (rand.nextGaussian() * 2 - 1);
        }

        for (int i = 0; i < this.condition_sd.length; i++)
            if (this.condition_sd[i] < 0.0001)
                this.condition_sd[i] = 0.0001;


    	/* Bit flip inputs */
        if (rand.nextDouble() < 0.1) {
            int f = rand.nextInt(this.sizeToPredict);
            this.inputs[f] = 1 - this.inputs[f];

            while (Util.sum(this.inputs) == 0) {
                this.inputs[f] = 1 - this.inputs[f];
            }
        }

        if (rand.nextDouble() < 0.1) {
            int f = rand.nextInt(this.sizeToPredict);
            this.outputs[f] = 1 - this.outputs[f];
            while (Util.sum(this.outputs) == 0) {
                this.outputs[f] = 1 - this.outputs[f];
            }
        }


    	/* Transformation */
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] += 0.01 * (rand.nextGaussian());
        }
    }

    /* Getters and setters */


    public double getProbActive() {
        return this.probabilityActive;
    }

    public void setProbActive(double p) {
        this.probabilityActive = p;
    }

    public int getNumberOfActive() {
        return this.num_active;
    }

    public int getAge() {
        return this.age;
    }

    /**
     * It will return the number active inputs, including the bias
     *
     * @return
     */
    public int getNumberOfInputs() {
        return this.inputs.length;
    }

    public int getNumberOfOutputs() {
        return this.outputs.length;
    }

    public double getInput(int i) {
        return this.inputs[i];
    }

    public double getOutput(int i) {
        return this.outputs[i];
    }

    public double getConditionMean(int i) {
        return this.condition_mean[i];
    }

    public void addToConditionMean(int i, double a) {
        this.condition_mean[i] += a;
    }

    public double getConditionSD(int i) {
        return this.condition_sd[i];
    }

    public void addToConditionSD(int i, double a) {
        this.condition_sd[i] += a;
    }

    public void increaseAge() {
        this.age++;
    }

}
