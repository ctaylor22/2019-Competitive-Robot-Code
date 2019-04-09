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
  
  double m_orthoTolerance = .075;

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
  }

  public void autonomousInit() {
    teleopInit();
    m_limelight.autonomousInit();
  }
  
  public void autonomousPeriodic() {
    // followTurnPath();
    teleopPeriodic();
    m_limelight.autonomousPeriodic();
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
    double xAxis, yAxis;
    xAxis = 0.35 * m_joy.getRawAxis(Constants.xAxis);
    // * -1 to correct axis sign
    yAxis = -1*m_joy.getRawAxis(Constants.yAxis);
    
    if (m_joy.getRawButton(4)) {
      limelight_table.getEntry("ledMode").setNumber(3);
      aligned.setBoolean(isAligned());
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

  public boolean isAligned() {
    double error = m_limelight.m_orthoError;
    if (-m_orthoTolerance <= error && error <= m_orthoTolerance) {
      return true;
    }
    turn_error.setNumber(error);
    return false;
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
