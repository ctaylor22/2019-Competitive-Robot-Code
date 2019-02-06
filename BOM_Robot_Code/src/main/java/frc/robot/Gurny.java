//Conner Taylor
//Gurny/Climbing System


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
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

  ShuffleboardTab MaxSpeed;

  NetworkTableEntry frontGurneyEntry;
  NetworkTableEntry backGurneyEntry;
  NetworkTableEntry driveGurneyEntry;

  public void robotInit(Joystick j) {
    MaxSpeed = Shuffleboard.getTab("Max Speed");

    frontGurneyEntry = MaxSpeed.add("Front Gurney Speed", 0.25).getEntry();
    backGurneyEntry = MaxSpeed.add("Back Gurney Speed", 0.25).getEntry();
    driveGurneyEntry = MaxSpeed.add("Drive Gurney Speed", 0.25).getEntry();

    m_joy = j;


    gBack = new WPI_VictorSPX(RobotMap.gBack);

    gFront = new WPI_TalonSRX(RobotMap.gFront);

    gDrive = new WPI_TalonSRX(RobotMap.gDrive);

    m_navX = new NavX();
    dashBackSpeed = 0.25;
    dashDriveSpeed = 0.5;
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
      
      gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * -dashFrontSpeed);
      gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);

      if (Constants.gDriveBack == 0) {
        gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
      } else if (Constants.gDriveForward == 0) {
        gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashDriveSpeed);
      }
      
     
    }
  }

  public void balanceAtVelocity(double output) {
    double pitch = m_navX.getPitch();
    double pFactor = output*(pitch/45);
    setFront(output-pFactor);
    setBack(output+pFactor);
  }
  
  public void setFront(double output) {
    gFront.set(ControlMode.PercentOutput, output);
  }

  public void setBack(double output) {
    gBack.set(ControlMode.PercentOutput, output);
  }

  public void report() {
    dashFrontSpeed = frontGurneyEntry.getDouble(0);
    dashBackSpeed = backGurneyEntry.getDouble(0);
    dashDriveSpeed = driveGurneyEntry.getDouble(0);
    
}


  
  public void testPeriodic() {
  }
}
