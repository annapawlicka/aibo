package population;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 25/01/2013
 * Time: 14:08
 * Population of curiosity loops.
 */
public class LoopPopulation {

    private ArrayList<CuriosityLoop> population;
    private int populationSize;
    private int inputSize;
    private int outputSize;
    private Random rand = new Random();

    public LoopPopulation(int size, int inputSize, int outputSize, boolean initialise){
        this.populationSize = size;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        population = new ArrayList<CuriosityLoop>(populationSize);
        if(initialise) init();
    }

    private void init(){
        for(int i=0; i<populationSize; i++){
            population.add(new CuriosityLoop(inputSize, outputSize));
        }
    }

    /* Getters and setters */
    public ArrayList<CuriosityLoop> getPopulation(){
        return population;
    }

    public int getPopulationSize(){
        return populationSize;
    }

    public int getInputSize(){
        return inputSize;
    }

    public int getOutputSize(){
        return outputSize;
    }

    public int getRandomCuriosityLoop(){
        return rand.nextInt(this.getPopulationSize());
    }


    /**
     * Method to sort population of loops and retrieve the best one.
     * @return
     */
    public int getBestLoop(){

        List<CuriosityLoop> sortedLoops = this.getPopulation();
        Collections.sort(sortedLoops, new LoopComparatorByFitnessValue());

        List<CuriosityLoop> bestActiveLoops = new ArrayList<CuriosityLoop>();
        for(int i=0; i<sortedLoops.size(); i++){
            if(sortedLoops.get(i).getActor().probabilityActive > sortedLoops.get(i).getActor().ACTIVE_THRESHOLD){
                bestActiveLoops.add(sortedLoops.get(i));
            }
        }

        if(bestActiveLoops.size() > 0){
            int index = bestActiveLoops.size()-1;
            //System.out.println("Retrieved loop no: "+ index);
            //return bestActiveLoops.get(bestActiveLoops.size()-1);
            return bestActiveLoops.size()-1;


        }
        else {
            //System.out.println("Retrieved last loop");
            //return sortedLoops.get(49);
            return 49;
        }

    }

    public void increaseAge(){
        for(int i=0; i< this.getPopulation().size(); i++){
            this.getPopulation().get(i).updateAge();
        }
    }

    public void overwriteWorstActor() {

        final int DELETE_AGE_THRESHOLD = 50;
        final int LOOPS_TO_OVERWRITE = 1;

        /* Create a sorted list by the fitness value */
        List<CuriosityLoop> sortedLoops = this.getPopulation();
        Collections.sort(sortedLoops, new LoopComparatorByFitnessValue());

        /* This will give the fitness value for the worst actor from the sorted list of actors */
        final double overallValueThreshold = sortedLoops.get(LOOPS_TO_OVERWRITE - 1).getFitnessScore();

        for (int i = 0; i < this.getPopulation().size(); i++) {
            if (this.getPopulation().get(i).getFitnessScore() <= overallValueThreshold && this.getPopulation().get(i).getAge() > DELETE_AGE_THRESHOLD) {
                int parent = rand.nextInt(this.getPopulation().size());
                if (parent >= 0) {
                    //System.out.println("Overall value threshold: "+overallValueThreshold);
                    //System.out.println("Overwriting actor " + i + " with fitness score: "+ this.getPopulation().get(i).getFitnessScore()+". Replacing it with actor " + parent + " with fitness score: "+ this.getPopulation().get(parent).getFitnessScore());
                    this.getPopulation().get(i).getActor().copy(this.getPopulation().get(parent).getActor());
                    this.getPopulation().get(i).getActor().mutate();
                }

            }
        }
    }

    /**
     * Actors that are inactive and above the age threshold have their condition mean and sd
     * moved towards the current input.
     *
     * @param input
     */
    public void convergeActorConditions(double[] input) {

        final int CONVERGE_AGE_THRESHOLD = 300;
        final double NUM_ACTIVE_THRESHOLD = 0.01;

        /* If actor active less than 10% of its lifetime. */
        for (int i = 0; i < this.getPopulation().size(); i++) {
            if (this.getPopulation().get(i).getAge()> CONVERGE_AGE_THRESHOLD &&
                    this.getPopulation().get(i).getActor().getProbActive() < this.getPopulation().get(i).getActor().ACTIVE_THRESHOLD &&
                    this.getPopulation().get(i).getActor().getNumberOfActive() / this.getPopulation().get(i).getActor().getAge() < NUM_ACTIVE_THRESHOLD) {

                /* Delta rule to move condition mean towards the current input to this predictor function */
                for (int j = 0; j < this.getPopulation().get(i).getActor().getNumberOfInputs(); j++) {
                    this.getPopulation().get(i).getActor().addToConditionMean(j, -0.2 * (this.getPopulation().get(i).getActor().getConditionMean(j) - input[j]));
                    this.getPopulation().get(i).getActor().addToConditionSD(j, 0.01 * rand.nextDouble());
                }
            }
        }
    }

}



class LoopComparatorByFitnessValue implements Comparator<CuriosityLoop> {

    @Override
    public int compare(CuriosityLoop a1, CuriosityLoop a2) {
        return (((Double) a1.getFitnessScore()).compareTo(a2.getFitnessScore()));
    }

}
