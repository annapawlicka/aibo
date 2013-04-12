package population;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 07/02/2013
 * Time: 02:01
 * To change this template use File | Settings | File Templates.
 */
class QUtil {

    byte[][] state;
    byte [][]action;

    public QUtil(byte actions, byte states) {
        state = new byte[states][4]; // 24 unique states, 4 unique percepts
        action = new byte[actions][2]; // 9 unique actions with 2 actuators (i.e. 2 motors with 3 functions each = 3^2)
        initAction(action);
        initState(state);
    }

    int previousLightValue = -1;
    int repeating = 0; // counts if light value is changing

    public float getRewardValue(byte [] e) {

        byte reward = 0;
        byte lightValue = e[4];

        // If the light value is increased, give a good reward:
        if(previousLightValue > 0)
            reward += (lightValue - previousLightValue);

        // If it has had the same light value for >5 times, penalize:
        if(previousLightValue == lightValue) {
            ++repeating;
        } else
            repeating = 0;
        if(repeating > 5)
            reward -= 2;

        previousLightValue = lightValue;

        // if bumper pressed, give bad reward
        if(e[1] == 1||e[2] == 1)
            reward -= 2;

        return reward;
    }

    /** Given an array of environment variables (sensor readings) and
     * a table of states this method returns a state ID number (assuming
     * there are 3x2x2x2 combinations).
     */
    public byte getState(byte [] e){
        byte count = 0;

        for(byte i=0;i<3;++i)
            for(byte j=0;j<2;++j)
                for(byte k=0;k<2;++k)
                    for(byte l=0;l<2;++l) {
                        if (e[0] == state[count][0])
                            if (e[1] == state[count][1])
                                if (e[2] == state[count][2])
                                    if (e[3] == state[count][3])
                                        return count;
                        ++count;
                    }
        return -1;
    }

    /** Given an action id (1-9), it converts it to an array representing what
     * the 2 motor ports should do. A and C only!
     */
    public void getCommands(byte a, byte[] commands) {
        //PCUtil.outPutTable(action);
        commands[0] = action[a][0];
        commands[1] = action[a][1];
    }

    /**
     * Sets random numbers for a given table of floats.
     * Note: It doesn't return a variable because an array
     * is an object, so changes take place within Q.
     */
    public static void setRandomValues(float [][] Q) {
        for(byte i=0;i<Q.length;++i) {
            for(byte j=0;j<Q[0].length;++j) {
                Q[i][j] = (float)Math.random();
            }
        }
    }

    /** Initializes a 24x4 array with all the combinations the sensors
     * can provide for this robot, assuming there are 3x2x2x2 combinations.
     */
    public static void initState(byte [][] state) {
        byte count = 0;
        for(byte i=0;i<3;++i)
            for(byte j=0;j<2;++j)
                for(byte k=0;k<2;++k)
                    for(byte l=0;l<2;++l) {
                        state[count][0] = i;
                        state[count][1] = j;
                        state[count][2] = k;
                        state[count][3] = l;
                        ++count;
                    }
    }

    /** Initializes a 9x2 array with all the states the motors
     * can provide for this robot, assuming there are 3x3 combinations.
     * i.e. each of 2 motors can be in forward, reverse, and stop states.
     */
    public static void initAction(byte [][] state) {
        byte count = 0;
        for(byte i=0;i<3;++i)
            for(byte j=0;j<3;++j) {
                state[count][0] = i;
                state[count][1] = j;
                ++count;
            }
    }
}
