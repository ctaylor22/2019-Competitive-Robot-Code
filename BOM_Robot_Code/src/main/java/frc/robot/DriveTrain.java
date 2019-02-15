//Conner Taylor
//Drive Train of Robot


package frc.robot;

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
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
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

  double driveSpeed = 0.5;
  
  ShuffleboardTab tab = Shuffleboard.getTab("DriveTrain");
  NetworkTableEntry rightEncoderEntry = tab.add("Right Encoder", 0).getEntry();
  NetworkTableEntry currentGearEntry = tab.add("Current Gear", lowGear).getEntry();
  NetworkTableEntry leftEncoderEntry = tab.add("Left Encoder", 0).getEntry(); 
  NetworkTableEntry driveSpeedEntry = tab.add("Drive Speed", 0.5)
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
  public void teleopPeriodic(double limeX) {

    //Dog Gear Shift
    if (m_joy.getRawButton(Constants.gearShift) && !dgLoop) {
      dgLoop = true;
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

    if(m_joy.getRawButton(9)){
      autoAlign(limeX);
    }

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

      m_rMaster.set(ControlMode.PercentOutput, rightSide * driveSpeed);
      m_lMaster.set(ControlMode.PercentOutput, leftSide * driveSpeed);
     
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
  
  public void testPeriodic() {
  }
  
  public void autoAlign(double X){
    if (X < -1.0){turnRight();}
    else if (X > 1.0){turnLeft();}
  }

  public void turnLeft(){
    m_rMaster.set(ControlMode.PercentOutput, -1 * 0.10);
    m_lMaster.set(ControlMode.PercentOutput,  1 * 0.10);
  }
  public void turnRight(){
    m_rMaster.set(ControlMode.PercentOutput, 1 * 0.10);
    m_lMaster.set(ControlMode.PercentOutput, -1 * 0.10);
  }
  public void moveForward(){
    m_rMaster.set(ControlMode.PercentOutput, 1 * 0.10);
    m_lMaster.set(ControlMode.PercentOutput, 1 * 0.10);
  }
  public void moveBackward(){
    m_rMaster.set(ControlMode.PercentOutput, -1 * 0.10);
    m_lMaster.set(ControlMode.PercentOutput, -1 * 0.10);
  }
  

}
