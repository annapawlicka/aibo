package population;

import utils.Util;


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

    private boolean isActive;

    public Actor(int inputSize) {

        super(inputSize);
        actions = new double[inputSize];
        isActive = false;

    }

    /**
     * Method used to create a next action. It updates actions array with new motor actions.
     *
     * @param in - current sensorimotor input
     */
    public void act(double[] in, int outputSize) {

        /* 1. Determine whether actor's conditions match input */
        this.probabilityActive = 1;

        // Decide probability of activation and reset isActive to false
        for (int i = 0; i < this.sizeToPredict; i++) {
            this.probabilityActive = this.probabilityActive + Math.abs(this.probabilityActive * Util.normPdf(in[i], this.condition_mean[i], this.condition_sd[i]));
            isActive = false;
        }

    	/* 2. Make actions */
        if (this.probabilityActive > this.ACTIVE_THRESHOLD) {
            this.num_active++; // Increase the number of how many times this actor was active
            isActive = true;

            // Initialise the array
            /*for (int j = 0; j < this.actions.length; j++) {
                this.actions[j] = 0;
            }*/
            // Compute actions
            int motor;
            for(int j=0; j<outputSize; j++){

                motor = rand.nextInt(actions.length); // Chooses random motor
                //motor = 0;//rand.nextInt(actions.length);
                this.actions[motor] = (in[motor] * this.weights[motor]*-1);
                //this.actions[motor] = this.actions[motor]+ (in[motor] * this.weights[motor]);

                //motor = 1;//rand.nextInt(actions.length);
                //this.actions[motor] = this.actions[motor]+ (in[motor] * this.weights[motor]);
                //motor = 2;//rand.nextInt(actions.length);
                //this.actions[motor] = this.actions[motor]+ (in[motor] * this.weights[motor]);

            }

            /*for (int j = 0; j < this.actions.length; j++) {
                this.actions[j] = in[j] * this.weights[j];
            }*/
        }
    }



    public void copy(Actor parent) {

        super.copy(parent);
        this.actions = Util.copy(parent.actions);

    }

    public void mutate() {
        super.mutate();
    }

    public double getAction(int i) {
        return this.actions[i];
    }

    public double[] getActions(){
        return this.actions;
    }

    public boolean getIsActive(){
        return isActive;
    }
}

