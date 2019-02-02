//Conner Taylor
//Gurny/Climbing System


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;
import frc.robot.NavX;

public class Gurny  {

  Joystick m_joy;


  WPI_VictorSPX gBack;
  
  WPI_TalonSRX gFront;

  WPI_TalonSRX gDrive;
  
  NavX m_navX;


  public void robotInit(Joystick j) {

    m_joy = j;


    gBack = new WPI_VictorSPX(RobotMap.gBack);

    gFront = new WPI_TalonSRX(RobotMap.gFront);

    gDrive = new WPI_TalonSRX(RobotMap.gDrive);

    m_navX = new NavX();
  }

  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

 
  
  public void teleopPeriodic() {

    gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * 0.7);

    gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * -0.7);
     

    if (m_joy.getRawAxis(Constants.gDriveForward) == 0) {

      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * 0.3);
      
    }
    if (m_joy.getRawAxis(Constants.gDriveBack) == 0) {
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -0.3);
    }
  }

  public void balanceAtVelocity(double output) {
    double pitch = m_navX.getPitch();
    setFront(output-(pitch/90));
    setBack(output+(pitch/90));
  }
  
  public void setFront(double output) {
    gFront.set(ControlMode.PercentOutput, output);
  }

  public void setBack(double output) {
    gBack.set(ControlMode.PercentOutput, output);
  }

  public void report() {
      
}


  
  public void testPeriodic() {
  }
}
