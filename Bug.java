import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.Random;


public class Bug extends SuperSmoothMover
{
    private static GreenfootImage littleImage;
    
     
    // Instance variables - Class variables
    private Flower targetFlower;
    private ArrayList<Flower> flowers;
    private SuperStatBar energyBar;
    private int energy;
    private int maxEnergy;

    private int mySpeed = 2;
    private int myAge;
    private int myActNumber;

    private static int nextActNumber = -1;

    /**
     * Primary constructor for Bug - creates a new Bug with full HP.
     * This is called by the Spawn button, and used for creating the 
     * first bug at the beginning of the simulation.
     */
    public Bug ()
    {
        maxEnergy = DesertWorld.BUG_MAX_HP; 
        energy = maxEnergy;
        if (DesertWorld.SHOW_BARS) energyBar =  new SuperStatBar (maxEnergy, energy, this, 40, 8, -32, Color.GREEN, Color.RED, true, Color.YELLOW, 1); // Construct a new HP bar with myself (this)
        myAge = 0;
        myActNumber = getNextActNumber();
        setImage(littleImage);
    }

    /**
     * An additional constructor used specifically for Bugs that are to be
     * spawned without full hit points (hatched from eggs... hungry!)
     */
    public Bug (double percentHealth)
    {
        maxEnergy = DesertWorld.BUG_MAX_HP; 
        energy = (int) ((double)maxEnergy * percentHealth); // Assign a percentage of max health
        if (DesertWorld.SHOW_BARS) energyBar =  new SuperStatBar (maxEnergy, energy, this, 40, 8, -32, Color.GREEN, Color.RED, true, Color.YELLOW, 1); // Construct a new HP bar with myself (this)
        // As the target to follow around
        myAge = 0;
        myActNumber = getNextActNumber();
        //System.out.println("Spawned: " + myActNumber);
        init();
        setImage(littleImage);
    }

    public static void init () { 
        Random rand = new Random();
        String[] images = {"ladybug_02.png", "ladybug_blue.png","ladybug_sayianBlue.png", "ladybug_green.png","ladybug_brolyGreen.png", "ladybug_yellow.png", "ladybug_orange.png",
            "ladybug_sayian.png", "ladybug_purple.png"}; 
        int random = rand.nextInt(9);
        littleImage = new GreenfootImage (images[random]);
        littleImage.scale((int)(littleImage.getWidth()*3.0/4.0), (int)(littleImage.getHeight()*3.0/4.0));
        
    }
    
    public void refreshActNumber() {
        
        myActNumber = getNextActNumber();
        //System.out.println("Refreshed to : " + myActNumber);
    }

    public static void resetActDistribution(){
        nextActNumber = 0;
    }
    
    private static int getNextActNumber () {
        if (nextActNumber == -1){
            nextActNumber = 0;
        }
        if (nextActNumber > 59){
            nextActNumber = 0;
        }
        return nextActNumber++;
    }

    /**
     * Method automatically called by Greenfoot when an object of this
     * class is added to the World
     * 
     * @param World This parameter is the World being added to.
     */
    public void addedToWorld (World w)
    {
        targetClosestFlower();
        if (DesertWorld.SHOW_BARS) 
        {
            w.addObject (energyBar, getX(), getY());
            energyBar.update(maxEnergy);
        }
    }

    /**
     * Act - do whatever the Bug wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        myAge++; // increase my age - used for tracking statistics
        if (energy > 0)
        {
            if (targetFlower != null && targetFlower.getWorld() == null){
                targetFlower = null;
            }
            energy -= DesertWorld.BUG_DEATH_RATE;
            if (DesertWorld.getActNumber() == myActNumber) // Only run on my assigned act number to save time
            {
                //System.out.print("I");
                if (targetFlower == null || DesertWorld.getDistance (this, targetFlower) > 20){
                    targetClosestFlower ();
                }
            }
            if (DesertWorld.SHOW_BARS && myAge % 6 == 0){
                energyBar.update(energy);
            }

            // If my current target Flower exists, move toward it
            if (targetFlower != null)
            {
                moveTowardOrEatFlower();
            }
            // If I can't find anything to eat, move in a random direction
            else
            {
                moveRandomly();
            }

            // If I'm not a newborn, and I manage to get to full HP, lay an egg
            if (energy >= maxEnergy && myAge > 100)
            {
                layEgg ();
            }
        }
        // Death:
        else
        {
            DesertWorld d = (DesertWorld)getWorld(); // Access the World
            d.addObject(new DeadBug(), getX(), getY()); // Place a skull in my place
            d.addDeath(myAge); // Add to stats
            getWorld().removeObject(this); // Remove myself
        }
    }    

    /**
     * Private method, called by act(), that constantly checks for closer targets
     */
    private void targetClosestFlower ()
    {
        double closestTargetDistance = 0;
        double distanceToActor;
        // Get a list of all Flowers in the World, cast it to ArrayList
        // for easy management

        flowers = (ArrayList<Flower>)getObjectsInRange(40, Flower.class);
        if (flowers.size() == 0){
            flowers = (ArrayList<Flower>)getObjectsInRange(140, Flower.class);
        } 
        if (flowers.size() == 0){
            flowers = (ArrayList<Flower>)getObjectsInRange(350, Flower.class);
        } 
        if (flowers.size() == 0){
            //flowers = (ArrayList<Flower>)getWorld().getObjects(Flower.class);
        } 

        if (flowers.size() > 0)
        {
            // set the first one as my target
            targetFlower = flowers.get(0);
            // Use method to get distance to target. This will be used
            // to check if any other targets are closer
            closestTargetDistance = DesertWorld.getDistance (this, targetFlower);

            // Loop through the objects in the ArrayList to find the closest target
            for (Flower o : flowers)
            {
                // Cast for use in generic method
                //Actor a = (Actor) o;
                // Measure distance from me
                distanceToActor = DesertWorld.getDistance(this, o);
                // If I find a Flower closer than my current target, I will change
                // targets
                if (distanceToActor < closestTargetDistance)
                {
                    targetFlower = o;
                    closestTargetDistance = distanceToActor;
                }
            }
            turnTowards(targetFlower.getX(), targetFlower.getY());
        }
    }

    /**
     * Private method, called by act(), that moves toward the target,
     * or eats it if within range.
     */
    private void moveTowardOrEatFlower ()
    {
        

        //if (this.getNeighbours (30, true, Flower.class).size() > 0)
        if (DesertWorld.getDistance (this, targetFlower) < 18)
        {
            /**
            // If I was able to eat, increase by life by flower's nibble power
            int tryToEat = targetFlower.nibble();
            if (tryToEat > 0 && energy < maxEnergy)
            {
                energy += tryToEat;
            }*/
            energy += targetFlower.nibble();
        }
        else
        {
            move (mySpeed);
        }
    }

    /**
     * A method to be used for moving randomly if no target is found. Will mostly
     * just move in its current direction, occasionally turning to face a new, random
     * direction.
     */
    private void moveRandomly ()
    {
        if (Greenfoot.getRandomNumber (100) == 50)
        {
            turn (Greenfoot.getRandomNumber(360));
        }
        else
            move (mySpeed);
    }

    /**
     * Method to spawn an Egg - reduces HP of this Bug by 30%
     */
    private void layEgg ()
    {
        getWorld().addObject (new Egg (), getX(), getY());
        // Lose 30% food life when laying an egg
        energy -= (int)(energy * 0.30);
    }
}
