import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * An egg which spawns another bug. Takes a preset number of acts 
 * to hatch. A set number of bugs will be spawned with a delay in 
 * between. Once all Bugs have left the Egg, it will remove itself
 * from the World.
 * 
 * @author Jordan Cohen
 * @version Feb 2017
 */
public class Egg extends Actor
{
    private int actsUntilHatch;
    private int spawnCount;
    private int spawnsLeft;
    private int hatchCounter;
    private int delayBetweenHatches;
    private int delayBetweenHatchesCounter;
    
    private GreenfootImage eggImage, crackedImage;

    public Egg ()
    {
        eggImage = new GreenfootImage("BlueEgg.png");
        crackedImage = new GreenfootImage("BlueEgg_2.png");
        setImage(eggImage);
        // Set acts until first bug is hatched
        actsUntilHatch = 350;
        // Generate a random number between 2 and 4 to determine how many
        // bugs will spawn from this Egg
        spawnCount = Greenfoot.getRandomNumber(2) + 2;
        spawnsLeft = spawnCount;
        // Set delay
        delayBetweenHatches = 150;
        // Starting value for timer
        delayBetweenHatchesCounter = delayBetweenHatches;
    }

    /**
     * Act - do whatever the Egg wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        // Wait for hatching to start if still counting down
        if (actsUntilHatch > 0)
        {
            actsUntilHatch--;
        }
        // Once hatching starts, spawn Bugs with delay in between
        else
        {
            delayBetweenHatchesCounter--;
            if (delayBetweenHatchesCounter == 0)
            {
                getWorld().addObject (new Bug (0.50), getX(), getY());   
                delayBetweenHatchesCounter = delayBetweenHatches;
                spawnsLeft--;
                if (spawnsLeft <= spawnCount / 2){
                    setImage (crackedImage);
                }
            }
            if (spawnsLeft == 0)
                getWorld().removeObject(this);
        }
    }    
}
