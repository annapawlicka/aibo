package ecAlgorithm;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import population.Actor;
import population.CuriosityLoop;
import population.Predictor;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 20/01/2013
 * Time: 12:07
 * Every evolutionary simulation must start with an initial population of candidate solutions and the CandidateFactory
 * interface is the mechanism by which the evolution engine creates this population.
 */

public class LoopCandidateFactory extends AbstractCandidateFactory {

    private int inputSize;
    private int outputSize;

    public LoopCandidateFactory(int inputSize, int outputSize){
        this.inputSize = inputSize;
        this.outputSize = outputSize;
    }

    @Override
    public Object generateRandomCandidate(Random random) {
        // Generate actor
        Actor actor = new Actor(inputSize);
        actor.generateIndividual();
        // Generate predictor
        Predictor predictor = new Predictor(inputSize);
        predictor.generateIndividual();
        // Create new curiosity loop and add it to array
        return new CuriosityLoop(inputSize, outputSize);
    }
}
