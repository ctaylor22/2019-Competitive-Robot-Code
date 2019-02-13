//Conner Taylor
//Gurny/Climbing System


package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;
import frc.robot.NavX;

public class Gurny  {

  Joystick m_joy;


  WPI_TalonSRX gBack;
  
  WPI_TalonSRX gFront;

  WPI_VictorSPX gDrive;
  double dashFrontSpeed, dashBackSpeed, dashDriveSpeed;
  
  NavX m_navX;
  ShuffleboardTab Gurney = Shuffleboard.getTab("Gurney");
 // NetworkTableEntry gurneyTiltEntry = Gurney.add("Gurney Pitch", 0)
 //                                      .withWidget(BuiltInWidgets.kGyro)
 //                                      .getEntry();
  NetworkTableEntry gFrontSpeedEntry;
  NetworkTableEntry gBackSpeedEntry;
  NetworkTableEntry gDriveSpeedEntry;
 

  ShuffleboardTab MaxSpeedTab = Shuffleboard.getTab("Max Speed");
  NetworkTableEntry frontGurneyEntry;
  NetworkTableEntry backGurneyEntry;
  NetworkTableEntry driveGurneyEntry;


  public void robotInit(Joystick j) {
    gFrontSpeedEntry = Gurney.add("Gurney Front Motor Speed", 0)
    .withWidget(BuiltInWidgets.kSpeedController)
    .getEntry();
    gBackSpeedEntry = Gurney.add("Gurney Back Motor Speed", 0)
    .withWidget(BuiltInWidgets.kTextView)
    .getEntry();
    gDriveSpeedEntry = Gurney.add("Gurney Drive Motor Speed", 0)
    .withWidget(BuiltInWidgets.kTextView)
    .getEntry();

    frontGurneyEntry = MaxSpeedTab.add("Front Gurney Speed", 0.5)
    .withWidget(BuiltInWidgets.kNumberSlider)
    .withProperties(Map.of("Min", 0, "Max", 1))
    .getEntry();
    backGurneyEntry = MaxSpeedTab.add("Back Gurney Speed", 0.5)
  .withWidget(BuiltInWidgets.kNumberSlider)
  .withProperties(Map.of("Min", 0, "Max", 1))
  .getEntry();
    driveGurneyEntry = MaxSpeedTab.add("Drive Gurney Speed", 0.5)
    .withWidget(BuiltInWidgets.kNumberSlider)
    .withProperties(Map.of("Min", 0, "Max", 1))
    .getEntry();

    m_joy = j;

    gBack = new WPI_TalonSRX(RobotMap.gBack);

    gFront = new WPI_TalonSRX(RobotMap.gFront);

    gDrive = new WPI_VictorSPX(RobotMap.gDrive);

    gDrive.configFactoryDefault();

    m_navX = new NavX();
  }

  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

 
  
  public void teleopPeriodic() {
    // Y button enable, RT drive
    if (m_joy.getRawButton(Constants.gUpLevelEnable)) {
      if (m_joy.getRawAxis(Constants.gDriveForward) < 0.1) {
        gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashFrontSpeed);
      }
      balanceBack(Constants.backDriveP);
    }
    else {
      dashFrontSpeed = 0.5;
      dashBackSpeed = 0.4;
      dashDriveSpeed = 1;
      gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * -dashFrontSpeed);
      gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);

      if (m_joy.getRawAxis(Constants.gDriveBack) < 0.1) {
        gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
      } else if (m_joy.getRawAxis(Constants.gDriveForward) < 0.1) {
         gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashDriveSpeed);
       }
      
    }
  }

  public void balanceAtVelocity(double output) {
    //double pitch = m_navX.getPitch();
    //double pFactorFront = output*(pitch/45);
    //double pFactorBack = output*(pitch/45);
    //setFront(output-pFactorFront);
    //setBack(output+pFactorBack);
  }

  public void balanceBack(double pfactor) {
    //TODO: Find correct callibration for pfactor and find correct axis to use (from pitch, roll, and yaw)
    double pitch = m_navX.getPitch();
    double speed = -pfactor*(pitch/45);
    gBack.set(ControlMode.PercentOutput, speed * -dashBackSpeed);
  }
  
  public void setFront(double output) {
    gFront.set(ControlMode.PercentOutput, output);
  }

  public void setBack(double output) {
    gBack.set(ControlMode.PercentOutput, output);
  }

  public void report() {
    // dashFrontSpeed = frontGurneyEntry.getDouble(0);
    // dashBackSpeed = backGurneyEntry.getDouble(0);
    // dashDriveSpeed = driveGurneyEntry.getDouble(0);

   // gurneyTiltEntry.setDouble(m_navX.getPitch());
    gDriveSpeedEntry.setDouble(m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
    gFrontSpeedEntry.setDouble(m_joy.getRawAxis(Constants.gUpFront) * -dashFrontSpeed);
    gBackSpeedEntry.setDouble(m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);
    
}

  public void testInit() {
    m_navX.testInit();
  }
  
  public void testPeriodic() {
    m_navX.testPeriodic();
  }
}
