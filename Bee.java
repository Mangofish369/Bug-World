import greenfoot.*;  

public class Bee extends Bug
{
    int mySpeed = 10000;
    public Bee(){
        maxEnergy = DesertWorld.BUG_MAX_HP; 
        energy = maxEnergy;
        if (DesertWorld.SHOW_BARS) energyBar =  new SuperStatBar (maxEnergy, energy, this, 40, 8, -32, Color.GREEN, Color.RED, true, Color.YELLOW, 1); // Construct a new HP bar with myself (this)
        myAge = 0;
        myActNumber = getNextActNumber();
        setImage("images/bee.png");
    }
    public void increaseSpeed(){
        mySpeed = mySpeed * 100000;
        if(energy < (maxEnergy * 03)){
            mySpeed = mySpeed *2;
        }
    }
    
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
}

