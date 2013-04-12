package population;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 07/02/2013
 * Time: 02:01
 * QBrain contains a table of Q values for all Q(action,state)
 * This table is updated everytime getAction() is called.
 **/

public class QBrain {

    public static float LEARN_RATE = 0.35f; // the learning rate constant
    public static float EXPLORE_RATE = 0.15f; // the rate it tries out random actions

    float [][] Q; // table of Q-values (action,state)

    byte i = -1; // last state
    byte a = -1; // last action
    byte a1 = 0; // action with highest q-value

    float r = 0; // Last reward

    QUtil util;

    public QBrain(byte actions, byte states, QUtil util) {
        Q = new float[actions][states]; // table of Q-values for action-states
        this.util = util;
        util.setRandomValues(Q);
    }

    /**
     * Returns an action given a set of environment percepts (e).
     */
    public byte getAction(byte[] e) {

        byte j = util.getState(e); // convert e to a state id number.

        if(i>=0) {
            r = util.getRewardValue(e);
            a1 = getMaxAction(j);
            Q[a][i] = Q[a][i] + LEARN_RATE * (r + Q[a1][j] - Q[a][i]);
        }

        i = j;

        float rand = (float)Math.random();
        if(rand > EXPLORE_RATE)
            a = getMaxAction(j);
        else {
            a = (byte)(Math.random() * 9);
            //josx.platform.rcx.Sound.beepSequence();
        }
        return a;
    }

    // find the largest Q-value for a given state (j), and return action
    public byte getMaxAction(byte state) {
        float max = -1000;
        byte action = 0;

        for(byte a=0;a<Q.length;++a) {
            if(Q[a][state] > max) {
                max = Q[a][state];
                action = a;
            }
        }
        return action;
    }
}
