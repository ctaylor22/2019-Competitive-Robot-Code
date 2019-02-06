package frc.ecommons;


public class Constants {
//Joystick Axis
    // Right stick X
    public static int xAxis = 4;
    // Left stick Y
    public static int yAxis = 1;

    // Right / Left Y axiss
    public static int gUpFront = 5;
    public static int gUpBack = 1;

    // triggers axis
    public static int gDriveForward = 3;
    public static int gDriveBack = 2;
    
    // enable auto up with leveling with button Y
    public static int gUpLevelEnable = 4;

    public static int elevatorUp = 3;
    public static int elevatorDown = 2;
 
//Buttons
public static int compressor = 8;
    //Drive Train
        public static int gearShift = 1;
        public static int encoderReset = 7;
    //Manipulator
        public static int manipPToggle = 1;
        public static int manipMToggle = 2;

//Percent Output
        public static double gForwardRate = 0.3;
        public static double gBackwardRate = -0.3;

    
//The angle at which the RoboRio and NavX board are tilted measured counterclockwise from the right, back, and top
    public static double rioYaw = 0;
    public static double rioPitch = 0;
    public static double rioRoll = 0;
}