//Conner Taylor
//Drive Train of Robot


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import frc.ecommons.RobotMap;

 
public class DriveTrain  {

  private TalonConfig m_TalonConfig = new TalonConfig();

  // Joysticks/Controllers
  Joystick m_joy;

  //Talons
  WPI_TalonSRX m_rMaster;
  WPI_TalonSRX m_lMaster;
  
  //Victors
  WPI_TalonSRX m_rSlave1;
  WPI_TalonSRX m_rSlave2;
  WPI_TalonSRX m_lSlave1;
  WPI_TalonSRX m_lSlave2;




  public void robotInit(Joystick j) {

    m_TalonConfig.driveMotors(m_rMaster, m_lMaster, m_rSlave1, m_rSlave2, m_lSlave1, m_lSlave2);
    //Joysticks
    m_joy = j;

    //Talons - IDS found in ecommons.RobotMap
    m_rMaster = new WPI_TalonSRX(RobotMap.rMaster);
    m_lMaster = new WPI_TalonSRX(RobotMap.lMaster);

    //Victors - IDS found in ecommons.RobotMap
    m_rMaster = new WPI_TalonSRX(RobotMap.rSlave1);
    m_rMaster = new WPI_TalonSRX(RobotMap.rSlave2);
    m_rMaster = new WPI_TalonSRX(RobotMap.lSlave1);
    m_rMaster = new WPI_TalonSRX(RobotMap.lSlave2);

    


  }


  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

  /**
   * This function is called periodically during operator control.
   */
  
  public void teleopPeriodic() {
    
    //Equation for ARCADE DRIVE
    double xAxis, yAxis;
				xAxis = m_joy.getRawAxis(5);
				yAxis = m_joy.getRawAxis(0);
				
				//Equation for Arcade Drive
				double leftSide, rightSide;
				rightSide = yAxis + xAxis;
        leftSide = xAxis - yAxis;
    
    
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
