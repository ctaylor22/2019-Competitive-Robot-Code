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

public class Gurny_josh  {

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
  int current_postion;

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

    gBack.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    gBack.setSelectedSensorPosition(0);
    current_postion = gBack.getSelectedSensorPosition();
    setUpPID();

    gFront.configNominalOutputForward(0);
    gFront.configNominalOutputReverse(0);
    gFront.configPeakOutputForward(1);
    gFront.configPeakOutputReverse(-1);
    gFront.configPeakCurrentLimit(8);
    gFront.configContinuousCurrentLimit(6);

    gFront.configAllowableClosedloopError(0, 0, 0);
    gFront.config_kF(0, 0);
    gFront.config_kP(0, 0.5);
    gFront.config_kI(0, 0);
    gFront.config_kD(0, 0);
    gFront.config_IntegralZone(0, 1);

    m_navX = new NavX();
    manual_or_hold_chooser.setDefaultOption("Manual", true);
    manual_or_hold_chooser.addOption("Hold Height", false);
    gurneyTab.add("Mode", manual_or_hold_chooser).withWidget(BuiltInWidgets.kSplitButtonChooser);
  }

  private void setUpPID() {
    gBack.config_kP(0, 1);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(700);
    gBack.configMotionCruiseVelocity(1000);
  }
  
  private void setDownPID() {
    gBack.config_kP(0, 0.1);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(700);
    gBack.configMotionCruiseVelocity(1200);
  }

  private void setHoldPID() {
    gBack.config_kP(0, 3);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 10);
    gBack.config_kF(0, 0);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(700);
    gBack.configMotionCruiseVelocity(450);
  }

  public void autonomousInit() {
    gBack.setSelectedSensorPosition(0);
  }


  
  public void autonomousPeriodic() {
  }

  public void teleopInit() {
    gBack.setSelectedSensorPosition(0);
  }
  
  public void teleopPeriodic() {
    // reset encoder
    if (m_joy.getRawButton(Constants.gurneyEncoderReset)) {
      gBack.setSelectedSensorPosition(0);
    }

    // drive    
    dashFrontSpeed = 0.5;
    dashBackSpeed = 0.4;
    dashDriveSpeed = 1;
    if (m_joy.getRawAxis(Constants.gDriveBack) < 0.1) {
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
    } else if (m_joy.getRawAxis(Constants.gDriveForward) < 0.1) {
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashDriveSpeed);
    }

    if (m_joy.getRawButton(Constants.gurneyGoUp)) {
      /*
       * Robot tilts forward ==> positive pitch ==> add to front output
       * 
       */
      // up
      // call motion magic with a set point of ~32000
      setUpPID();
      gBack.set(ControlMode.MotionMagic, 42000);

      // accelerometer PID for front
      double pitch_error = m_navX.getPitch() - 2;
      double front_kF = 0.5;
      double front_kP = 0.13;
      double output = front_kF + (front_kP)*pitch_error;
      gFront.set(ControlMode.PercentOutput, output);
      current_postion = gBack.getSelectedSensorPosition();
    }
    else if (m_joy.getRawButton(Constants.gurneyGoDown)) {
      // down
      setDownPID();
      gBack.set(ControlMode.MotionMagic, 10);
      double pitch_error = m_navX.getPitch() - 2;
      double front_kF = 0.35;
      double front_kP = 0.11;
      double output = front_kF + (front_kP)*pitch_error;
      gFront.set(ControlMode.PercentOutput, output);
      current_postion = gBack.getSelectedSensorPosition();
    }
    else if (gBack.getSelectedSensorPosition() > 5000) {
      // hold when encoder reads a rotation
      setHoldPID();
      gBack.set(ControlMode.MotionMagic, current_postion);

      // accelerometer PID for front
      double pitch_error = m_navX.getPitch() - 2;
      double front_kF = 0.4;
      double front_kP = 0.12;
      double output = front_kF + (front_kP)*pitch_error;
      gFront.set(ControlMode.PercentOutput, output);
    }
    else {
      // manual
      gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * -dashFrontSpeed);
      gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * -dashBackSpeed);
    }
    
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
