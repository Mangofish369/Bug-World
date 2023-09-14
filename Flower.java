import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The Flower Class. A flower simply waits to be nibbled on and
 * grants hp to bugs that nibble it until it has no nibbles left 
 * to give, at which point it removes itself from the World
 * 
 * @author Jordan Cohen 
 * @version Feb 2017
 */
public class Flower extends Actor
{
    // Declare instance variables
    private int nibbles;
    private int hpPerNibble;
    // Create hpBar object for myself
    private SuperStatBar hpBar;

    // Constructor for Flower
    public Flower ()
    {
        // Set some initial values for my variables based on constants in the World
        nibbles = DesertWorld.NIBBLES_PER_FLOWER;
        hpPerNibble = DesertWorld.HP_PER_NIBBLE;
        // Create my own HealthBar object - this is an instance object. 
        // 
        hpBar = new SuperStatBar (nibbles, nibbles, this, 32, 6, 20, Color.PINK, Color.BLUE, true, Color.YELLOW, 1); // Construct a new HP bar with myself (this)

    }

    public void addedToWorld (World w)
    {
        w.addObject (hpBar, getX(), getY());
        hpBar.update(nibbles);
    }

    /**
     * Act - do whatever the Flower wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if (DesertWorld.getActNumber() % 15 == 0) hpBar.update(nibbles);
        // check if I have been completely eaten, and if so, remove me
        checkAndRemove();
    }    

    /**
     * Take a bite out of this Flower. 
     * 
     * @return int  The amount of HP that has been consumed
     */
    public int nibble ()
    {
        
        if (nibbles >= hpPerNibble) // If there is enough nibbles left..
        {
            nibbles -= hpPerNibble;
            return hpPerNibble;
        }
        else // If nibbles left is just about empty, return last little bit
        {
            int tempNibbles = nibbles;
            nibbles = 0;
            return tempNibbles;
        }
    }

    private void checkAndRemove ()
    {
        if (nibbles == 0) // If I'm out of nibbles to give...
        {
            getWorld().removeObject(this); // ... remove me from the World
        }
    }
}
