package frc.ecommons;

public class RobotMap {
//Joysticks
    public static int driveJoy = 0;
    public static int eJoy = 1;
    public static int gJoy = 2;

/* Elevator IDS
    Talons: 10 */

    public static int elevator = 10; 
    public static int elevFollow = 18; //Follows Elevator


/* Gurney
    Talons: 11                Victors: ... */

    //Back
    public static int gBack = 11;
    
    //Right Front
    public static int gFront = 15;

    public static int gDrive = 20;


/* Drive Train */
    //Dog Gear Pneumatics
    public static int dogGearSolenoid1 = 0;
    public static int dogGearSolenoid2 = 1;

    /* Talons: 13(R), 14(L)      Victors: 16(R), 17(R), 18(L), 19(L)
    R = Rightside Motor
    L = Leftside Motor */

    //Left Side
    public static int lMaster = 14;
    public static int lSlave1 = 19;
    
        //Right Side
        public static int rMaster = 13;
        public static int rSlave1 = 17;
        public static int rSlave2 = 16; //16 Is top reverse(wiring) motor...

    public static int ahrsAddress = 1;

/* Manipulator */

    public static int grab1 = 2;
    public static int grab2 = 3;

    public static int dual1 = 5;
    public static int dual2 = 4;

    public static int hatchPneumatic1 = 6;
    public static int hatchPneumatic2 = 7;

    public static int manipUpDown = 12;
    public static int manipWheels = 21; 

}