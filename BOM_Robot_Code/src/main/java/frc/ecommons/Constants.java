package frc.ecommons;


public class Constants {

// *** Drive Joystick Axis ***
    // Left stick Y
    // drive fwd, bkwd
    public static int yAxis = 1;

    // Right stick X
    // turns
    public static int xAxis = 4;

    // left trigger
    // drive elevator down
    public static int elevatorDown = 2;
    // right trigger
    // drive elevator up
    public static int elevatorUp = 3;
 
// *** Drive Joystick Buttons ***
    // A button
    // Shifts gears of the Drive Train
    public static int gearShift = 1;

    // Y button
    // elevator closed loop control
    // goes to position selected by index of heights array
    public static int elevatorToPositionButton = 4;

    // Select button (small black button just left of the center of the controller)
    // resets the drive shaft encoders' position and the elevator encoder's position to 0
    public static int encoderReset = 7;

    // Menu button (small black button just right of the center of the controller)
    // toggles the compressor on/off
    public static int compressor = 8;

// *** gurney joystick axis ***
    // Left stick Y axis
    // Winds rear gurney motor
    public static int gUpBack = 1;

    // Left trigget axis
    // drive gurney backward
    public static int gDriveBack = 2;

    // Right trigger axis
    // drive gurney forward
    public static int gDriveForward = 3;

    // Right Stick Y axis
    // winds front gurney motor
    public static int gUpFront = 5;

// *** gurney joystick buttons ***
    //Manipulator
    // A button
    // toggles manipulator pneumatics
    public static int manipPToggle = 1;

    // B button
    // toggles manipulator motor
    public static int manipMToggle = 2;
    
    // Y button
    // enable auto up with leveling
    public static int gUpLevelEnable = 4;
    

// *** other ***
    //Percent Output
    public static double gForwardRate = 0.3;
    public static double gBackwardRate = -0.3;
    
    //The angle at which the RoboRio and NavX board are tilted measured counterclockwise from the right, back, and top
    public static double rioYaw = 0;
    public static double rioPitch = 0;
    public static double rioRoll = 0;
}