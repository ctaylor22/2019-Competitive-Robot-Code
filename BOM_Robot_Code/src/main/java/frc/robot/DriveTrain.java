//Conner Taylor
//Drive Train of Robot


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.ecommons.RobotMap;
import frc.ecommons.Constants;

 
public class DriveTrain  {

  

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
  DoubleSolenoid leftSolenoid;
  DoubleSolenoid rightSolenoid;


  //Loops
  boolean dgLoop = false;


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
    leftSolenoid = new DoubleSolenoid(RobotMap.leftDogGear1, RobotMap.leftDogGear2);
    rightSolenoid = new DoubleSolenoid(RobotMap.rightDogGear1, RobotMap.rightDogGear2);

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
    rightSolenoid.set(DoubleSolenoid.Value.kForward);
    leftSolenoid.set(DoubleSolenoid.Value.kForward);

  } 
  public void teleopPeriodic() {
    //Dog Gear Shift
    if (m_joy.getRawButton(Constants.gearShift) && !dgLoop) {
      dgLoop = true;
      if (rightSolenoid.get() == (Value.kForward) && leftSolenoid.get() == (Value.kForward)) {
        rightSolenoid.set(DoubleSolenoid.Value.kReverse);
        leftSolenoid.set(DoubleSolenoid.Value.kReverse);
      } else if (rightSolenoid.get() == (Value.kReverse) && leftSolenoid.get() == (Value.kReverse)) {
        rightSolenoid.set(DoubleSolenoid.Value.kForward);
        leftSolenoid.set(DoubleSolenoid.Value.kForward);
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
    m_rMaster.set(ControlMode.PercentOutput, rightSide);
      m_rSlave1.follow(m_rMaster);
      m_rSlave2.follow(m_rMaster);

    m_lMaster.set(ControlMode.PercentOutput, leftSide);
      m_lSlave1.follow(m_lMaster);
      m_lSlave2.follow(m_lMaster);

  }

  public void report() {

  }

  /**
   * This function is called periodically during test mode.
   */
  
  public void testPeriodic() {
  }
}
