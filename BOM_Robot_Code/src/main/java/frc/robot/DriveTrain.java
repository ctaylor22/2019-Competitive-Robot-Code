//Conner Taylor
//Drive Train of Robot


package frc.robot;

import java.sql.Time;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.RobotMap;
import frc.ecommons.Constants;

// import edu.wpi.first.wpilibj.shuffleboard.BuiltInTypes;

// import edu.wpi.first.wpilibj.shuffleboard;
 
public class DriveTrain  {

  String lowGear = "Low Gear";
  String highGear = "High Gear";

  

  // Joysticks/Controllers
  Joystick m_joy;

  //Talons
  WPI_TalonSRX m_rMaster;
  WPI_TalonSRX m_lMaster;
  
  //Victors
  WPI_VictorSPX m_rSlave1;
  WPI_VictorSPX m_rSlave2;
  WPI_VictorSPX m_lSlave1;
  WPI_VictorSPX m_lSlave2;

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

  NetworkTableEntry rightMasterOnOffCheck = motor.add("Right Master", true)
                                               .withWidget(BuiltInWidgets.kToggleSwitch)
                                               .withPosition(0, 0)
                                               .getEntry();
  NetworkTableEntry rightSlave1OnOffCheck = motor.add("Right Slave 1", true)
                                               .withWidget(BuiltInWidgets.kToggleSwitch)
                                               .withPosition(0, 1)
                                               .getEntry();
  NetworkTableEntry rightSlave2OnOffCheck = motor.add("Right Slave 2", true)
                                               .withWidget(BuiltInWidgets.kToggleSwitch)
                                               .withPosition(0, 2)
                                               .getEntry();
  NetworkTableEntry leftMasterOnOffCheck = motor.add("Left Master", true)
                                               .withWidget(BuiltInWidgets.kToggleSwitch)
                                               .withPosition(1, 0)
                                               .getEntry();
  NetworkTableEntry leftSlave1OnOffCheck = motor.add("Left Slave 1", true)
                                               .withWidget(BuiltInWidgets.kToggleSwitch)
                                               .withPosition(1, 1)
                                               .getEntry();
  NetworkTableEntry leftSlave2OnOffCheck = motor.add("Left Slave 2", true)
                                               .withWidget(BuiltInWidgets.kToggleSwitch)
                                               .withPosition(1, 2)
                                               .getEntry();

  NetworkTableEntry rightEncoderEntry = tab.add("Right Encoder", 0)
                                           .withSize(1, 1)
                                           .withPosition(1, 1) 
                                           .getEntry();
  NetworkTableEntry currentGearEntry = tab.add("Current Gear", lowGear)
                                          .withSize(2, 1)
                                          .withPosition(0, 2)
                                          .getEntry();
  NetworkTableEntry leftEncoderEntry = tab.add("Left Encoder", 0)
                                          .withSize(1, 1)
                                          .withPosition(0, 1)
                                          .getEntry();
  NetworkTableEntry driveSpeedEntry = tab.add("Drive Speed", 0.5)
                                          .withSize(2, 1)
                                          .withPosition(0, 0)
                                          .withWidget(BuiltInWidgets.kNumberSlider)
                                          .withProperties(Map.of("Min", 0, "Max", 1))
                                          .getEntry();

  public void TalonConfig() {
    //Configs Talon to default
    m_rMaster.configFactoryDefault();
    m_lMaster.configFactoryDefault();
    m_rSlave1.configFactoryDefault();
    m_rSlave2.configFactoryDefault();
    m_lSlave1.configFactoryDefault();
    m_lSlave2.configFactoryDefault();
    m_rSlave1.follow(m_rMaster);
    m_rSlave2.follow(m_rMaster);
    m_lSlave1.follow(m_lMaster);
    m_lSlave2.follow(m_lMaster);  
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
    m_lSlave2.setInverted(InvertType.FollowMaster);

    m_rMaster.setNeutralMode(NeutralMode.Brake);
    m_lMaster.setNeutralMode(NeutralMode.Brake);
    
  
    //ARGS (Slot, Value)
    m_rMaster.config_kF(0, 0);
    m_rMaster.config_kP(0, 0.1);
    m_rMaster.config_kI(0, 0);
    m_rMaster.config_kD(0, 0);
    // m_rMaster.config_IntegralZone(0, 30000);

    m_lMaster.config_kP(0, 0);
    m_lMaster.config_kF(0, 0.1);
    m_lMaster.config_kI(0, 0);
    m_lMaster.config_kD(0, 0);
    // m_lMaster.config_IntegralZone(0, 30000);


    int sensorUnitsPer100ms = 12000;
    m_rMaster.configMotionCruiseVelocity(sensorUnitsPer100ms);
    m_lMaster.configMotionCruiseVelocity(sensorUnitsPer100ms);

    int sensorUnitsPer100msPerSec = 4000;
    m_rMaster.configMotionAcceleration(sensorUnitsPer100msPerSec);
    m_lMaster.configMotionAcceleration(sensorUnitsPer100msPerSec);


  }
  public void robotInit(Joystick j) {
    run = new Timer();

    
    //Joysticks
    m_joy = j;

    //Talons - IDS found in ecommons.RobotMap
    m_rMaster = new WPI_TalonSRX(RobotMap.rMaster);
    m_lMaster = new WPI_TalonSRX(RobotMap.lMaster);

    //Victors - IDS found in ecommons.RobotMap
    m_rSlave1 = new WPI_VictorSPX(RobotMap.rSlave1);
    m_rSlave2 = new WPI_VictorSPX(RobotMap.rSlave2);
    m_lSlave1 = new WPI_VictorSPX(RobotMap.lSlave1);
    m_lSlave2 = new WPI_VictorSPX(RobotMap.lSlave2);

    //DoubleSolenoid
    dogGearSolenoid = new DoubleSolenoid(RobotMap.dogGearSolenoid1, RobotMap.dogGearSolenoid2);

    TalonConfig();

 
  }

  public void autonomousInit() {

  }
  
  public void autonomousPeriodic() {

  }

  /**
   * This function is called periodically during operator control.
   */
  
  public void teleopInit() {
    dogGearSolenoid.set(DoubleSolenoid.Value.kForward);


  } 
  public void teleopPeriodic() {

    //Dog Gear Shift
    if (m_joy.getRawButton(Constants.gearShift) && !dgLoop) {

      dgLoop = true;
      Shuffleboard.selectTab("Beginning Game");
      if (dogGearSolenoid.get() == (Value.kForward)) {
        dogGearSolenoid.set(DoubleSolenoid.Value.kReverse);
        
        currentGearEntry.setString(lowGear);
      } else if (dogGearSolenoid.get() == (Value.kReverse)) {
        dogGearSolenoid.set(DoubleSolenoid.Value.kForward);
      
        currentGearEntry.setString(highGear);
      }

    }
    if (!m_joy.getRawButton(Constants.gearShift)) {
      dgLoop = false;
    }

    if (m_joy.getRawButton(Constants.encoderReset)) {
      m_rMaster.setSelectedSensorPosition(0, 0, 0);
      m_lMaster.setSelectedSensorPosition(0, 0, 0);
    }
    
    //Equation for ARCADE DRIVE
    double xAxis, yAxis;
    xAxis = m_joy.getRawAxis(Constants.xAxis);
    // * -1 to correct axis sign
    yAxis = -1*m_joy.getRawAxis(Constants.yAxis);
    
    //Equation for Arcade Drive
    double leftSide, rightSide;
    rightSide = -(yAxis - xAxis);
    leftSide = yAxis + xAxis;

    //Percent drive output with slave follows
    if (m_joy.getRawButton(3)) {
      int ticksPerRev = 4096;
      int encoderToWheelGearRatio = 6;
      int wheelDiameter = 6;
      int targDistance = 60;
      double pi = 3.1415;
      double targPos = yAxis * ticksPerRev * encoderToWheelGearRatio * targDistance / (wheelDiameter * pi);

      m_lMaster.set(ControlMode.MotionMagic, targPos);
      m_rMaster.set(ControlMode.MotionMagic, -targPos);
    } else {

      TalonConfig();

      if (rightMasterOnOffCheck.getBoolean(true)) {
        m_rMaster.set(ControlMode.PercentOutput, rightSide * driveSpeed);
      }
      if (leftMasterOnOffCheck.getBoolean(true)) {
        m_lMaster.set(ControlMode.PercentOutput, leftSide * driveSpeed);
      }
      if (rightSlave1OnOffCheck.getBoolean(true)) {
        m_rSlave1.set(ControlMode.PercentOutput, rightSide * driveSpeed);
      }
      if (rightSlave2OnOffCheck.getBoolean(true)) {
        m_rSlave2.set(ControlMode.PercentOutput, rightSide * driveSpeed);
      }
      if (leftSlave1OnOffCheck.getBoolean(true)) {
        m_lSlave1.set(ControlMode.PercentOutput, leftSide * driveSpeed);
      }
      if (leftSlave2OnOffCheck.getBoolean(true)) {
        m_lSlave2.set(ControlMode.PercentOutput, leftSide * driveSpeed);
      }
      
     
    }
      

  }

  public void report() {
    driveSpeed = driveSpeedEntry.getDouble(0.5);
    rightEncoderEntry.setDouble(m_rMaster.getSelectedSensorPosition());
    leftEncoderEntry.setDouble(m_lMaster.getSelectedSensorPosition());
  }

  /**
   * This function is called periodically during test mode.
   */
  public void testInit() {
    run.reset();
    testDriveEntry.setBoolean(false);
    
  }
  public void testPeriodic() {
    m_lSlave1.follow(m_lMaster);
    m_lSlave2.follow(m_lMaster);
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

  }
}
