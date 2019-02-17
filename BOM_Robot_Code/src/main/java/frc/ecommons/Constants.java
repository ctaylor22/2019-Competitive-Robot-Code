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

    public static int motorTest = 0;

    public static int manipUpDownBut = 3;

    // B button
    // toggles manipulator motor
    public static int manipWheelBackToggle = 5;
    public static int manipWheelForToggle = 6;
    
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
    // Ticks per rotation of the wheels
    public static double ticksPerRotation = 100;

// *** gurney joystick axis ***
    // Left stick Y axis
    // Winds rear gurney motor
    public static int gUpBack = 1;

    // Left trigger axis
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

    
    // X
    // go down
    public static int gurneyGoDown = 3;

    // Y button
    // enable auto up with leveling
    public static int gurneyGoUp = 4;

    // Left bumper = 5

    // Right bumper
    public static int gurneyManualDrive = 6;

    public static int gurneyEncoderReset = 7;

    // unused
    public static int gurneyHoldEnable = 8;

// *** gurney balance calibration ***
    //P factor of back motor
    public static double frontDriveP = 8.5;

    //Current at which the robot will lower safely
    public static double gSafteySpeed = .075;

// *** other ***
    //Percent Output
    public static double gForwardRate = 0.3;
    public static double gBackwardRate = -0.3;
    
    //The angle at which the RoboRio and NavX board are tilted measured counterclockwise from the right, back, and top
    public static double rioYaw = 0;
    public static double rioPitch = 34;
    public static double rioRoll = 0;

//Distance measurements for Limelight
    //Limelight
    public static double camHeight = 0.0;
    public static double targetHeight = 0.0;
    public static double camAngle = 0.0;
}