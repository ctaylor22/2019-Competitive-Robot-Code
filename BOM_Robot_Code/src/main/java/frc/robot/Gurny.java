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

public class Gurny  {

  Joystick m_joy;


  WPI_VictorSPX gBack;
  
  WPI_TalonSRX gFront;

  WPI_TalonSRX gDrive;
  double dashFrontSpeed, dashBackSpeed, dashDriveSpeed;
  



  public void robotInit(Joystick j) {
    SmartDashboard.putNumber("Front Gurney Speed", 0.25);
    SmartDashboard.putNumber("Back Gurney Speed", 0.25);
    SmartDashboard.putNumber("Drive Gurney Speed", 0.25);
    m_joy = j;


    gBack = new WPI_VictorSPX(RobotMap.gBack);

    gFront = new WPI_TalonSRX(RobotMap.gFront);

    gDrive = new WPI_TalonSRX(RobotMap.gDrive);

  }

  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

 
  
  public void teleopPeriodic() {

    gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * dashFrontSpeed);

    gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);
     

   

      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
      
    
    
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashDriveSpeed);
    
  }



  

  public void report() {
    dashFrontSpeed = SmartDashboard.getNumber("Front Gurney Speed", 0);
    dashBackSpeed = SmartDashboard.getNumber("Back Gurney Speed", 0);
    dashDriveSpeed = SmartDashboard.getNumber("Drive Gurney Speed", 0);
}


  
  public void testPeriodic() {
  }
}
