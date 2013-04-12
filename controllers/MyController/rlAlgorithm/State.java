package rlAlgorithm;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 31/01/2013
 * Time: 23:27
 * To change this template use File | Settings | File Templates.
 */
/**
 * State - An interface for implementing the input (i.e. sensory
 * information) that an agent receives from the environment.
 *
 * Created: Fri May 31 07:54:39 2002
 *
 * @author Amy J. Kerr, Todd W. Neller, Spring '03 CS 392 students
 * @version 1.1 */

public interface State {

    /** whether or not the state is a terminal state */
    boolean isTerminal();

}
