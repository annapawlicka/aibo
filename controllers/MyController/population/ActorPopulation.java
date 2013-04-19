package population;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 26/12/2012
 * Time: 21:34
 * Population of actors.
 */

public class ActorPopulation {

    private Actor[] actors;
    private int inputSize;
    private Random rand = new Random();

    // Create a population
    public ActorPopulation(int populationSize, boolean initialise, int inputSize) {

        this.inputSize = inputSize;
        // Initialise population
        if (initialise) {
            actors = new Actor[populationSize];
            // Loop and create predictors
            for (int i = 0; i < populationSize; i++) {
                Actor newActor = new Actor(inputSize);
                newActor.generateIndividual();
                actors[i] = newActor;
            }
        }
    }

    /**
     * All actors decide on their next action, based on the current sensorimotor input.
     * @param input
     * @return
     */
    public double[] decideAction(double[] input) {

		/* First determine which actors match the current sensory conditions */
        for (int p = 0; p < this.getNumberOfActors(); p++) {
            this.getIndividual(p).act(input);
        }

        double[] actions = new double[input.length];
        // Initialise the array
        for (int k = 0; k < actions.length; k++) {
            actions[k] = 0;
        }

        Actor bestActor = getBestActor();  // Choose actor with greatest fitness value

        for(int q = 0; q<bestActor.sizeToPredict; q++){
            actions[q] = bestActor.getAction(q);

        }

        return actions;
    }

    public void performTournamentSelection(double[] actualState){

        int ind1 = rand.nextInt(50);
        int ind2 = rand.nextInt(50);

        if(ind1==ind2) ind2 = rand.nextInt(50);

        // Assess G-Causality of both individuals

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
        for (int i = 0; i < this.getNumberOfActors(); i++) {
            if (this.getActors()[i].getAge() > CONVERGE_AGE_THRESHOLD &&
                    this.getActors()[i].getProbActive() < this.getActors()[i].ACTIVE_THRESHOLD &&
                    this.getActors()[i].getNumberOfActive() / this.getActors()[i].getAge() < NUM_ACTIVE_THRESHOLD) {

                System.out.println("Converging actor " + i + " with activity " + this.getActors()[i].getNumberOfActive() / this.getActors()[i].getAge());

                /* Delta rule to move condition mean towards the current input to this predictor function */
                for (int j = 0; j < this.getActors()[i].getNumberOfInputs(); j++) {
                    this.getActors()[i].addToConditionMean(j, -0.2 * (this.getActors()[i].getConditionMean(j) - input[j]));
                    this.getActors()[i].addToConditionSD(j, 0.01 * rand.nextDouble());
                }
            }
        }
    }

    /**
     * Method to overwrite worst actor.
     * Worst actor is an actor that has the smallest overall value.
     * //TODO Actor is overwritten with random individual! Change this?
     * Offspring is created by copying information from random parent, and mutating it.
     */
    public void overwriteWorstActor() {

        final int DELETE_AGE_THRESHOLD = 50;
        final int ACTORS_TO_OVERWRITE = 1;

		/* Create a sorted list by the overall value */
        List<Actor> sortedActors = Arrays.asList(this.getActors());
        Collections.sort(sortedActors, new ActorComparatorByFitnessValue());
				
		/* This will give the overall value for the worst actor from the sorted list of actors */
        final double overallValueThreshold = sortedActors.get(ACTORS_TO_OVERWRITE - 1).getFitness();
        //System.out.println("Actor at position 0:"+sortedActors.get(0).getOverallValue());
        //System.out.println("Actor at position 49:"+sortedActors.get(49).getOverallValue());


        for (int i = 0; i < actors.length; i++) {
            if (actors[i].getFitness() <= overallValueThreshold && actors[i].getAge() > DELETE_AGE_THRESHOLD) {
                int parent = rand.nextInt(actors.length);
                if (parent >= 0) {
                    //System.out.println("Overall value threshold: "+overallValueThreshold);
                    System.out.println("Overwriting actor " + i + " with overall value: "+ actors[i].getFitness()+". Replacing it with actor " + parent + " with overall value: "+ actors[parent].getFitness());
                    actors[i].copy(actors[parent]);
                    actors[i].mutate();
                }

            }
        }
    }

    public Actor getBestActor(){
        int bestActorPos = -1;

        List<Actor> sortedActors = Arrays.asList(this.getActors());
        //Collections.sort(sortedActors, new ActorComparatorByResponsibility());
        Collections.sort(sortedActors, new ActorComparatorByFitnessValue());
        System.out.println("Actor at pos 0: "+sortedActors.get(0).getFitness());
        System.out.println("Actor at pos 49: "+sortedActors.get(49).getFitness());

        List<Actor> bestActiveActors = new ArrayList<Actor>();
        for(int i=0; i<sortedActors.size(); i++){
            if(sortedActors.get(i).probabilityActive > sortedActors.get(i).ACTIVE_THRESHOLD){
                bestActiveActors.add(sortedActors.get(i));
            }
        }

        if(bestActiveActors.size() > 0){
            return bestActiveActors.get(bestActiveActors.size()-1);
        }
        else {
            return sortedActors.get(49);
        }

    }

    /* Getters and setters */

    public Actor getIndividual(int index) {
        return actors[index];
    }

    public Actor[] getActors() {
        return actors;
    }

    // Get population size
    public int getNumberOfActors() {
        return actors.length;
    }

    public int getInputSize() {
        return inputSize;
    }
}

class ActorComparatorByFitnessValue implements Comparator<Actor> {

    @Override
    public int compare(Actor a1, Actor a2) {
        return (((Double) a1.getFitness()).compareTo(a2.getFitness()));
    }

}