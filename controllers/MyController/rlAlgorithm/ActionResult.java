package rlAlgorithm;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 31/01/2013
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
/**
 * ActionResult.java - This class combines a <code>State</code>
 * instance and a numerical reward to form a single Object that
 * describes the results of taking a specific action in a specific
 * state.  It acts as a wrapper class so that the Environment cans
 * return both values together.
 *
 * Created: Tue Jun  4 13:43:49 2002
 *
 * @author Amy J. Kerr
 * @version 1.0
 */

public class ActionResult {

    /**
     * The resulting <code>State</code> from taking a specific
     * action from a specific state.  */
    public State nextState;

    /**
     * The numerical payoff received for taking a specific action from
     * a specific state.  */
    public double reward;

    /**
     * Creates a new <code>ActionResult</code> instance that describes
     * the resulting <code>State</code> and numerical reward for
     * taking a specific action in a specific state.
     *
     * @param nextState the resulting <code>State</code> value from
     * taking a specific action
     * @param reward a <code>double</code> value, the numerical payoff
     * for taking the action.  */
    public ActionResult (State nextState, double reward){
        this.nextState = nextState;
        this.reward = reward;
    }

}// ActionResult
