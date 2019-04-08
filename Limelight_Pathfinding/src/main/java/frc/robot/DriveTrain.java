//Conner Taylor
//Drive Train of Robot


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.RobotMap;
import frc.ecommons.Constants;
import frc.utilities.TurnRadius;



// import edu.wpi.first.wpilibj.shuffleboard.BuiltInTypes;

// import edu.wpi.first.wpilibj.shuffleboard;
 
public class DriveTrain  {

  String lowGear = "Low Gear";
  String highGear = "High Gear";

  NetworkTable limelight_table = NetworkTableInstance.getDefault().getTable("limelight"); //LimelightNetworkTable
  ShuffleboardTab opening_tab = Shuffleboard.getTab("Beginning Game");
  NetworkTableEntry aligned = opening_tab.add("Aligned", false)
                              .withSize(1, 1)
                              .withPosition(3, 0)
                              .withWidget(BuiltInWidgets.kBooleanBox)
                              .getEntry();
  double steering_adjust = 0.0;
  double last_error = 0.0;
  double heading_error = 0;

  //Limelight
  Limelight m_limelight;
  int limelight_warm_up_counter = 0;
  
  // Joysticks/Controllers
  Joystick m_joy;
  Joystick m_gJoy;
  //Talons
  WPI_TalonSRX m_rMaster;
  WPI_TalonSRX m_lMaster;
  
  //Victors
  WPI_VictorSPX m_rSlave1;
  WPI_VictorSPX m_rSlave2;
  WPI_VictorSPX m_lSlave1;

  //Solenoids
  DoubleSolenoid dogGearSolenoid;

  //Loops
  boolean dgLoop = false;
  boolean driveTestLoop = false;

  double driveSpeed = 0.5;

  Timer run;
  ShuffleboardTab testMode = Shuffleboard.getTab("Test Mode");
  NetworkTableEntry testDriveEntry = testMode.add("Drive Test", false)
                                             .withWidget(BuiltInWidgets.kToggleButton)
                                             .getEntry();
  NetworkTableEntry testTimerEntry = testMode.add("Timer", 0)
                                             .withWidget(BuiltInWidgets.kTextView)
                                             .getEntry();
  
  ShuffleboardTab tab = Shuffleboard.getTab("Beginning Game");

  ShuffleboardTab motor = Shuffleboard.getTab("Motors");
  
  NetworkTableEntry txEntry = testMode.add("Limelight tx", 0)
                                  .withPosition(0, 3)
                                  .withWidget(BuiltInWidgets.kTextView)
                                  .getEntry();

  NetworkTableEntry steeringEntry = testMode.add("Steering Adjust Output", 0)
                                  .withPosition(1, 3)
                                  .withSize(2,1)
                                  .withWidget(BuiltInWidgets.kTextView)
                                  .getEntry();

  NetworkTableEntry rightEncoderEntry = tab.add("Right Encoder", 0)
                                           .withSize(1, 1)
                                           .withPosition(1, 1) 
                                           .getEntry();

  NetworkTableEntry leftEncoderEntry = tab.add("Left Encoder", 0)
                                          .withSize(1, 1)
                                          .withPosition(0, 4)
                                          .getEntry();

  NetworkTableEntry rightVelocity = tab.add("Right Speed", 0)
                                          .withSize(1, 1)
                                          .withPosition(0, 5)
                                          .getEntry();


  NetworkTableEntry leftVelocity = tab.add("Left Speed", 0)
                                          .withSize(1, 1)
                                          .withPosition(0, 6)
                                          .getEntry();

  NetworkTableEntry rightPathGoal = tab.add("Right Path Goal", 0)
                                          .withSize(1, 1)
                                          .withPosition(0, 7)
                                          .getEntry();


  NetworkTableEntry leftPathGoal = tab.add("Left Path Goal", 0)
                                          .withSize(1, 1)
                                          .withPosition(0, 8)
                                          .getEntry();


  //Turning correction pid loop
  PID turnPid = new PID();
  
  double turnMagnitudeLimit = .7;
  double minTurningPower = .2;

  //Whether automatic turn radius calculation is active
  boolean m_isTurning = false;

  //Hard coded paths for sandstorm
  double[][] paths = {{1, 1, 1}};
                    //{turnradius, degress, direction}

  //Ratios between the right and left wheels for each piece in the hard coded paths
  double[] ratios = {0.0};

  //Path which the limelight or another pather can write to
  double[] path = {0, 0, 0};
                  //{turnradius, degress, direction}

  //Ratio between right and left wheel spins on current path
  double ratio = 0;

  //The encoder values at which the robot *thinks* it will reach its target for left and right wheels respectively
  double[] encoderGoals = {0, 0};

  //The P value at which the robot will try to achive it's encoder goals on its paths
  //TODO: Tune this!
  double pPath = 100;

  int turnPath = 0;

  final double pi = 3.1415926535897932384626433832;

  public void TalonConfig() {
    //Configs Talon to default
    m_rMaster.configFactoryDefault();
    m_lMaster.configFactoryDefault();
    m_rSlave1.configFactoryDefault();
    m_rSlave2.configFactoryDefault();
    m_lSlave1.configFactoryDefault();
    m_rSlave1.follow(m_rMaster);
    m_rSlave2.follow(m_rMaster);
    m_lSlave1.follow(m_lMaster);
    //Motors go right way
    m_rMaster.setSensorPhase(false);
    m_lMaster.setSensorPhase(false);


    m_rMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    m_lMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

    m_rMaster.setInverted(false);
    m_rSlave1.setInverted(InvertType.FollowMaster);
    m_rSlave2.setInverted(InvertType.FollowMaster);

    m_lMaster.setInverted(false);
    m_lSlave1.setInverted(InvertType.FollowMaster);

    m_rMaster.setNeutralMode(NeutralMode.Brake);
    m_lMaster.setNeutralMode(NeutralMode.Brake);
    m_lSlave1.setNeutralMode(NeutralMode.Brake);
    m_rSlave1.setNeutralMode(NeutralMode.Brake);
    m_rSlave2.setNeutralMode(NeutralMode.Brake);
    
  
    //ARGS (Slot, Value)
    m_rMaster.config_kF(0, 0.0);
    m_rMaster.config_kP(0, 0);
    m_rMaster.config_kI(0, 0);
    m_rMaster.config_kD(0, 0);
    m_rMaster.config_IntegralZone(0, 1000);

    m_lMaster.config_kF(0, 0.00);
    m_lMaster.config_kP(0, 0);
    m_lMaster.config_kI(0, 0);
    m_lMaster.config_kD(0, 0);
    m_lMaster.config_IntegralZone(0, 1000);


    int sensorUnitsPer100ms = 8000;
    m_rMaster.configMotionCruiseVelocity(sensorUnitsPer100ms);
    m_lMaster.configMotionCruiseVelocity(sensorUnitsPer100ms);

    int sensorUnitsPer100ms_per_sec = 10240;
    m_rMaster.configMotionAcceleration(sensorUnitsPer100ms_per_sec);
    m_lMaster.configMotionAcceleration(sensorUnitsPer100ms_per_sec);


  }


  public void robotInit(Joystick j, Joystick gJ) {
    run = new Timer();
    //Joysticks
    m_joy = j;
    m_gJoy = gJ;

    //Talons - IDS found in ecommons.RobotMap
    m_rMaster = new WPI_TalonSRX(RobotMap.rMaster);
    m_lMaster = new WPI_TalonSRX(RobotMap.lMaster);

    //Victors - IDS found in ecommons.RobotMap
    m_rSlave1 = new WPI_VictorSPX(RobotMap.rSlave1);
    m_rSlave2 = new WPI_VictorSPX(RobotMap.rSlave2);
    m_lSlave1 = new WPI_VictorSPX(RobotMap.lSlave1);

    //DoubleSolenoid
    dogGearSolenoid = new DoubleSolenoid(RobotMap.dogGearSolenoid1, RobotMap.dogGearSolenoid2);

    TalonConfig();

    m_limelight = new Limelight(m_joy);
    SmartDashboard.putBoolean("Aligned", false);
    turnPid.robotInit(1.0, 1.0, 1.0, 1.0, true);
  }

  public void autonomousInit() {
    //Finds ratios for hard coded paths
    for (int ind = 0; ind < paths.length; ind++) {
        ratios[ind] = TurnRadius.turnRadiusRatio(paths[ind][0], Constants.wheelOffset, paths[ind][2]);
      }
    teleopInit();
    m_limelight.autonomousInit();
  }
  
  public void autonomousPeriodic() {
    // followTurnPath();
    teleopPeriodic();
    m_limelight.autonomousPeriodic();
  }

  public void followHardPaths(int pathnum) {
    if (turnPath == paths.length) {
      turnPath = -1;
      return;
    } else {
      if (followPath()) {
        turnPath += 1;
        processHardVariables(paths[pathnum]);
      }
    }
  }

  public void getLimelightPath() {
    /* Writes needed variables to the path array
     * The array is formatted as {turnradius, degresstoturn, direction}
     * direction is 1 if right and -1 if left
     */
    //TODO: Write code for the limelight to pick up nedded turn path and format it

    //Call this after path list has been written to
    processPathVariables();
  }

  public void processPathVariables() {
    double[] radiuses = {0, 0};
    double[] distances = {0, 0};
    //Rotations of the robot's wheels
    double[] rotations = {0, 0};
    double radians = path[1]*(pi/180);
    //Finds ratio between right and left wheels
    ratio = TurnRadius.turnRadiusRatio(path[0], Constants.wheelOffset, path[2]);
    //Finds the radius each side will rotate about depending on turn direction
    radiuses[0] = path[1]+Constants.wheelOffset*path[2];
    radiuses[1] = path[1]+Constants.wheelOffset*-path[2];
    //Finds the distance each side will travel
    distances[0] = radiuses[0]*(2*pi)*radians;
    distances[1] = radiuses[1]*(2*pi)*radians;
    //Finds how many rotations will be needed for wheels on each side
    rotations[0] = distances[0]/(2*pi*Constants.wheelRadius);
    rotations[1] = distances[1]/(2*pi*Constants.wheelRadius);
    //Calculates the encoder goals
    encoderGoals[0] = (int) m_lMaster.getSelectedSensorPosition()+Constants.ticksPerRotation*rotations[0];
    encoderGoals[1] = (int) m_rMaster.getSelectedSensorPosition()+Constants.ticksPerRotation*rotations[1];
  }

  public void processHardVariables(double[] pathlist) {
    double[] radiuses = {0, 0};
    double[] distances = {0, 0};
    //Rotations of the robot's wheels
    double[] rotations = {0, 0};
    double radians = pathlist[1]*(pi/180);
    ratio = TurnRadius.turnRadiusRatio(pathlist[0], Constants.wheelOffset, pathlist[2]);
    radiuses[0] = pathlist[1]+Constants.wheelOffset*pathlist[2];
    radiuses[1] = pathlist[1]+Constants.wheelOffset*-pathlist[2];
    distances[0] = radiuses[0]*(2*pi)*radians;
    distances[1] = radiuses[1]*(2*pi)*radians;
    rotations[0] = distances[0]/(2*pi*Constants.wheelRadius);
    rotations[1] = distances[1]/(2*pi*Constants.wheelRadius);
    encoderGoals[0] = (int) m_lMaster.getSelectedSensorPosition()+Constants.ticksPerRotation*rotations[0];
    encoderGoals[1] = (int) m_rMaster.getSelectedSensorPosition()+Constants.ticksPerRotation*rotations[1];
  }

  public boolean followPath() {
    double leftVelocity;
    double rightVelocity;
    double lefterr = encoderGoals[0] - m_lMaster.getSelectedSensorPosition();
    double righterr = encoderGoals[1] - m_rMaster.getSelectedSensorPosition();
    rightVelocity = ratio*righterr*pPath;
    leftVelocity = 1*lefterr*pPath;
    if (righterr > 1 && lefterr > 1) {
      m_rMaster.set(ControlMode.PercentOutput, rightVelocity);
      m_lMaster.set(ControlMode.PercentOutput, leftVelocity);
    } else if (righterr > 1) {
      m_rMaster.set(ControlMode.PercentOutput, rightVelocity);
      m_lMaster.set(ControlMode.PercentOutput, 0);
    } else if (lefterr > 1) {
      m_rMaster.set(ControlMode.PercentOutput, 0);
      m_lMaster.set(ControlMode.PercentOutput, leftVelocity);
    } else {
      m_rMaster.set(ControlMode.PercentOutput, 0);
      m_lMaster.set(ControlMode.PercentOutput, 0);
      return true;
    }
    return false;
  }

  /**
   * This function is called periodically during operator control.
   */
  
  public void teleopInit() {
    dogGearSolenoid.set(DoubleSolenoid.Value.kForward);
    m_limelight.teleopInit();
  }

  public void teleopPeriodic() {
    //Equation for ARCADE DRIVE
    double xAxis, yAxis, turnCorrect;
    xAxis = 0.35 * m_joy.getRawAxis(Constants.xAxis);
    // * -1 to correct axis sign
    yAxis = -1*m_joy.getRawAxis(Constants.yAxis);
    
    if (false) {//m_joy.getRawButtonPressed(Constants.limelightLEDon)) {
      limelight_table.getEntry("ledMode").setNumber(3);
      limelightAutoTurn(xAxis, yAxis);
    } /*else if (m_joy.getRawButton(Constants.makeTurnRadius)) {
      //TODO: See if limelight is picking up on a new path
      if (followPath() && m_isTurning) {
        m_isTurning = false;
      }
    }*/ else if (m_joy.getRawButton(4)) {
      if (true) {//limelight_table.getEntry("tv").getDouble(0) == 1 && Math.abs(limelight_table.getEntry("tx").getDouble(0)) < 0.05) {
        // auto turn off led if reading a good value and it's within tolerance
        limelight_table.getEntry("ledMode").setNumber(3);
      }
      turnCorrect = findTurnCorrection();
      xAxis += turnCorrect;
    } else {
      limelight_table.getEntry("ledMode").setNumber(1);
    }
    arcadeDrive(xAxis, yAxis);
    m_limelight.teleopPeriodic();
  }

  public void limelightAutoTurn(double xAxis, double yAxis) {
    // turn limelight led on

    // if button, limelight turn
    if (m_joy.getRawButton(Constants.limelightAutoTurn) && limelight_table.getEntry("tv").getDouble(0) == 1) {
        double limelight_kP = 0.02;
        double limelight_kF = 0.1;
        heading_error = limelight_table.getEntry("tx").getDouble(0.0);
        steering_adjust = 0.0;
        if (heading_error > 0.5)
        {
          steering_adjust = limelight_kP*heading_error + limelight_kF;
        }
        else if (heading_error < -0.5) {
          steering_adjust = limelight_kP*heading_error - limelight_kF;
        }
      arcadeDrive(xAxis, yAxis, steering_adjust);
    }
  }

  public void arcadeDrive(double x, double y) {
    //Equation for Arcade Drive
    double leftSide, rightSide;
    rightSide = x - y;
    leftSide = x + y;
    m_lMaster.set(ControlMode.PercentOutput, leftSide);
    m_rMaster.set(ControlMode.PercentOutput, rightSide);
  }

  public void arcadeDrive(double x, double y, double steering_adjust) {
    //Equation for Arcade Drive
    double leftSide, rightSide;
    rightSide = x - y + steering_adjust;
    leftSide = x + y + steering_adjust;
    // clamp the output to +/- 0.7 
    double min = -.5;
    double max = 0.5;
    leftSide = Math.max(min, Math.min(max, leftSide));
    rightSide = Math.max(min, Math.min(max, rightSide));
    m_lMaster.set(ControlMode.PercentOutput, leftSide);
    m_rMaster.set(ControlMode.PercentOutput, rightSide);
  }

  public double findTurnCorrection() {
    double turnCorrect;
    if (m_limelight.target) {
      turnCorrect = -m_limelight.m_orthoError;
      if (-.1 >= turnCorrect || turnCorrect >= .1) {
        SmartDashboard.putNumber("Turn Correction", turnCorrect);
        aligned.setBoolean(false);
        //Makes sure turn correction is significant enough to turn the Robot
        if (turnCorrect > 0) {
          turnCorrect = Math.max(turnCorrect, minTurningPower)
        } else if (turnCorrect < 0) {
          turnCorrect = Math.min(turnCorrect, -minTurningPower)
        }
        //Constrains return range to [-.5, .5]
        return Math.max(-turnMagnitudeLimit, Math.min(turnCorrect, turnMagnitudeLimit));
      } else {
        aligned.setBoolean(true);
        return 0;
      }
    } else {
      //Return nothing if the limelight had no target or the error was negligible
      aligned.setBoolean(false);
      return 0;
    }
  }

  public void report() {
    
    rightEncoderEntry.setDouble(m_rMaster.getSelectedSensorPosition());
    leftEncoderEntry.setDouble(m_lMaster.getSelectedSensorPosition());

    SmartDashboard.putNumber("Sensor Velocity", m_rMaster.getSelectedSensorVelocity());
    
    txEntry.setDouble(limelight_table.getEntry("tx").getDouble(0.0));
    steeringEntry.setDouble(steering_adjust);
  }

  /**
   * This function is called periodically during test mode.
   */
  public void testInit() {
    run.reset();
    testDriveEntry.setBoolean(false);
    m_limelight.testInit();
  }
  public void testPeriodic() {
    m_lSlave1.follow(m_lMaster);
    m_rSlave1.follow(m_rMaster);
    m_rSlave2.follow(m_rMaster);

    //Test to see if driving works
    boolean driveTestCheck = testDriveEntry.getBoolean(false);
    testTimerEntry.setDouble(run.get());
    if (driveTestCheck && !driveTestLoop) {
      driveTestLoop = true;
      run.reset();
      run.start();

    }
    if (run.get() < 3 && run.get() > 0.1) {
      m_rMaster.set(ControlMode.PercentOutput, 0.7);
      m_lMaster.set(ControlMode.PercentOutput, 0.7);
    } else if (run.get() > 3 && run.get() < 6) {
      m_rMaster.set(ControlMode.PercentOutput, -0.7);
      m_lMaster.set(ControlMode.PercentOutput, -0.7);
    } else if (run.get() >= 6) {
      m_lMaster.set(ControlMode.PercentOutput, 0);
      m_rMaster.set(ControlMode.PercentOutput, 0);
      testDriveEntry.setBoolean(false);
    }
    if (!driveTestCheck) {
      driveTestLoop = false;
      m_lMaster.set(ControlMode.PercentOutput, 0);
      m_rMaster.set(ControlMode.PercentOutput, 0);
      run.stop();
      run.reset();
     }

     m_limelight.testPeriodic();
  }
}
