package ecAlgorithm;

import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Servo;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import population.CuriosityLoop;
import utils.FilesFunctions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 20/01/2013
 * Time: 15:04
 * Fitness function to evaluate curiosity loops.
 */

public class LoopEvaluator implements FitnessEvaluator<CuriosityLoop> {

    Servo[] sensorimotors;
    DistanceSensor distanceSensor;

    int inputSize;
    int outputSize;
    int populationSize;

    double[] input;
    double[] motorActions;
    double[] actualState;

    double[] errors;

    int noOfActivePredictors = 0;       // Number of active predictors
    double activePredictorError = 0;    // Current predictor error

    BufferedWriter out2;

    public LoopEvaluator(Servo[] joints, DistanceSensor d, int inputSize, int outputSize, int popSize) {

        this.sensorimotors = joints;
        this.distanceSensor = d;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.populationSize = popSize;

        input = new double[inputSize];
        motorActions = new double[outputSize];
        actualState = new double[inputSize];

        errors = new double[populationSize];

        FileWriter errorFile = null;
        try {
            errorFile = new FileWriter("errors.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
        out2 = new BufferedWriter(errorFile);

        initialise();
    }

    /* Method to initialise arrays */
    private void initialise() {

        for (double i : input)
            i = 0;

        for (double i : actualState)
            i = 0;

        for (double i : motorActions)
            i = 0;

        for (double i : errors)
            i = 0;
    }

    @Override
    public double getFitness(CuriosityLoop candidate, List<? extends CuriosityLoop> population) {

        /* Get current sensorimotor readings */
        for (int i = 0; i < inputSize; i++) {
            input[i] = sensorimotors[i].getPosition();
        }

        //System.out.println("Distance sensor: "+ distanceSensor.getValue());

        /* All actors decide their actions and predictors make their predictions */
        for (int j = 0; j < population.size(); j++) {
            population.get(j).getActor().act(input);
            population.get(j).getPredictor().predict(input);
        }

        /* Update motor actions with decisions made by active actors */
        // TODO Choose which actions to perform first! Need to determine the fittest actor. Should probably move this
        // outside of the getFitness function.
        for (int k = 0; k < motorActions.length; k++) {

            //motorActions[k] = actions[k] / loopPopulation.getNoOfActiveLoops();
        }

        /* Send actions to motors */
        for (int k = 0; k < motorActions.length; k++) {
            sensorimotors[k].setPosition(motorActions[k]);
        }

        /* Get current sensorimotor readings */
        for (int i = 0; i < inputSize; i++) {
            actualState[i] = sensorimotors[i].getPosition();
        }

        /* Update Predictors */
        for (int l = 0; l < population.size(); l++) {
            // Calculate error of that predictor
            errors[l] = population.get(l).getPredictor().calculateError(actualState);
            if (population.get(l).getPredictor().getProbabilityActive() > population.get(l).getPredictor().ACTIVE_THRESHOLD) {
                activePredictorError += errors[l];
                // Increase number of active predictors
                noOfActivePredictors++;
            }
            // Update weights and conditions of Predictor
            population.get(l).getPredictor().updatePrediction(input, actualState);
            // Increase age of Predictor
            population.get(l).getPredictor().increaseAge();
        }

        /* Update Actors */
        for (int a = 0; a < population.size(); a++) {
            // Increase age
            population.get(a).getActor().increaseAge();
        }

        double meanActivePredictorError = activePredictorError / noOfActivePredictors;
        FilesFunctions.writeErrors(out2, meanActivePredictorError, noOfActivePredictors);


        return 0;
    }


    /*
    Natural fitness function is one that returns higher values for fitter individuals.
     */
    @Override
    public boolean isNatural() {

        return true;
    }
}
