package population;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 03/11/2012
 * Time: 19:46
 * Population of predictors.
 */

public class PredictorPopulation {

    private Predictor[] predictors;
    private int inputSize;
    private Random rand = new Random();

    // Create a population
    public PredictorPopulation(int populationSize, boolean initialise, int inputSize) {
        this.inputSize = inputSize;
        // Initialise population
        if (initialise) {
            predictors = new Predictor[populationSize];
            // Loop and create predictors
            for (int i = 0; i < populationSize; i++) {
                Predictor newPredictor = new Predictor(inputSize);
                newPredictor.generateIndividual();
                predictors[i] = newPredictor;
            }
        }
    }


    /* Getters */
    public Predictor getIndividual(int index) {
        return predictors[index];
    }

    public Predictor[] getPredictors() {
        return predictors;
    }

    public void convergePredictorConditions(double[] input) {

        final int CONVERGE_AGE_THRESHOLD = 300;
        final double NUM_ACTIVE_THRESHOLD = 0.01;

        for (int i = 0; i < this.getNumberOfPredictors(); i++) {
            if (this.getPredictors()[i].getProbActive() < predictors[i].ACTIVE_THRESHOLD &&
                    predictors[i].getAge() > CONVERGE_AGE_THRESHOLD &&
                    predictors[i].getNumberOfActive() / predictors[i].getAge() < NUM_ACTIVE_THRESHOLD) {

                /* Delta rule to move condition mean towards the current input to this predictor function */
                for (int j = 0; j < predictors[i].getInputSize(); j++) {
                    predictors[i].addToConditionMean(j, -0.2 * (predictors[i].getConditionMean(j) - input[j]));
                    predictors[i].addToConditionSD(j, 0.01 * rand.nextDouble());
                }
            }
        }
    }

    public void overwriteWorstPredictor() {

        final int DELETE_AGE_THRESHOLD = 500;
        final int PREDICTORS_TO_OVERWRITE = 1;		/* this allows any number of actors to be overwritten - currently just one */

		/* Create a sorted list by the overall error */
        List<Predictor> sortedPredictors = Arrays.asList(predictors);
        Collections.sort(sortedPredictors, new PredictorComparatorByOverallError());
        final double overallErrorThreshold = sortedPredictors.get(PREDICTORS_TO_OVERWRITE - 1).getOverallError();


		/* This will give the overall value for least worst predictor from the sorted list of predictors */
        for (int i = 0; i < predictors.length; i++) {
            if (predictors[i].getOverallError() >= overallErrorThreshold && predictors[i].getAge() > DELETE_AGE_THRESHOLD) {

                int parent = maxSimilar(i);

                if (parent >= 0) { // copying only if there is a "different" parent
                    System.out.println("Overwriting predictor " + i + " with its parent " + parent);

                    predictors[i].copy(predictors[parent]);
                    predictors[i].mutate();
                }
            }
        }
    }

    private int maxSimilar(int n) {

        double[] worstInput = new double[this.getInputSize()];
        for (int i = 0; i < worstInput.length; i++) {
            worstInput[i] = this.getPredictors()[n].getInput(i);
            worstInput[i] = this.getPredictors()[n].getInput(i);
        }

        double[] worstOutput = new double[this.getInputSize()];
        for (int i = 0; i < worstOutput.length; i++) {
            worstOutput[i] = this.getPredictors()[n].getOutput(i);
            worstOutput[i] = this.getPredictors()[n].getOutput(i);
        }

        int[] diff = new int[predictors.length];

        for (int j = 0; j < diff.length; j++) { // for each predictor

            if (j == n) continue; // in order to ensure that the worst predictor is not overwritten by itself

            double[] thisInput = new double[this.getInputSize()];
            for (int i = 0; i < thisInput.length - 1; i++) {
                thisInput[i] = getPredictors()[j].getInput(i);
                thisInput[i] = this.getPredictors()[j].getInput(i);
            }

            double[] thisOutput = new double[this.getInputSize()];
            for (int i = 0; i < thisOutput.length; i++) {
                thisOutput[i] = this.getPredictors()[j].getOutput(i);
                thisOutput[i] = this.getPredictors()[j].getOutput(i);
            }


            for (int i = 0; i < worstInput.length; i++) {
                if (worstInput[i] == thisInput[i]) diff[j]++;
                if (worstInput[i] == thisInput[i]) diff[j]++;
            }


            for (int i = 0; i < worstOutput.length; i++) {
                if (worstOutput[i] == thisOutput[i]) diff[j]++;
                if (worstOutput[i] == thisOutput[i]) diff[j]++;
            }

        }

        int max = 0; // get the max similar
        for (int j = 0; j < diff.length; j++) {
            if (diff[j] > max) max = diff[j];
        }
        List<Integer> diff_index = new ArrayList<Integer>();
        for (int j = 0; j < diff.length; j++) {
            if (diff[j] == max) diff_index.add(j); // stores all the indexes of the most similar potential parents
        }
        if (diff_index.size() > 0) {
            int parent = diff_index.get(rand.nextInt(diff_index.size()));

            if (parent != n)
                return parent;
        }

        return -1;

    }

    /* Public methods */

    // Get population size
    public int getNumberOfPredictors() {
        return predictors.length;
    }

    public int getInputSize() {
        return inputSize;
    }
}

/**
 * This comparator will be used to sort the predictors in descending order by prob_actove
 */
class PredictorComparatorByProbActive implements Comparator<Predictor> {

    @Override
    public int compare(Predictor a1, Predictor a2) {
        return (((Double) a2.getProbActive()).compareTo(a1.getProbActive()));
    }

}

/**
 * This comparator will be used to sort the predictors in descending order by overall error
 */
class PredictorComparatorByOverallError implements Comparator<Predictor> {

    @Override
    public int compare(Predictor a1, Predictor a2) {
        return (((Double) a2.getOverallError()).compareTo(a1.getOverallError()));
    }
}

/**
 * This comparator will be used to sort the predictors in descending order by mp error
 */
class PredictorComparatorByMPError implements Comparator<Predictor> {

    @Override
    public int compare(Predictor a1, Predictor a2) {
        return (((Double) a2.getMPError()).compareTo(a1.getMPError()));
    }
}

/**
 * This comparator will be used to sort the predictors in ascending order by improvement
 */
class PredictorComparatorByImprovement implements Comparator<Predictor> {
    @Override
    public int compare(Predictor a1, Predictor a2) { // CHECK THE ORDER FIRST!!!
        return (((Double) a1.getImprovement()).compareTo(a2.getImprovement()));
    }
}

