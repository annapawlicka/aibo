package ecAlgorithm;

import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;
import population.CuriosityLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 20/01/2013
 * Time: 12:40
 * Variable-point (fixed or random) cross-over for Curiosity loop candidates.
 */
public class LoopCrossover extends AbstractCrossover<CuriosityLoop> {

    public LoopCrossover(int crossoverPoints) {
        super(crossoverPoints);
    }

    public LoopCrossover(int crossoverPoints, Probability crossoverProbability) {
        super(crossoverPoints, crossoverProbability);
    }

    public LoopCrossover(NumberGenerator<Integer> crossoverPointsVariable) {
        super(crossoverPointsVariable);
    }

    public LoopCrossover(NumberGenerator<Integer> crossoverPointsVariable, NumberGenerator<Probability> crossoverProbabilityVariable) {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }

    @Override
    protected List<CuriosityLoop> mate(CuriosityLoop parent1, CuriosityLoop parent2, int numberOfCrossoverPoints, Random rng) {

        if (parent1.getInputSize() != parent2.getInputSize() || parent1.getOutputSize() != parent2.getOutputSize())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }

        CuriosityLoop offspring1 = parent1;
        CuriosityLoop offspring2 = parent2;
        // Apply as many cross-overs as required.
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1.getInputSize() - 1));
            for (int j = 0; j < crossoverIndex; j++)
            {
                double temp1a = offspring1.getActor().condition_mean[j];
                offspring1.getActor().condition_mean[j] = offspring2.getActor().condition_mean[j];
                offspring2.getActor().condition_mean[j] = temp1a;

                double temp1b = offspring1.getPredictor().condition_mean[j];
                offspring1.getPredictor().condition_mean[j] = offspring2.getPredictor().condition_mean[j];
                offspring2.getPredictor().condition_mean[j] = temp1b;

                double temp2a = offspring1.getActor().condition_sd[j];
                offspring1.getActor().condition_sd[j] = offspring2.getActor().condition_sd[j];
                offspring2.getActor().condition_sd[j] = temp2a;

                double temp2b = offspring1.getPredictor().condition_sd[j];
                offspring1.getPredictor().condition_sd[j] = offspring2.getPredictor().condition_sd[j];
                offspring2.getPredictor().condition_sd[j] = temp2b;

                double temp3a = offspring1.getActor().weights[j];
                offspring1.getActor().weights[j] = offspring2.getActor().weights[j];
                offspring2.getActor().weights[j] = temp3a;

                double temp3b = offspring1.getPredictor().weights[j];
                offspring1.getPredictor().weights[j] = offspring2.getPredictor().weights[j];
                offspring2.getPredictor().weights[j] = temp3b;
            }
        }

        offspring1.getActor().deleted = offspring2.getActor().deleted = 0;
        offspring1.getActor().age = offspring2.getActor().age = 0;
        offspring1.getActor().num_active = offspring2.getActor().num_active = 0;

        offspring1.getPredictor().deleted = offspring2.getPredictor().deleted = 0;
        offspring1.getPredictor().age = offspring2.getPredictor().age = 0;
        offspring1.getPredictor().num_active = offspring2.getPredictor().num_active = 0;

        List<CuriosityLoop> result = new ArrayList<CuriosityLoop>(2);
        result.add(offspring1);
        result.add(offspring2);

        return result;
    }
}
