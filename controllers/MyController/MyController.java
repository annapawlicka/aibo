/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 25/12/2012
 * Time: 17:53
 * Controller to control Aibo robot simulation.
 *
 * The position field of Servo represents the current angle difference (in radians) with
 * respect to the initial rotation of the Servo, e.g. For example if we have a "rotational"
 * Servo and the value of the position field is 1.5708, this means that this Servo is 90 degrees
 * from its initial rotation.
 */

import com.cyberbotics.webots.controller.*;
import population.LoopPopulation;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Controller for Aibo simulation.
 *
 * States: distance from red ball, collision detector reading, whether or not readings have repeated 5 times.
 */
public class MyController extends Robot {


    /* Data structures */
    LoopPopulation population;
    double[] input;
    double[] output;
    double[] motorActions;
    int inputSize;

    Servo[] joints;
    int timeStep;
    Random rand = new Random();

    /* Camera - Aibo's sight */
    Camera camera;
    int width, height;  // Width and height of the image from camera
    int[] image;         // Image from camera

    enum BALL_COLOR {RED, GREEN, BLUE, NONE};
    BALL_COLOR currentColor;
    int red, blue, green;

    /* Color distance Sensor - detects red */
    DistanceSensor distanceSensor;
    double oldDistance;
    double currentDistance;

    /* Chest distance sensor */
    DistanceSensor chestSensor;

    public MyController() {

        // call the Robot constructor
        super();
        joints = new Servo[12];
        timeStep = 1;
        currentDistance = -1;
        inputSize = 4;

    }

    public void run() {

        // Main loop:
        // Perform simulation steps of 64 milliseconds
        // and leave the loop when the simulation is over
        while (step(64) != -1) {


            updateSensorReadings();

            // Loop through actors/predictors and decide on actions/predictions
            for(int i=0; i<population.getPopulationSize(); i++){
                population.getPopulation().get(i).getActor().act(input, inputSize );
                population.getPopulation().get(i).getPredictor().predict(input);
            }
            // Get best loop
            int bestLoop = population.getBestLoop();
            //int randomLoop = population.getRandomCuriosityLoop();
            //System.out.println("Index: "+bestLoop);
            // Send motor actions to joints
            output = population.getPopulation().get(bestLoop).getMotorActions();
            for(int j=0; j< motorActions.length; j++){
                motorActions[j] = output[j];
            }
            // Apply actions to randomly chosen joints
            for(int k=0; k< motorActions.length; k++){
                if(joints[k].getMaxPosition() < motorActions[k]){
                    //System.out.println("Value too high: "+actions[k]);
                    //actions[k] = actions[k]/4;
                }

                if(joints[k].getMinPosition() > motorActions[k]){
                    //System.out.println("Value too low: "+actions[k]);
                    //actions[k] = actions[k] * -2;
                }

                joints[k].setPosition(50* motorActions[k]);
            }
            // Assess fitness of the best loop
            updateSensorReadings();
            System.out.println("Current distance: "+currentDistance);
            oldDistance = currentDistance;
            population.getPopulation().get(bestLoop).updateFitness(oldDistance, currentDistance);
            // Evolve actors
            population.increaseAge();

            if(timeStep % 4 ==0){
                population.convergeActorConditions(input);
                population.overwriteWorstActor();
            }

            timeStep++;

        }

    }

    public void updateSensorReadings() {
        for (int i = 0; i < joints.length; i++) {
            input[i] = joints[i].getPosition();
        }
        currentDistance = chestSensor.getValue();
        //System.out.println("Chest distance: "+ chestSensor.getValue());
    }

    public void initialise() {

        /* Initialise joints */
        joints[0] = getServo("PRM:/r2/c1-Joint2:21");
        joints[1] = getServo("PRM:/r2/c1/c2-Joint2:22");
        joints[2] = getServo("PRM:/r2/c1/c2/c3-Joint2:23");
        joints[3] = getServo("PRM:/r4/c1-Joint2:41");
        joints[4] = getServo("PRM:/r4/c1/c2-Joint2:42");
        joints[5] = getServo("PRM:/r4/c1/c2/c3-Joint2:43");
        joints[6] = getServo("PRM:/r3/c1-Joint2:31");
        joints[7] = getServo("PRM:/r3/c1/c2-Joint2:32");
        joints[8] = getServo("PRM:/r3/c1/c2/c3-Joint2:33");
        joints[9] = getServo("PRM:/r5/c1-Joint2:51");
        joints[10] = getServo("PRM:/r5/c1/c2-Joint2:52");
        joints[11] = getServo("PRM:/r5/c1/c2/c3-Joint2:53");

        /* Initialise camera */
        camera = getCamera("PRM:/r1/c1/c2/c3/i1-FbkImageSensor:F1");
        camera.enable(64);
        width = camera.getWidth();
        height = camera.getHeight();

        /* Initialise head (far) distance sensor */
        distanceSensor = getDistanceSensor("color_sensor");
        distanceSensor.enable(64);

        /* Initialise chest distance sensor */
        chestSensor = getDistanceSensor("PRM:/p1-Sensor:p1");
        chestSensor.enable(64);

        /* Enable joints */
        for (int i = 0; i < joints.length; i++) {
            joints[i].enablePosition(64);
        }

        /* Initialise inputs */
        input = new double[12];
        for (int j = 0; j < input.length; j++) {
            input[j] = joints[j].getPosition();
        }

        /* Initialise motor actions */
        motorActions = new double[12];
        output = new double[12];
        for(int i=0; i< motorActions.length; i++){
            motorActions[i] = 0;
            output[i] = 0;
        }

        /* Initialise population */
        population = new LoopPopulation(50, 12, 12, true);


        System.out.println("Init completed.");
    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.####");
        return Double.valueOf(twoDForm.format(d));
    }

    public static void main(String[] args) {

        MyController controller = new MyController();
        controller.initialise();
        controller.run();
    }
}