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
  DoubleSolenoid dualActionGrabber;

  String grabState = "N/A";
  String inOutState = "N/A";

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
  //boolean grabLoop = false;
  int hold_position = 0;

  double motorGo = 0;

  ShuffleboardTab testMode = Shuffleboard.getTab("Test Mode");
  ShuffleboardTab tab = Shuffleboard.getTab("Beginning Game");

  NetworkTableEntry grabEncoderResetEntry = testMode.add("Manip Encoder Reset", false)
                                                    .withWidget(BuiltInWidgets.kToggleButton)
                                                    .getEntry();
  NetworkTableEntry grabberStateEntry = tab.add("Disc Up/Down State", grabState)
                                           .getEntry();

  NetworkTableEntry dualActionGrabberEntry = tab.add("Disc In/Out State", inOutState)
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
    dualActionGrabber = new DoubleSolenoid(RobotMap.dual1, RobotMap.dual2);

    manipWheels = new VictorSPX(RobotMap.manipWheels);
    manipUpDown = new TalonSRX(RobotMap.manipUpDown);

    talonConfig();
    manipUpDown.setSelectedSensorPosition(0);
    setUpPID();
    
    grabber.set(DoubleSolenoid.Value.kReverse);
    grabState = "Up";
    dualActionGrabber.set(DoubleSolenoid.Value.kReverse);
    inOutState = "Out";
    
  }

  public void talonConfig() {
    manipUpDown.configFactoryDefault();
    manipWheels.configClosedloopRamp(0.1);
    manipUpDown.setSensorPhase(false);
    manipUpDown.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    manipUpDown.setNeutralMode(NeutralMode.Coast);
    manipUpDown.configForwardSoftLimitThreshold(1000);
    manipUpDown.configForwardSoftLimitEnable(true);
    manipUpDown.configReverseSoftLimitThreshold(0);
    manipUpDown.configReverseSoftLimitEnable(true);
    manipUpDown.configPeakOutputForward(0.22);
    manipUpDown.configPeakOutputReverse(-0.40);
    manipUpDown.configNominalOutputForward(0);
    manipUpDown.configNominalOutputReverse(0);
  }

  public void robotPeriodic() {

  }
  
  public void autonomousInit() {
    teleopInit();
  }


  
  public void autonomousPeriodic() {
    teleopPeriodic();

  }

  private void resetPID() {
    //Originally 50, 50
    manipUpDown.configFactoryDefault();
    manipUpDown.configMotionAcceleration(0);    
    manipUpDown.configMotionCruiseVelocity(0);

    //Originally 0.7
    manipUpDown.config_kP(0, 0);
    manipUpDown.config_kI(0, 0);
    manipUpDown.config_kD(0, 0);
    manipUpDown.config_kF(0, 0);
    manipUpDown.config_IntegralZone(0, 100);
  }

  private void setUpPID() {
    //Originally 50, 50
    manipUpDown.configMotionAcceleration(50);    
    manipUpDown.configMotionCruiseVelocity(50);

    //Originally 0.7
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
    talonConfig();

  }
  
  public void teleopPeriodic() {
    if (grabEncoderResetEntry.getBoolean(true)) {
      manipUpDown.setSelectedSensorPosition(0);
      grabEncoderResetEntry.setBoolean(false);
    }

    if (m_joy.getRawButtonReleased(Constants.discGrabber)) {
      if (grabber.get() == (Value.kForward)) {
        grabber.set(DoubleSolenoid.Value.kReverse);
        grabState = "Down";
        //currentDiscGrabberState.setString(out);
      } else if (grabber.get() == (Value.kReverse)) {
        grabber.set(DoubleSolenoid.Value.kForward);
        grabState = "Up";
        //currentDiscGrabberState.setString(in);
      }
    }

    if (m_joy.getRawButtonReleased(Constants.dualActionGrabber)) {
      if (dualActionGrabber.get() == (Value.kForward)) {
        dualActionGrabber.set(DoubleSolenoid.Value.kReverse);
        inOutState = "In";
      } else if (dualActionGrabber.get() == (Value.kReverse)) {
        dualActionGrabber.set(DoubleSolenoid.Value.kForward);
        inOutState = "Out";
      }
    }

    // setUpPID();
    // if (m_joy.getRawAxis(Constants.manipulatorUp) < 0.1 && m_joy.getRawAxis(Constants.manipulatorDown) < 0.1) {
    //   manipUpDown.set(ControlMode.Position, hold_position);
    // }
    // else if (m_joy.getRawAxis(Constants.manipulatorUp) < 0.1) {
    //   manipUpDown.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.manipulatorDown));
    //   hold_position = manipUpDown.getSelectedSensorPosition();
    //   if (hold_position < 0) hold_position = 0;
    // }
    // else if (m_joy.getRawAxis(Constants.manipulatorDown) < 0.1) {
    //   manipUpDown.set(ControlMode.PercentOutput, -1 * m_joy.getRawAxis(Constants.manipulatorUp));
    //   hold_position = manipUpDown.getSelectedSensorPosition();
    // }

      
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

    grabberStateEntry.setString(grabState);
    dualActionGrabberEntry.setString(inOutState);      
}


  
  public void testPeriodic() {
    resetPID();
    if (m_joy.getRawButton(2)) {
      manipUpDown.set(ControlMode.PercentOutput, 0.2);
    } else if (m_joy.getRawButton(4)) {
      manipUpDown.set(ControlMode.PercentOutput, -0.2);
    } else {
      manipUpDown.set(ControlMode.PercentOutput, 0);
    }
    if (m_joy.getRawButtonReleased(1)) {
      manipUpDown.setSelectedSensorPosition(0);
    }

  }
}
