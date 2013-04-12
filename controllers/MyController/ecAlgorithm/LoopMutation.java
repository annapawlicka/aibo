package ecAlgorithm;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import population.CuriosityLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 20/01/2013
 * Time: 13:16
 * Mutation of individual attributes in a CuriosityLoop according to some
 * probability.
 */
public class LoopMutation implements EvolutionaryOperator<CuriosityLoop> {

    private final NumberGenerator<Probability> mutationProbability;

    /**
     * Creates a mutation operator that is applied with the given
     * probability.
     *
     * @param mutationProbability The probability that a given double
     *                            is changed.
     */
    public LoopMutation(Probability mutationProbability) {
        this(new ConstantGenerator<Probability>(mutationProbability));
    }

    /**
     * Creates a mutation operator that is applied with the given
     * probability.
     *
     * @param mutationProbability The (possibly variable) probability that a
     *                            given character is changed.
     */
    public LoopMutation(NumberGenerator<Probability> mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    @Override
    public List<CuriosityLoop> apply(List<CuriosityLoop> selectedCandidates, Random random) {

        List<CuriosityLoop> mutatedPopulation = new ArrayList<CuriosityLoop>(selectedCandidates.size());

        for (int j = 0; j < selectedCandidates.size(); j++) {
            if (mutationProbability.nextValue().nextEvent(random)) {

                for (int i = 0; i < selectedCandidates.get(j).getActor().condition_mean.length; i++) {
                    selectedCandidates.get(j).getActor().condition_mean[i] += 0.02 * (random.nextGaussian() * 2 - 1);
                    selectedCandidates.get(j).getActor().condition_sd[i] += 0.05 * (random.nextGaussian() * 2 - 1);
                }

                for (int k = 0; k < selectedCandidates.get(j).getActor().condition_sd.length; k++)
                    if (selectedCandidates.get(j).getActor().condition_sd[k] < 0.0001)
                        selectedCandidates.get(j).getActor().condition_sd[k] = 0.0001;

                /* Transformation */
                for (int l = 0; l < selectedCandidates.get(j).getActor().weights.length; l++) {
                    selectedCandidates.get(j).getActor().weights[l] += 0.01 * (random.nextGaussian());
                }
            }
        }

        return mutatedPopulation;
    }
}
