package population;

import utils.*;


/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 25/12/2012
 * Time: 17:53
 * Actor represents an individual responsible for deciding actions.
 */

public class Actor extends AbstractIndividual {

    /* Data structures */
    private double[] actions;

    private double fitness;
    private double gCausality;

    public Actor(int inputSize) {

        super(inputSize);
        actions = new double[inputSize];
        fitness = 0;
        gCausality = 0;

    }

    /**
     * Method used to create a next action. It updates actions array with new motor actions.
     *
     * @param in - current sensorimotor input
     */
    public void act(double[] in) {

        /* 1. Determine whether actor's conditions match input */
        this.probabilityActive = 1;

        // Decide probability of activation
        for (int i = 0; i < this.sizeToPredict; i++) {
            this.probabilityActive = this.probabilityActive + Math.abs(this.probabilityActive * Util.normPdf(in[i], this.condition_mean[i], this.condition_sd[i]));
        }

    	/* 2. Make actions */
        if (this.probabilityActive > this.ACTIVE_THRESHOLD) {
            this.num_active++; // Increase the number of how many times this actor was active

            // Initialise the array
            for (int j = 0; j < this.actions.length; j++) {
                this.actions[j] = 0;
            }
            // Compute actions
            for (int j = 0; j < this.actions.length; j++) {
                this.actions[j] = in[j] * this.weights[j];
            }
        }
    }

    public void copy(Actor parent) {

        super.copy(parent);
        this.actions = Util.copy(parent.actions);
        gCausality = 0;
        fitness = 0;

    }

    public void mutate() {
        super.mutate();
    }

    public double getAction(int i) {
        return this.actions[i];
    }

    public double getFitness(){
        return fitness;
    }

    public void setFitness(double f){
        fitness = f;
    }

    public double getGCausality(){
        return gCausality;
    }

    public void setgCausality(double g){
        gCausality = g;
    }
}
