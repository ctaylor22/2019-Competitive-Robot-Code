//Conner Taylor
//Manipulator of Robot


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

public class Manipulator  {
  Joystick m_joy;
  DoubleSolenoid grabber;
  TalonSRX manipUpDown;
  VictorSPX manipWheels;
  boolean manipulator = false;
  boolean manipulator2 = false;
  boolean motorForLoop = false;
  boolean motorBackLoop = false;
  boolean manipUpDownLoop = false;
  boolean manipToggle = false;
  boolean hasBeenToggled = false;
  boolean isDownFirstLoop = true;
  int hold_position = 0;

  double motorGo = 0;

  ShuffleboardTab testMode = Shuffleboard.getTab("Test Mode");

  NetworkTableEntry grabEncoderResetEntry = testMode.add("Manip Encoder Reset", false)
                                                    .withWidget(BuiltInWidgets.kToggleButton)
                                                    .getEntry();


  SendableChooser<Boolean> Enable_OR_Disable = new SendableChooser<Boolean>();

  public void robotInit(Joystick j) {
    Enable_OR_Disable.setDefaultOption("Disable", false);
    Enable_OR_Disable.addOption("Enable", true);
    testMode.add("Manip Closed Loop Enable", Enable_OR_Disable)
            .withWidget(BuiltInWidgets.kSplitButtonChooser)
            .withSize(2,1);


    m_joy = j;
    grabber = new DoubleSolenoid(RobotMap.grab1, RobotMap.grab2);
    manipWheels = new VictorSPX(RobotMap.manipWheels);
    manipUpDown = new TalonSRX(RobotMap.manipUpDown);

    manipUpDown.configFactoryDefault();
    manipWheels.configClosedloopRamp(0.1);
    manipUpDown.setSelectedSensorPosition(0);
    manipUpDown.setSensorPhase(false);
    manipUpDown.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    manipUpDown.setNeutralMode(NeutralMode.Coast);
    manipUpDown.configForwardSoftLimitThreshold(1000);
    manipUpDown.configForwardSoftLimitEnable(true);
    manipUpDown.configReverseSoftLimitThreshold(0);
    manipUpDown.configReverseSoftLimitEnable(true);
    manipUpDown.configPeakOutputForward(0.08);
    manipUpDown.configPeakOutputReverse(-0.3);
    manipUpDown.configNominalOutputForward(0);
    manipUpDown.configNominalOutputReverse(0);
    setUpPID();
  }

  public void robotPeriodic() {

  }
  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

  private void setUpPID() {
    //Originally 50, 50
    manipUpDown.configMotionAcceleration(75);    
    manipUpDown.configMotionCruiseVelocity(75);

    manipUpDown.config_kP(0, 0.7);
    manipUpDown.config_kI(0, 0);
    manipUpDown.config_kD(0, 0);
    manipUpDown.config_kF(0, 0);
    manipUpDown.config_IntegralZone(0, 100);
  }

  public void teleopInit() {
    manipulator = false;
    motorForLoop = false;
    motorBackLoop = false;

  }
  
  public void teleopPeriodic() {
    if (grabEncoderResetEntry.getBoolean(false)) {
      manipUpDown.setSelectedSensorPosition(0);
      grabEncoderResetEntry.setBoolean(false);
    }

    
    setUpPID();
    
    
      if (m_joy.getRawAxis(Constants.manipulatorUp) < 0.1 && m_joy.getRawAxis(Constants.manipulatorDown) < 0.1) {
        manipUpDown.set(ControlMode.Position, hold_position);
      }
      else if (m_joy.getRawAxis(Constants.manipulatorUp) < 0.1) {
        manipUpDown.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.manipulatorDown));
        hold_position = manipUpDown.getSelectedSensorPosition();
        if (hold_position < 0) hold_position = 0;
      }
      else if (m_joy.getRawAxis(Constants.manipulatorDown) < 0.1) {
        manipUpDown.set(ControlMode.PercentOutput, -1 * m_joy.getRawAxis(Constants.manipulatorUp));
        hold_position = manipUpDown.getSelectedSensorPosition();
      }

      
      if (!manipulator && !manipulator2) {
        motorGo = 0;
      }
      if (m_joy.getRawButton(Constants.manipWheelForToggle) && !motorForLoop) {
        motorForLoop = true;
        motorGo = 1;
        manipulator2 = false;
        manipulator = !manipulator;
      }
      if (!m_joy.getRawButton(Constants.manipWheelForToggle)) {
        motorForLoop = false;
      }

      if (m_joy.getRawButton(Constants.manipWheelBackToggle) && !motorBackLoop) {
        motorBackLoop = true;
        motorGo = 2;
        manipulator = false;
        manipulator2 = !manipulator2;
      }
      if (!m_joy.getRawButton(Constants.manipWheelBackToggle)) {
        motorBackLoop = false;
      }

      
      if (motorGo == 0) {
        manipWheels.set(ControlMode.PercentOutput, 0);
      }else if (motorGo == 1) {
        manipWheels.set(ControlMode.PercentOutput, 1);    
      } else if (motorGo == 2) {
        manipWheels.set(ControlMode.PercentOutput, -0.5);
      }


  }

  public void report() {
    
    SmartDashboard.putNumber("Grab Encoder", manipUpDown.getSelectedSensorPosition());
      
}


  
  public void testPeriodic() {

  }
}
