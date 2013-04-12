package rlAlgorithm;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 31/01/2013
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
import java.util.Random;

/**
 * This abstract class implements some of an agent's methods for
 * learning the optimal policy for a reinforcement learning problem
 * using a temporal-difference control alogorithm (possibly with
 * eligibility traces) and an arbitrary memory model.
 * The agent seeks to learn the optimal policy by approximating the
 * optimal action-value function, Q* (an action-value function is a
 * mapping of state-action pairs to their values).  For all methods
 * except getAction(), if a subclass of TDAgent.java defines a method
 * with the same signature, the subclass method MUST call TDAgent's
 * instance of the method, too.  In the following comments, Q(state, act)
 * denotes the agent's current APPROXIMATION of the value of the
 * state-action pair (state,act).
 *
 * Created: Fri May 31 07:03:02 2002
 *
 * @author Amy J. Kerr, Todd W. Neller, Christopher J. La Pilla
 * @version 1.1 */

public abstract class TDAgent extends Agent{

    /**
     * A random number generator.  */
    public static Random rng;

    /**
     * The probability that the agent "explores" by choosing a random
     * action from the current state (as opposed to "exploiting" its
     * current knowledge and greedily selecting the state of highest
     * (estimated) value). */
    public double epsilon;

    /**
     * The learning rate for a TDAgent. */
    public double alpha;

    /**
     * The discount rate for delayed rewards; a number in [0,1] used
     * to quantify the current value of future rewards. */
    public double gamma;

    /**
     * The trace-decay parameter (ie TD(lambda)); a number in [0,1].
     * If lambda == 0, eligibility traces are not used. */
    public double lambda;


    /* The following class variables are necessary for learning (ie
       value updates).  These variables are necessary because an agent
       cannot typically learn about an action (ie state-action pair)
       until AFTER it takes the action, receives the reward and next
       state for that action, AND selects its NEXT action.  */

    /**
     * The previous state experienced by the TDAgent. This variable is
     * initialized by the startTrial() method and updated by the step()
     * method; it should not be altered in any other contexts.  */
    protected State prevState;

    /**
     * The previous action taken by the TDAgent in response to
     * prevState. This variable is initialized by the startTrial() method
     * and updated by the step() method; it should not be altered
     * in any other contexts.  */
    protected Action prevAction;

    /**
     * Whether the previous action taken was optimal or exploratory. */
    protected boolean optimalAction;


    /**
     * Creates a new <code>TDAgent</code> instance, an agent that
     * learns using a TD(lambda) based control algorithm.
     *
     * @param epsilon a <code>double</code> value, the probability
     * that an agent explores.
     * @param alpha a <code>double</code> value, the learning rate of
     * the agent.
     * @param gamma a <code>double</code> value, the discount rate for
     * learning.
     * @param lambda a <code>double</code> value, the trace-decay
     * parameter. */
    public TDAgent(double epsilon, double alpha, double gamma, double lambda){
        this.epsilon = epsilon;
        this.alpha   = alpha;
        this.gamma   = gamma;
        this.lambda  = lambda;

        rng = new Random();
    }


    /**
     * Initializes an agent by initializing/resetting the agent's
     * approximation of Q and the eligibility trace values (if lambda
     * > 0).  A user should "override" this method (in a subclass) to
     * construct the necessary data structures for learning and to
     * further initialize/reset the agent to its naive condition if
     * necessary; the user-defined instance of this method MUST call
     * super.init() after constructing the data structure(s) for the
     * Q-estimates and eligibility trace values so that they are
     * properly initialized. This method is typically called once when
     * the simulation is first assembled and initialized.  This method
     * is called from the <code>Simulation</code> instance's init()
     * method after the init() method for the environment has been
     * called; thus, this method can access the
     * <code>Simulation</code> instance (sim) and thus information
     * about the environment, if needed.
     *
     * @param arr a <code>Object[]</code> value */
    public void init( Object[] arr ){
        // reset the agent's approximation of Q & the eligibility
        // trace values.
        resetEst();
    }


    /**
     * Performs any necessary initialization of the agent to prepare
     * it for the beginning of a new trial, initializes the class
     * variables prevState and prevAction, and returns the agent's
     * first action in the trial.
     *
     * @param state the first <code>State</code> of the new
     * trial. Should not be altered within method.
     * @return the first <code>Action</code> of the agent in the new
     * trial */
    public Action startTrial( State state ){
        // store the current state and the agent's first action
        // in the class variables for the previous state and
        // action (for use in future calls of the step() method)
        prevState = state;
        prevAction = getAction(prevState);
        resetElig();
        return prevAction;
    }


    /**
     * The method where all learning takes place for an agent; the
     * agent chooses its next action and then (possibly) learns from
     * the consequences of its previous action. The method checks for
     * the case of a terminal nextState, which indicates that the trial
     * terminates with this step, and adjusts the agent's learning and
     * other processes accordingly; in this case, the value returned
     * from the TDAgent's step() method will be ignored.  The method
     * is called once by the <code>Simulation</code> instance on each
     * step of the simulation.
     *
     * Note: A subclass of TDAgent must define the agent's memory
     * model.  For some memory models, the eligibility trace values
     * for state-action pairs are explicity stored; in these cases,
     * the eligibility trace value of (prevState, prevAction) must be
     * incremented PRIOR to calling this method.  In implementations
     * involving certain associative memory models, such as neural
     * networks (NN), for example, eligibility trace values are
     * maintained but they are not explicitly stored for each
     * state-action pair, so direct incrementation of SPECIFIC
     * elig. trace values is not possible (in the case of NN, this
     * issue is resolved by Richard Sutton's algorithm for
     * TD-backpropagation for NNs). */
    public Action step(State nextState, double reward) {
        State currState = nextState;
        Action nextAction;
        double error;

        if(currState.isTerminal()){ // in terminal state

            // compute the TD-error in Q(prevState, prevAction).  This
            // state-action pair led to a terminal state & the value
            // of the terminal state is 0 - hence the simplified
            // formula for TD-error
            error = reward - getQEst(prevState, prevAction);

            nextAction = null;
        }
        else{ // trial has not yet terminated

            // Choose the next action to take in response to nextState
            nextAction = getAction(currState);

            // compute the TD-error in Q(prevState, prevAction)
            error = getTDError(nextState, reward, nextAction);

            // store current action & state in class variables.
            // Will be the "previous values" for the next call to step()
            prevAction = nextAction;
            prevState   = currState;
        }

        // update all state-action value estimates and eligibility
        // trace values
        updateEst(error);

        return nextAction;
    }


    /**
     * Returns the next action to be taken given the current
     * state.  The default implementation uses an E-greedy policy.
     * With probability 1-epsilon, the agent selects the next action
     * greedily with respect to the current state-action value
     * estimates; with probability epsilon, the agent "explores" by
     * selecting a random action.  A subclass may override this
     * method; in this case, this (TDAgent.java's) instance of the
     * method should NOT be called by the subclass' version of the
     * method.
     *
     * @param currState a <code>State</code> value, the current
     * state
     * @return the <code>Action</code> to be taken in response to
     * currState */
    public Action getAction(State currState){
        optimalAction = (rng.nextDouble() >= epsilon);
        if (optimalAction) // exploit current knowledge
            return getOptimalAction(currState);
        else // explore
            return getRandomAction(currState);
    }


    /**
     * Sets the agent's learning rate to the specified value. If this
     * value is set to 0, the agent no longer learns: it simply
     * follows its current policy using its most recent approximation
     * of the action-value function.
     *
     * @param value a <code>double</code> value */
    public void setAlpha(double value){
        alpha = value;
    }

    /**
     * Sets the probability that the agent "explores" (ie selects a
     * random action) to the specified value. If this value is set to
     * 0, the agent no longer explores random actions; instead, the
     * agent always chooses the "greedy" action with respect to its
     * current approximation of the action-value function.
     *
     * @param value a <code>double</code> value, must be in [0,1] */
    public void setEpsilon(double value){
        epsilon = value;
    }

    /**
     * Sets the agent's discount rate of learning to the specified
     * value.
     *
     * @param value a <code>double</code> value, must be in [0,1] */
    public void setGamma(double value){
        gamma = value;
    }

    /**
     * Sets the trace-decay parameter to the specifed value. If this
     * value is set to 0, then eligibility traces are not used.
     *
     * @param lambda a <code>double</code> value, must be in [0,1] */
    public void setLambda(double value){
        lambda = value;
    }


                      /* ABSTRACT METHODS: */
    /**
     * Returns a random action from the current state.
     *
     * @param currState a <code>State</code> value, the current
     * state
     * @return the <code>Action</code> to be taken in response to the
     * current state */
    public abstract Action getRandomAction(State currState);

    /**
     * Returns the optimal action (with respect to the current
     * approximation of the action-value function) for the current
     * state
     *
     * @param currState a <code>State</code> value, the current
     * state
     * @return the <code>Action</code> to be taken in response to the
     * current state */
    public abstract Action getOptimalAction(State currState);

    /**
     * Updates all estimates and eligibility values necessary for
     * learning. */
    public abstract void updateEst(double error);

    /**
     * Resets the agent's estimation of the action-value function (eg,
     * using setQEst()). */
    public abstract void resetEst();

    /**
     * Resets the agent's eligibility trace values to their initial
     * values (eg, using setElig()). */
    public abstract void resetElig();

    /**
     * Returns the current value estimate for the given state-action
     * pair.
     *
     * @param state a <code>State</code> value
     * @param act an <code>Action</code> value
     * @return a <code>double</code> value, Q(state, act) */
    public abstract double getQEst( State state, Action act );

    /**
     * Sets the state-action value for the given state-action pair.
     * For neural network implementations, this method should NEVER be
     * called.
     *
     * @param state a <code>State</code> value
     * @param act an <code>Action</code> value
     * @param value a <code>double</code> value, the new value for
     * Q(state,act) */
    public abstract void setQEst( State state, Action act, double value );

    /**
     * Returns the current eligibility trace value for the given
     * state-action pair.  If lambda == 0, returns 0.  For neural
     * network implementations, this method should NEVER be called.
     *
     * @param state a <code>State</code> value
     * @param act an <code>Action</code> value
     * @return a <code>double</code> value, the current eligibility
     * trace value of (state, act) */
    public abstract double getElig( State state, Action act );

    /**
     * Sets the eligibility trace value for the given state-action
     * pair.  For neural network implementations and agents with
     * lambda == 0, this method should NEVER be called.
     *
     * @param state a <code>State</code> value
     * @param act an <code>Action</code> value
     * @param value a <code>double</code> value, the new value for the
     * eligibility trace */
    public abstract void setElig(State state, Action act, double value );

    /**
     * Computes the TD-error in Q(prevState, prevAction).  This formula
     * is typically dependent on the specific TD(lambda)-based control
     * algorithm (ie SARSA, Q-Learning, etc).  In neural network
     * implementations, the TD-error is equivalent to the output error
     * for the network.
     *
     * @param prevActResult an <code>ActionResult</code> describing
     * the consequent reward and state given to the agent for its
     * previous action.
     * @param nextAction the <code>Action</code> taken by the agent in
     * response to nextState
     * @return a <code>double</code> value, the TD-error in
     * Q(prevState, prevAction) */
    public abstract double getTDError(State nextState, double reward,
                                      Action nextAction);
}




