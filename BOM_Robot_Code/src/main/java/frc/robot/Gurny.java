//Conner Taylor
//Gurny/Climbing System


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;
import frc.robot.NavX;

public class Gurny  {

  Joystick m_joy;


  WPI_VictorSPX gBack;
  
  WPI_TalonSRX gFront;

  WPI_TalonSRX gDrive;
  double dashFrontSpeed, dashBackSpeed, dashDriveSpeed;
  
  NavX m_navX;


  public void robotInit(Joystick j) {
    SmartDashboard.putNumber("Front Gurney Speed", 0.25);
    SmartDashboard.putNumber("Back Gurney Speed", 0.25);
    SmartDashboard.putNumber("Drive Gurney Speed", 0.25);
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
    // Y button enable, RT drive
    if (m_joy.getRawButton(Constants.gUpLevelEnable)) {
      balanceAtVelocity(0.25*m_joy.getRawAxis(Constants.gDriveForward));
    }
    else {
      dashBackSpeed = 0.25;
      dashDriveSpeed = 0.5;
      gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * -dashBackSpeed);
      gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);

      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashDriveSpeed);
    }
  }

  public void balanceAtVelocity(double output) {
    double pitch = m_navX.getPitch();
    setFront(output-(output*(pitch/90)));
    setBack(output+(output*(pitch/90)));
  }
  
  public void setFront(double output) {
    gFront.set(ControlMode.PercentOutput, output);
  }

  public void setBack(double output) {
    gBack.set(ControlMode.PercentOutput, output);
  }

  public void report() {
    dashFrontSpeed = SmartDashboard.getNumber("Front Gurney Speed", 0);
    // dashBackSpeed = SmartDashboard.getNumber("Back Gurney Speed", 0);
    // dashDriveSpeed = SmartDashboard.getNumber("Drive Gurney Speed", 0);
}


  
  public void testPeriodic() {
  }
}
