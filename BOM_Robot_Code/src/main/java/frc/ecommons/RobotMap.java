package frc.ecommons;

public class RobotMap {
//Joysticks
    public static int driveJoy = 0;
    public static int gJoy = 1;

/* Elevator IDS
    Talons: 10 */

    public static int elevator = 10; 

/* Gurney
    Talons :11                Victors: ... */

    //Back
    public static int gBack = 11;
    
    //Right Front
    public static int gFront = 15;

    public static int gDrive = 19;


/* Drive Train */
    //Dog Gear
        public static int dogGearSolenoid1 = 4;
        public static int dogGearSolenoid2 = 5;

              
    /* Talons: 13(R), 14(L)      Victors: 15(R), 16(R), 17(L), 18(L) 
    R = Rightside Motor
    L = Leftside Motor */

        //Left Side
        public static int lMaster = 14;
        public static int lSlave1 = 17;
        public static int lSlave2 = 18;

        //Right Side
        public static int rMaster = 13;
        public static int rSlave1 = 20;
        public static int rSlave2 = 16;

        public static int ahrsAddress = 1;
        /* Manipulator */

            public static int grab1 = 2;
            public static int grab2 = 3;

            public static int grabWheels = 12;




}