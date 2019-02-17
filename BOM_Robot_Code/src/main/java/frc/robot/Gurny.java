//Conner Taylor
//Gurny/Climbing System


package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

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
  ShuffleboardTab gurneyTab = Shuffleboard.getTab("Gurney");
  NetworkTableEntry gurneyPitchEntry = gurneyTab.add("Gurney Pitch", 0)
                                      .withWidget(BuiltInWidgets.kTextView)
                                      .getEntry();
  NetworkTableEntry gurneyFrontSpeedEntry = gurneyTab.add("Gurney Front Motor Speed", 0)
  .withWidget(BuiltInWidgets.kTextView)
  .getEntry();
  NetworkTableEntry gurneyBackSpeedEntry = gurneyTab.add("Gurney Back Motor Speed", 0)
  .withWidget(BuiltInWidgets.kTextView)
  .getEntry();
  NetworkTableEntry gurneyDriveSpeedEntry = gurneyTab.add("Gurney Drive Motor Speed", 0)
  .withWidget(BuiltInWidgets.kTextView)
  .getEntry();
 
  SendableChooser<Boolean> manual_or_hold_chooser = new SendableChooser<Boolean>();

  // ShuffleboardTab MaxSpeedTab = Shuffleboard.getTab("Max Speed");
  // NetworkTableEntry frontGurneyEntry;
  // NetworkTableEntry backGurneyEntry;
  // NetworkTableEntry driveGurneyEntry;


  public void robotInit(Joystick j) {
  //   frontGurneyEntry = MaxSpeedTab.add("Front Gurney Speed", 0.5)
  //   .withWidget(BuiltInWidgets.kNumberSlider)
  //   .withProperties(Map.of("Min", 0, "Max", 1))
  //   .getEntry();
  //   backGurneyEntry = MaxSpeedTab.add("Back Gurney Speed", 0.5)
  // .withWidget(BuiltInWidgets.kNumberSlider)
  // .withProperties(Map.of("Min", 0, "Max", 1))
  // .getEntry();
  //   driveGurneyEntry = MaxSpeedTab.add("Drive Gurney Speed", 0.5)
  //   .withWidget(BuiltInWidgets.kNumberSlider)
  //   .withProperties(Map.of("Min", 0, "Max", 1))
  //   .getEntry();

    m_joy = j;

    gBack = new WPI_TalonSRX(RobotMap.gBack);

    gFront = new WPI_TalonSRX(RobotMap.gFront);

    gDrive = new WPI_VictorSPX(RobotMap.gDrive);

    gDrive.configFactoryDefault();
    gBack.configFactoryDefault();
    gFront.configFactoryDefault();

    m_navX = new NavX();
  }

  public void autonomousInit() {
  }


  
  public void autonomousPeriodic() {
  }

  public void teleopInit() {
  }

  public void teleopPeriodic() {
    // Y button enable, RT drive
    if (m_joy.getRawButton(Constants.gurneyGoUp)) {
      if (m_joy.getRawAxis(Constants.gDrive) < Constants.gSafteySpeed) {
        gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDrive) * -dashFrontSpeed);
        balanceFront(Constants.frontDriveP);
      } else if (true) { //Needs to activate only when back gurny motor is above a certain point and is not being driven
        gBack.set(ControlMode.PercentOutput, Constants.gSafteySpeed);
        double pitch = m_navX.getPitch();
        if (pitch > .1 || pitch < -.1) {
          balanceFront(Constants.frontDriveP);
        } else {
          gFront.set(ControlMode.PercentOutput, Constants.gSafteySpeed);
        }
      } 
    }
    // else {
    //   // manual driving
    //   dashFrontSpeed = 0.5;
    //   dashBackSpeed = 0.4;
    //   dashDriveSpeed = 1;
    //   gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * -dashFrontSpeed);
    //   gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);

    //   if (m_joy.getRawAxis(Constants.gDrive) < 0.1) {
    //     gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDrive) * dashDriveSpeed);
    //   } else if (m_joy.getRawAxis(Constants.gDrive) < 0.1) {
    //     gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDrive) * -dashDriveSpeed);
    //   }
    // }
  }

  public void balanceAtVelocity(double output) {
    //double pitch = m_navX.getPitch();
    //double pFactorFront = output*(pitch/45);
    //double pFactorBack = output*(pitch/45);
    //setFront(output-pFactorFront);
    //setBack(output+pFactorBack);
  }

  public void balanceFront(double pfactor) {
    //Balances the front accoring to the NavX tilt
    double pitch = m_navX.getPitch();
    double speed = -pfactor*(pitch/15);
    if (speed < 0) {
      speed = 0;
    }
    gFront.set(ControlMode.PercentOutput, speed * -dashFrontSpeed);
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
    // gDriveSpeedEntry.setDouble(m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
    // gFrontSpeedEntry.setDouble(m_joy.getRawAxis(Constants.gUpFront) * -dashFrontSpeed);
    // gBackSpeedEntry.setDouble(m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);

    gurneyPitchEntry.setDouble(m_navX.getPitch());
    
}

  public void testInit() {

  }
  
  public void testPeriodic() {
  }
}
