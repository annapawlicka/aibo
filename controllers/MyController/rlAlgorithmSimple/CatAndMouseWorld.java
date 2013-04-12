package rlAlgorithmSimple;

/**
 * Created with IntelliJ IDEA.
 * User: annapawlicka
 * Date: 31/01/2013
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
import java.awt.*;

public class CatAndMouseWorld implements RLWorld{
    public int bx, by;

    public int mx, my;
    public int cx, cy;
    public int chx, chy;
    public int hx, hy;
    public boolean gotCheese = false;

    public int catscore = 0, mousescore = 0;
    public int cheeseReward=50, deathPenalty=100;

    static final int NUM_OBJECTS=6, NUM_ACTIONS=8, WALL_TRIALS=100;
    static final double INIT_VALS=0;

    int[] stateArray;
    double waitingReward;
    public boolean[][] walls;

    public CatAndMouseWorld(int x, int y, int numWalls) {
        bx = x;
        by = y;
        makeWalls(x,y,numWalls);

        resetState();
    }

    public CatAndMouseWorld(int x, int y, boolean[][] newwalls) {
        bx = x;
        by = y;

        walls = newwalls;

        resetState();
    }

    /******* RLWorld interface functions ***********/
    public int[] getDimension() {
        int[] retDim = new int[NUM_OBJECTS+1];
        int i;
        for (i=0; i<NUM_OBJECTS;) {
            retDim[i++] = bx;
            retDim[i++] = by;
        }
        retDim[i] = NUM_ACTIONS;

        return retDim;
    }

    // given action determine next state
    public int[] getNextState(int action) {
        // action is mouse action:  0=u 1=ur 2=r 3=dr ... 7=ul
        Dimension d = getCoords(action);
        int ax=d.width, ay=d.height;
        if (legal(ax,ay)) {
            // move agent
            mx = ax; my = ay;
        } else {
            //System.err.println("Illegal action: "+action);
        }
        // update world
        moveCat();
        waitingReward = calcReward();

        // if mouse has cheese, relocate cheese
        if ((mx==chx) && (my==chy)) {
            d = getRandomPos();
            chx = d.width;
            chy = d.height;
        }

		/*// if cat has mouse, relocate mouse
		if ((mx==cx) && (my==cy)) {
			d = getRandomPos();
			mx = d.width;
			my = d.height;
		}*/

        return getState();
    }

    public double getReward(int i) { return getReward(); }
    public double getReward() {	return waitingReward; }

    public boolean validAction(int action) {
        Dimension d = getCoords(action);
        return legal(d.width, d.height);
    }

    Dimension getCoords(int action) {
        int ax=mx, ay=my;
        switch(action) {
            case 0: ay = my - 1; break;
            case 1: ay = my - 1; ax = mx + 1; break;
            case 2: ax = mx + 1; break;
            case 3: ay = my + 1; ax = mx + 1; break;
            case 4: ay = my + 1; break;
            case 5: ay = my + 1; ax = mx - 1; break;
            case 6: ax = mx - 1; break;
            case 7: ay = my - 1; ax = mx - 1; break;
            default: //System.err.println("Invalid action: "+action);
        }
        return new Dimension(ax, ay);
    }

    // find action value given x,y=0,+-1
    int getAction(int x, int y) {
        int[][] vals={{7,0,1},
                {6,0,2},
                {5,4,3}};
        if ((x<-1) || (x>1) || (y<-1) || (y>1) || ((y==0)&&(x==0))) return -1;
        int retVal = vals[y+1][x+1];
        return retVal;
    }

    public boolean endState() { return endGame(); }
    public int[] resetState() {
        catscore = 0;
        mousescore = 0;
        setRandomPos();
        return getState();
    }

    public double getInitValues() { return INIT_VALS; }
    /******* end RLWorld functions **********/

    public int[] getState() {
        // translates current state into int array
        stateArray = new int[NUM_OBJECTS];
        stateArray[0] = mx;
        stateArray[1] = my;
        stateArray[2] = cx;
        stateArray[3] = cy;
        stateArray[4] = chx;
        stateArray[5] = chy;
        return stateArray;
    }

    public double calcReward() {
        double newReward = 0;
        if ((mx==chx)&&(my==chy)) {
            mousescore++;
            newReward += cheeseReward;
        }
        if ((cx==mx) && (cy==my)) {
            catscore++;
            newReward -= deathPenalty;
        }
        //if ((mx==hx)&&(my==hy)&&(gotCheese)) newReward += 100;
        return newReward;
    }

    public void setRandomPos() {
        Dimension d = getRandomPos();
        cx = d.width;
        cy = d.height;
        d = getRandomPos();
        mx = d.width;
        my = d.height;
        d = getRandomPos();
        chx = d.width;
        chy = d.height;
        d = getRandomPos();
        hx = d.width;
        hy = d.height;
    }

    boolean legal(int x, int y) {
        return ((x>=0) && (x<bx) && (y>=0) && (y<by)) && (!walls[x][y]);
    }

    boolean endGame() {
        //return (((mx==hx)&&(my==hy)&& gotCheese) || ((cx==mx) && (cy==my)));
        return ((cx==mx) && (cy==my));
    }

    Dimension getRandomPos() {
        int nx, ny;
        nx = (int)(Math.random() * bx);
        ny = (int)(Math.random() * by);
        for(int trials=0; (!legal(nx,ny)) && (trials < WALL_TRIALS); trials++){
            nx = (int)(Math.random() * bx);
            ny = (int)(Math.random() * by);
        }
        return new Dimension(nx, ny);
    }

    /******** heuristic functions ***********/
    Dimension getNewPos(int x, int y, int tx, int ty) {
        int ax=x, ay=y;

        if (tx==x) ax = x;
        else ax += (tx - x)/Math.abs(tx-x); // +/- 1 or 0
        if (ty==y) ay = y;
        else ay += (ty - y)/Math.abs(ty-y); // +/- 1 or 0

        // check if move legal
        if (legal(ax, ay)) return new Dimension(ax, ay);

        // not legal, make random move
        while(true) {
            // will definitely exit if 0,0
            ax=x; ay=y;
            ax += 1-(int) (Math.random()*3);
            ay += 1-(int) (Math.random()*3);

            //System.out.println("old:"+x+","+y+" try:"+ax+","+ay);
            if (legal(ax,ay)) return new Dimension(ax,ay);
        }
    }

    void moveCat() {
        Dimension newPos = getNewPos(cx, cy, mx, my);
        cx = newPos.width;
        cy = newPos.height;
    }

    void moveMouse() {
        Dimension newPos = getNewPos(mx, my, chx, chy);
        mx = newPos.width;
        my = newPos.height;
    }

    int mouseAction() {
        Dimension newPos = getNewPos(mx, my, chx, chy);
        return getAction(newPos.width-mx,newPos.height-my);
    }
    /******** end heuristic functions ***********/


    /******** wall generating functions **********/
    void makeWalls(int xdim, int ydim, int numWalls) {
        walls = new boolean[xdim][ydim];

        // loop until a valid wall set is found
        for(int t=0; t<WALL_TRIALS; t++) {
            // clear walls
            for (int i=0; i<walls.length; i++) {
                for (int j=0; j<walls[0].length; j++) walls[i][j] = false;
            }

            float xmid = xdim/(float)2;
            float ymid = ydim/(float)2;

            // randomly assign walls.
            for (int i=0; i<numWalls; i++) {
                Dimension d = getRandomPos();

                // encourage walls to be in center
                double dx2 = Math.pow(xmid - d.width,2);
                double dy2 = Math.pow(ymid - d.height,2);
                double dropperc = Math.sqrt((dx2+dy2) / (xmid*xmid + ymid*ymid));
                if (Math.random() < dropperc) {
                    // reject this wall
                    i--;
                    continue;
                }


                walls[d.width][d.height] = true;
            }

            // check no trapped points
            if (validWallSet(walls)) break;

        }

    }

    boolean validWallSet(boolean[][] w) {
        // copy array
        boolean[][] c;
        c = new boolean[w.length][w[0].length];

        for (int i=0; i<w.length; i++) {
            for (int j=0; j<w[0].length; j++) c[i][j] = w[i][j];
        }

        // fill all 8-connected neighbours of the first empty
        // square.
        boolean found = false;
        search: for (int i=0; i<c.length; i++) {
            for (int j=0; j<c[0].length; j++) {
                if (!c[i][j]) {
                    // found empty square, fill neighbours
                    fillNeighbours(c, i, j);
                    found = true;
                    break search;
                }
            }
        }

        if (!found) return false;

        // check if any empty squares remain
        for (int i=0; i<c.length; i++) {
            for (int j=0; j<c[0].length; j++) if (!c[i][j]) return false;
        }
        return true;
    }

    void fillNeighbours(boolean[][] c, int x, int y) {
        c[x][y] = true;
        for (int i=x-1; i<=x+1; i++) {
            for (int j=y-1; j<=y+1; j++)
                if ((i>=0) && (i<c.length) && (j>=0) && (j<c[0].length) && (!c[i][j]))
                    fillNeighbours(c,i,j);
        }
    }
    /******** wall generating functions **********/

}