/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 25/12/2012
 * Time: 17:53
 * Controller to control Aibo robot simulation.
 *
 * The position field of Servo represents the current angle difference (in radians) with
 * respect to the initial rotation of the Servo, e.g. For example if we have a "rotational"
 * Servo and the value of the position field is 1.5708, this means that this Servo is 90 degrees
 * from its initial rotation.
 */

import population.ActorPopulation;
import population.PredictorPopulation;
import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.Display;
import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.Servo;
import utils.FilesFunctions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class MyController extends Robot {

    /* Populations */
    PredictorPopulation predictorPopulation;
    ActorPopulation actorPopulation;

    /* Data structures */
    double[] errors;
    double[] input;
    double[] output;
    Servo[] joints;
    double[] motors;               // Array of doubles that stores motor actions

    int timeStep;

    BufferedWriter out1;
    BufferedWriter out2;

    Random rand = new Random();

    /* Camera - Aibo's sight */
    Camera camera;

    /* Graph */
    Display display;


    public MyController() {

        // call the Robot constructor
        super();

        joints = new Servo[12];
        predictorPopulation = new PredictorPopulation(50, true, 12);
        actorPopulation = new ActorPopulation(50, true, 12);
        errors = new double[50];
        output = new double[12];
        timeStep = 0;

        FileWriter errorFile = null;
        FileWriter predictionFile = null;
        try {
            errorFile = new FileWriter("errors.txt");
            predictionFile = new FileWriter("predictions.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
        out1 = new BufferedWriter(predictionFile);
        out2 = new BufferedWriter(errorFile);

    }

    public void run() {

        // Main loop:
        // Perform simulation steps of 64 milliseconds
        // and leave the loop when the simulation is over
        while (step(64) != -1) {

            int noOfActivePredictors = 0;       // Number of active predictors
            double activePredictorError = 0;    // Current predictor error

            // Actual sensorimotor state
            double[] actualState = new double[12];
            for (int w = 0; w < actualState.length; w++) {
                actualState[w] = joints[w].getPosition();
            }

            // Actors decide their actions
            double[] overallAction = actorPopulation.decideAction(actualState);

            if (timeStep % 2 == 0) {
                //TODO 1. Get two random individuals 2. Get the fittest 3. Crossover and mutate
                //actorPopulation.convergeActorConditions(actualState);
                //actorPopulation.overwriteWorstActor();
            }

            // Update motor actions with decisions made by actors
            for (int k = 0; k < motors.length; k++) {
                motors[k] = overallAction[k];
            }

                /* Update Predictors */

            for (int z = 0; z < predictorPopulation.getPredictors().length; z++) {
                // Calculate error of that predictor
                errors[z] = predictorPopulation.getPredictors()[z].calculateError(actualState);

                if (predictorPopulation.getPredictors()[z].getProbabilityActive() > predictorPopulation.getPredictors()[z].ACTIVE_THRESHOLD) {
                    activePredictorError += errors[z];
                    // Increase number of active predictors
                    noOfActivePredictors++;
                }

                // Update weights and conditions of Predictors
                predictorPopulation.getPredictors()[z].updatePrediction(input, actualState);
                // Increase age of Predictors
                predictorPopulation.getPredictors()[z].increaseAge();
            }

            double meanActivePredictorError = activePredictorError / noOfActivePredictors;
            FilesFunctions.writeErrors(out2, meanActivePredictorError, noOfActivePredictors, timeStep);

                /* Update Actors */
            for (int a = 0; a < actorPopulation.getNumberOfActors(); a++) {
                // Increase age
                this.actorPopulation.getActors()[a].increaseAge();
            }

            if (timeStep % 2 == 0) {
                //TODO 1. Get two random individuals 2. Get the fittest 3. Crossover and mutate
                //predictorPopulation.convergePredictorConditions(actualState);
                //predictorPopulation.overwriteWorstPredictor();
            }
        }

        // Get current sensorimotor state into an array
        input = new double[12];
        for (int m = 0; m < input.length; m++) {
            input[m] = joints[m].getPosition();
        }

        // Predict
        for (int i = 0; i < predictorPopulation.getPredictors().length; i++) {
            predictorPopulation.getPredictors()[i].predict(input);
        }

        // Make action (initial motor actions are set to 0, hence the first move made by Aibo is to straighten its joints)
        for (int z = 0; z < motors.length; z++) {
            joints[z].setPosition(motors[z]);
        }

            /* Every 10 time steps add pixel to a graph */
        if (timeStep % 10 == 0) {
            plot_fitness(timeStep);
        }
        timeStep++;

    }

    public void initialise() {

        timeStep = 0;

        /* Initialise joints */
        joints[0] = getServo("PRM:/r2/c1-Joint2:21");
        joints[1] = getServo("PRM:/r2/c1/c2-Joint2:22");
        joints[2] = getServo("PRM:/r2/c1/c2/c3-Joint2:23");
        joints[3] = getServo("PRM:/r4/c1-Joint2:41");
        joints[4] = getServo("PRM:/r4/c1/c2-Joint2:42");
        joints[5] = getServo("PRM:/r4/c1/c2/c3-Joint2:43");
        joints[6] = getServo("PRM:/r3/c1-Joint2:31");
        joints[7] = getServo("PRM:/r3/c1/c2-Joint2:32");
        joints[8] = getServo("PRM:/r3/c1/c2/c3-Joint2:33");
        joints[9] = getServo("PRM:/r5/c1-Joint2:51");
        joints[10] = getServo("PRM:/r5/c1/c2-Joint2:52");
        joints[11] = getServo("PRM:/r5/c1/c2/c3-Joint2:53");

        /* Initialise camera */
        camera = getCamera("PRM:/r1/c1/c2/c3/i1-FbkImageSensor:F1");
        camera.enable(64);

        /* Initialise 2D display */
        display = getDisplay("graph_display");
        //display.setOpacity(0.0);
        display.setAlpha(0.7);
        display.drawLine(10, 10, 10, 290); // Y
        display.drawLine(10, 290, 290, 290); // X
        display.drawText("Overall value", 2, 2);

        /* Initialise motor actions array */
        motors = new double[12];
        for (int i = 0; i < motors.length; i++) {
            motors[i] = 0;
        }

        /* Initialise output array */
        for (int l = 0; l < output.length; l++) {
            double value = (double) Math.round(rand.nextDouble() * 10000) / 10000;
            output[l] = -2.4 + (2.4 - (-2.4)) * value;
        }

        /* Get current sensorimotor state into an array */
        input = new double[12];
        for (int m = 0; m < input.length; m++) {
            input[m] = joints[m].getPosition();
        }

        /* Enable joints */
        for (int i = 0; i < joints.length; i++) {
            joints[i].enablePosition(64);
        }
        System.out.println("Init completed.");
    }

    public void plot_fitness(int counter) {

        display.setColor(0xff0000); // red
        display.drawPixel(20, counter);

    }


    public static void main(String[] args) {

        MyController controller = new MyController();
        controller.initialise();
        controller.run();
    }
}