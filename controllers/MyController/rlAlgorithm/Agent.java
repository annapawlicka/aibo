package rlAlgorithm;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 31/01/2013
 * Time: 23:24
 * Agent.java
 * An abstract class for implementing all agents.  An agent
 * is an entity that interacts with the environment, receiving
 * states and selecting actions.  The agent may or may not learn,
 * may or may not build a model of the environment, etc.  Specific
 * agents are instances of subclasses of <code>Agent</code>.
 */

public abstract class Agent {


    /** A <code>Simulation</code> instance used by the agent to
     * access the simulation and the corresponding environment. This
     * instance is initialized for a specific <code>Agent</code>
     * instance in the <code>Simulation</code> class constructor.  */
    public Simulation sim;

    /**
     * Creates a new <code>Agent</code> instance; an agent interacts
     * with the environment, receiving states & selecting actions.  */
    public Agent (){
    }


    /**
     * A user-defined method to initialize an agent & construct any
     * necessary data structures.  If the agent learns or changes in
     * any way with experience, this method should reset it to its
     * original, naive condition.  Normally, this method is called
     * once when the simulation is first assembled and initialized.
     * This class' <code>Simulation</code> instance, sim, and its
     * corresponding environment are both guaranteed to be "inited" by
     * the time an Agent's init method is called.
     *
     * @param a a <code>Object[]</code> value */
    public abstract void init( Object[] a );

    /**
     * A user-defined method to perform any necessary initialization
     * of the agent to prepare it for the beginning of a new
     * trial. The method creates and returns the first
     * <code>Action</code> of the new trial in response to the initial
     * <code>State</code> state.
     *
     * @param state a <code>State</code>, the first
     * <code>State</code> of the new trial. Should not be altered
     * within method.
     * @return the first <code>Action</code> of the agent in the new trial */
    public abstract Action startTrial( State state );



    /**
     * The user-defined method where all learning takes place for an
     * agent; the method is called once by the <code>Simulation</code>
     * instance on each step of the simulation.
     *
     * The method checks for nextState.isTerminal(), which indicates
     * that the trial terminates with this step, and adjusts the
     * agent's learning and other processes accordingly.  In that
     * case, the value returned from this method call is ignored.
     *
     * @param nextState, the resulting <code>State</code> from taking
     * the previous action; nextState.isTerminal() is true if the
     * trial terminates with this step.
     * @param reward a <code>double</code> value, the reward
     * received for the previous action.
     * @return the next <code>Action</code> to be taken in response to
     * <code>State</code> nextState
     */
    public abstract Action step(State nextState, double reward);

}// Agent
