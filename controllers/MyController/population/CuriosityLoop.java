package population;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 19/01/2013
 * Time: 22:26
 * Curiosity loop contains predictor of a subset os sensory dimensions and actor acting on a subset of motor
 * dimensions.
 */


public class CuriosityLoop {

    private Actor actor;
    private Predictor predictor;
    private int inputSize;
    private int outputSize;
    private double fitnessScore;
    private int age;

    public CuriosityLoop(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        actor = new Actor(inputSize);
        predictor = new Predictor(inputSize);
        init();
    }

    public void init(){
        actor.generateIndividual();
        predictor.generateIndividual();
        fitnessScore = 0;
        age = 0;
    }

    /* Getters and setters */

    public Actor getActor(){
        return actor;
    }

    public Predictor getPredictor(){
        return predictor;
    }

    public int getInputSize(){
        return inputSize;
    }

    public int getOutputSize(){
        return outputSize;
    }

    public int getAge(){
        return age;
    }

    public void updateAge(){
        age++;
    }

    public void setInputSize(int n){
        inputSize = n;
    }

    public void setOutputSize(int n){
        outputSize = n;
    }

    public double[] getPredictions(){
        return predictor.getPredictions();
    }

    public double[] getMotorActions(){
        return actor.getActions();
    }

    public double getFitnessScore(){
        return fitnessScore;
    }

    public void updateFitness(double oldDistance, double newDistance){
        //if(oldDistance < newDistance) fitnessScore --;
        //if(oldDistance > newDistance) fitnessScore ++;
        if(newDistance > oldDistance) fitnessScore ++;
        else if(newDistance < 200 || newDistance > 700) fitnessScore ++;
    }



}
