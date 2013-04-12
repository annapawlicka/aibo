package rlAlgorithm;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 31/01/2013
 * Time: 23:28
 * To change this template use File | Settings | File Templates.
 */
/**
 * Environment.java
 * An abstract class for implementing all environments.  An
 * environment defines the problem to be solved.  It determines
 * the dynamics of the environment, the rewards, and the
 * trial terminations.
 *
 *
 * Created: Fri May 31 07:57:05 2002
 *
 * @author Amy J. Kerr
 * @version 1.0
 */
public abstract class Environment {

    /** A <code>Simulation</code> instance used by the environment to
     *  access the simulation.  This instance is initialized for an
     *  <code>Environment</code> instance in the
     *  <code>Simulation</code> class constructor.  */
    public Simulation sim;


    /**
     * Creates a new <code>Environment</code> instance; the
     * environment defines the problem to be solved.  It determines
     * the dynamics of the environment, the rewards, and the trial
     * terminations.
     */
    public Environment (){
    }

    /**
     * This user-defined method initializes an
     * <code>Environment</code> instance, making any needed
     * data-structures.  If the environment changes in any way with
     * experience, then this function should reset it to its original,
     * naive condition.  Normally, this method is called once when the
     * simulation is first assembled and initialized.  The agent
     * corresponding to this class' <code>Simulation</code> instance,
     * sim, is not guaranteed to be initialized when this method is
     * called.
     *
     * @param a a <code>Object[]</code> value */
    public abstract void init( Object[] a );


    /**
     * A user-defined method to perform any needed initialization of
     * the environment to prepare it for the beginning of a new trial.
     * Creates and returns the first <code>State</code> of the new
     * trial.
     *
     * @return the first <code>State</code> of the new trial
     */
    public abstract State startTrial();


    /**
     * The user-defined method that causes the environment to undergo
     * a transition from its current state to a next state dependent
     * on the <Action> act.  The method returns an
     * <code>ActionResult</code> instance describing the numerical
     * payoff and the next <code>State</code> after taking
     * <code>Action</code> act in the current state; if the terminal
     * state has been reached, then the next <code>State</code>
     * instance should be null.  The method is called once by the
     * simulation instance on each step of the simulation.
     *
     * @param act the <code>Action</code> taken in the current
     * state.
     * @return an <code>ActionResult</code> instance describing the
     * resulting <code>State</code> and reward for taking
     * <code>Action</code> act.  */
    public abstract ActionResult step( Action act );


}// Environment
