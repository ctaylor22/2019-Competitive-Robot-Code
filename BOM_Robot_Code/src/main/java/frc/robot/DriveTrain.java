//Conner Taylor
//Drive Train of Robot


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.RobotMap;
import frc.ecommons.Constants;

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
  boolean eResetLoop = false;
  boolean forwardLoop = false;
  boolean go = false;

  double driveSpeed = 0.3;

  ShuffleboardTab tab;
  NetworkTableEntry rightEncoderEntry;
  NetworkTableEntry currentGearEntry;
  NetworkTableEntry leftEncoderEntry;

  // ShuffleboardTab tab = Shuffleboard.getTab("max motor speeds");
  // private NetworkTableEntry maxSpeed = tab.add("Drive Speed", 0)
  //                                             .withWidget(BuiltInWidgets.kNumberSlide)
  //                                             .withProperties(Map.of("min", 0, "max", 1))
  //                                             .getEntry();

  public void TalonConfig() {
    //Configs Talon to default
    m_rMaster.configFactoryDefault();
    m_lMaster.configFactoryDefault();
    m_rSlave1.configFactoryDefault();
    m_rSlave2.configFactoryDefault();
    m_lSlave1.configFactoryDefault();
    m_lSlave2.configFactoryDefault();

    //Motors go right way
    m_rMaster.setSensorPhase(false);
    m_lMaster.setSensorPhase(false);
    m_rSlave1.setSensorPhase(false);
    m_rSlave2.setSensorPhase(false);
    m_lSlave1.setSensorPhase(false);
    m_lSlave2.setSensorPhase(false);





    
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

    m_rMaster.setNeutralMode(NeutralMode.Brake);
    m_lMaster.setNeutralMode(NeutralMode.Brake);
    TalonConfig();
    tab = Shuffleboard.getTab("DriveTrain");
    rightEncoderEntry = tab.add("Right Encoder", 0).getEntry();
    currentGearEntry = tab.add("Current Gear", lowGear).getEntry();
    leftEncoderEntry = tab.add("Left Encoder", 0).getEntry();    
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

    if (m_joy.getRawButton(3) && forwardLoop == false) {
      forwardLoop = true;
      // m_rMaster.setSelectedSensorPosition(0);
      // m_lMaster.setSelectedSensorPosition(0);
    }
    if (m_rMaster.getSelectedSensorPosition() > -50000 && m_lMaster.getSelectedSensorPosition() > -50000 && forwardLoop) {
     go = true;

    } else {
      forwardLoop = false;
      go = false;
      m_lMaster.set(ControlMode.PercentOutput, 0);
      m_rMaster.set(ControlMode.PercentOutput, 0);
    }
    if (go) {
      m_rMaster.set(ControlMode.PercentOutput, -0.5);
      m_lMaster.set(ControlMode.PercentOutput, -0.5);

    }

    if (m_joy.getRawButton(Constants.encoderReset) && eResetLoop == false) {
      eResetLoop = true;

      m_rMaster.setSelectedSensorPosition(0);
      m_lMaster.setSelectedSensorPosition(0);


    }
    if (!m_joy.getRawButton(Constants.encoderReset)) {
      eResetLoop = false;
    }

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

    
    //Equation for ARCADE DRIVE
    double xAxis, yAxis;
				xAxis = m_joy.getRawAxis(Constants.xAxis);
				yAxis = m_joy.getRawAxis(Constants.yAxis);
				
				//Equation for Arcade Drive
				double leftSide, rightSide;
				rightSide = yAxis + xAxis;
        leftSide = xAxis - yAxis;
    
    //Percent drive output with slave follows
    m_rMaster.set(ControlMode.PercentOutput, rightSide * driveSpeed);
      m_rSlave1.follow(m_rMaster);
      m_rSlave2.follow(m_rMaster);

    m_lMaster.set(ControlMode.PercentOutput, leftSide * driveSpeed);
      m_lSlave1.follow(m_lMaster);
      m_lSlave2.follow(m_lMaster);

      

  }

  public void report() {

    rightEncoderEntry.setDouble(m_rMaster.getSelectedSensorPosition());
    leftEncoderEntry.setDouble(m_lMaster.getSelectedSensorPosition());
  }

  /**
   * This function is called periodically during test mode.
   */
  
  public void testPeriodic() {
  }
}
