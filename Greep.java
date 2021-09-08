import greenfoot.*;

/**
 * A Greep is an alien creature that likes to collect tomatoes.
 * 
 * @author (your name here)
 * @version 0.2
 */
public class Greep extends Creature
{
    private static final double WALKING_SPEED = 5.0;
    private final int[] SHIP_COORDS;
    
    // Remember: you cannot extend the Greep's memory. So:
    // no additional fields (other than final fields) allowed in this class!
    
    /**
     * Default constructor for testing purposes.
     */
    public Greep()
    {
        this(null);
    }
    
    /**
     * Create a Greep with its home space ship.
     */
    public Greep(Ship ship)
    {
        super(ship);
        SHIP_COORDS= new int[]{ship.getX(), ship.getY()};
        setRotation(0);
    }
    
    public boolean inFrontOfWater() {
        double angle = Math.toRadians( getRotation() );
        int x,y;
        if(Greenfoot.getRandomNumber(3) != 1) {
            x = (int) Math.round(getX() + Math.cos(angle) * WALKING_SPEED*15);
            y = (int) Math.round(getY() + Math.sin(angle) * WALKING_SPEED*15);
        } else {
            x = (int) Math.round(getX() + Math.cos(angle) * WALKING_SPEED*(5 +Greenfoot.getRandomNumber(15)));
            y = (int) Math.round(getY() + Math.sin(angle) * WALKING_SPEED*(5 +Greenfoot.getRandomNumber(15)));
        }
        
        // now make sure that we are not stepping out of the world
        try {
            if (x >= getWorld().getWidth() || x < 0 || y >= getWorld().getHeight() || ((Earth)getWorld()).isWater(x, y)) {
                return true;
            }
        } catch(Exception e){}
        return false;
    }
    
    public int[] cellOfCherryInMemory() {
        int y = (int)Math.floor(getMemory() / getWorld().getWidth()/45);
        int x = getMemory() * 45 - y;
        return new int[]{x,y};
    }
    public void turnToMemory() {
        int[] cim = cellOfCherryInMemory();
        int dX = cim[0]- getX();
        int dY = cim[1] - getY();
        setRotation((int) (180 * Math.atan2(dY, dX) / Math.PI));
    }
    public int getCell() {
        return (int)getY() / 45  * getWorld().getWidth()/45 + getX() / 45;
    }
    
    public boolean inCellOfMemory() {
        int[] cim = cellOfCherryInMemory();
        int dX = Math.abs(cim[0]- getX());
        int dY = Math.abs(cim[1] - getY());
        return dX < 50 && dY < 50;
        
    }
    
    public int[] cellCoords() {
        int[] cim = cellOfCherryInMemory();
        int dX = Math.abs(cim[0]- getX());
        int dY = Math.abs(cim[1] - getY());
        return new int[] {dX, dY};
        
    }
    

    /**
     * Do what a greep's gotta do.
     */
    public void act()
    {
        super.act();   // do not delete! leave as first statement in act().
        if (carryingTomato()) {
            if (atShip()) {
                dropTomato();
            }
            else {
                turnHome();
                while(inFrontOfWater()) {
                    turn(10);
                }
                if(isAtEdge() || atWater()) {
                    turn(100);
                }
                move();
                //spit("purple");

            }
        }
        else {
            if(seePaint("red")) {
                setMemory(0);
            }
            
            //turn(Greenfoot.getRandomNumber(10)-5);
            int turns = 0;
            int wentOtherway = 30;
            while(inFrontOfWater()) {
                turn(wentOtherway);
                turns+= wentOtherway;
                if(turns >= 70) {
                    wentOtherway = -30;
                } else if (turns <= -70) {
                    setFlag(1, false);
                    setFlag(2, true);
                    setMemory(getCell());
                }
            }
            if(isAtEdge() || atWater()) {
                    turn(20 + Greenfoot.getRandomNumber(30));
            }
            //memory is what direction purple was tasted last
            
            
            if(getMemory() > 18 && getFlag(1) && !getFlag(2)) {
                    if(!inCellOfMemory()) {
                        turnToMemory();
                        while(inFrontOfWater()) {
                            turn(45);
                        }
                        if(isAtEdge() || atWater()) {
                            turn(90);
                        }
                    } else {
                        System.out.println("Here");
                        while(inFrontOfWater()) {
                            turn(40+ Greenfoot.getRandomNumber(10));
                        }
                        if(isAtEdge() || atWater()) {
                            turn(20 + Greenfoot.getRandomNumber(30));
                        }
                    }
                    
                    if(getOneIntersectingObject(TomatoPile.class) == null) {
                        move();
                    } else {
                        System.out.println("Tomato");
                    }
                    if(!seePaint("purple") || seePaint("red")) {
                        setMemory(0);
                    }
                        
                } else if(getMemory() > 18 && getFlag(2) && !getFlag(1)) {
                    if(!inCellOfMemory()) {
                        if(!(cellCoords()[0] < 50) || !(cellCoords()[1] < 50) && (SHIP_COORDS[0] > 150 || SHIP_COORDS[1] > 150)) {
                            turnToMemory();
                            if(!(getFlag(1) && getFlag(2))) {
                                turn(180);
                            }
                            setFlag(1, true);
                            setFlag(2, true);
                            spit("orange");
                        }
                        while(inFrontOfWater()) {
                            turn(15 + Greenfoot.getRandomNumber(30));
                        }
                        if(isAtEdge() || atWater()) {
                            turn(90);
                        }
                    }
                }
            
                if(seePaint("purple") && !seePaint("red")) {
                    setMemory(getCell());
                }
                if(seePaint("orange")) {
                    
                            if(!(getFlag(1) && getFlag(2))) {
                                turnToMemory();
                                turn(180);
                            }
                            setFlag(1, true);
                            setFlag(2, true);
                } else {
                    setFlag(1, false);
                            setFlag(2, false);
                }
                
                if((TomatoPile) getOneIntersectingObject(TomatoPile.class) == null || carryingTomato()) {
                    move();
                }
            
            move();
            checkFood();
        }
    }
    
    /**
     * Is there any food here where we are? If so, try to load some!
     */
    public void checkFood()
    {
        // check whether there's a tomato pile here
        TomatoPile tomatoes = (TomatoPile) getOneIntersectingObject(TomatoPile.class);
        if (tomatoes != null) {
            loadTomato();
            setMemory(0);
            
            
            setRotation(80);
            if(getOneIntersectingObject(TomatoPile.class) == null) {
                spit("red");
            } else {
                spit("purple");
                setMemory(getCell());
                setFlag(1, true);
                setFlag(2, false);
            }
            
            
            
            // Note: this attempts to load a tomato onto *another* Greep. It won't
            // do anything if we are alone here.
        }
    }

    /**
     * This method specifies the name of the author (for display on the result board).
     */
    public static String getAuthorName()
    {
        return "Anonymous";  // write your name here!
    }

    /**
     * This method specifies the image we want displayed at any time. (No need 
     * to change this for the competition.)
     */
    public String getCurrentImage()
    {
        return carryingTomato() ? "greep-with-food.png" : "greep.png";
    }
}