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
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

public class Manipulator  {

  public static double elevatorUpBecauseOfHatch = 500;

  Joystick m_joy;

  DoubleSolenoid grabber;
  DoubleSolenoid dualActionGrabber;
  DoubleSolenoid hatchPickup;

  String grabState = "N/A";
  String inOutState = "N/A";

  String onePushState = "NotExecuting";

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

  public static boolean hatchIsDown = false;

  boolean hatchPickLoop = false;
  public static boolean elevatorDown = false;
  //boolean grabLoop = false;
  int hold_position = 0;

  double motorGo = 0;

  ShuffleboardTab testMode = Shuffleboard.getTab("Test Mode");
  ShuffleboardTab tab = Shuffleboard.getTab("Beginning Game");

  NetworkTableEntry grabEncoderResetEntry = testMode.add("Manip Encoder Reset", false)
                                                    .withWidget(BuiltInWidgets.kToggleButton)
                                                    .getEntry();
  NetworkTableEntry grabberStateEntry = tab.add("Disc Up/Down State", grabState)
                                            .withPosition(1, 3).getEntry();

  NetworkTableEntry dualActionGrabberEntry = tab.add("Disc In/Out State", inOutState)
                                                .withPosition(2, 3).getEntry();

  Timer oneDeliverTimer;
                                               


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

    //Reverse is up... Forward is down... change if necessary
    hatchPickup = new DoubleSolenoid(RobotMap.hatchPneumatic1, RobotMap.hatchPneumatic2);
    

    manipWheels = new VictorSPX(RobotMap.manipWheels);
    manipUpDown = new TalonSRX(RobotMap.manipUpDown);

    talonConfig();
    setUpPID();
    
    grabber.set(DoubleSolenoid.Value.kReverse);
    grabState = "Up";
    dualActionGrabber.set(DoubleSolenoid.Value.kForward);
    inOutState = "Out";

    hatchPickup.set(DoubleSolenoid.Value.kReverse);
    
    manipulator = false;

    motorForLoop = false;
    motorBackLoop = false; 
    oneDeliverTimer = new Timer();
  }

  public void talonConfig() {
    manipUpDown.configFactoryDefault();
    manipWheels.configClosedloopRamp(0.1);
    
    manipUpDown.setSensorPhase(true);
    manipUpDown.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    manipUpDown.setNeutralMode(NeutralMode.Coast);
    manipUpDown.configForwardSoftLimitThreshold(-50);
    manipUpDown.configForwardSoftLimitEnable(true);
    // this reverse limit is working, gravity takes over and lets it fall when 
    // the trigger is pushed, causing the closed loop to deactivate
    manipUpDown.configReverseSoftLimitThreshold(-1400);
    manipUpDown.configReverseSoftLimitEnable(true);
    manipUpDown.configPeakOutputForward(0.3);
    manipUpDown.configPeakOutputReverse(-0.17);
    manipUpDown.configNominalOutputForward(0);
    manipUpDown.configNominalOutputReverse(0);
    manipUpDown.configMotionAcceleration(0);    
    manipUpDown.configMotionCruiseVelocity(0);
  }

  private void resetPID() {
    //Originally 50, 50
    // manipUpDown.configFactoryDefault();

    //Originally 0
    manipUpDown.config_kP(0, 0);
    manipUpDown.config_kI(0, 0);
    manipUpDown.config_kD(0, 0);
    manipUpDown.config_kF(0, 0);
    manipUpDown.config_IntegralZone(0, 100);
  }

  private void setUpPID() {

    //Originally 0.7
    manipUpDown.config_kP(0, 0.65);
    manipUpDown.config_kI(0, 0);
    manipUpDown.config_kD(0, 0);
    manipUpDown.config_kF(0, 0);
    manipUpDown.config_IntegralZone(0, 100);
  }

  public void robotPeriodic() {

  }
  
  public void autonomousInit() {
    manipulator = false;
    motorForLoop = false;
    motorBackLoop = false;
  }

  public void autonomousPeriodic() {
    teleopPeriodic();

  }

  public void teleopInit() {
    setUpPID();

  }
  
  public void teleopPeriodic() {



    // Hatch Pickup
    // If Right D-Pad pressed... then hatch pickup should go down elevator goes up
    // Click again and hatch pickup will go up
    // Elevator will stay in same position
    if (m_joy.getPOV() == 90 && !hatchPickLoop) {
      hatchPickLoop = true;
      if (hatchPickup.get() == Value.kForward) {
        hatchPickup.set(Value.kReverse);
      } else if (hatchPickup.get() == Value.kReverse) {
        hatchIsDown = true;
        hatchPickup.set(Value.kForward);
      }
      

      hatchPickLoop = false;
    }

    // disk grabber flippy thing
    if (m_joy.getRawButtonReleased(Constants.discGrabber)) {
      if (grabber.get() == (Value.kReverse)) {
        grabber.set(DoubleSolenoid.Value.kForward);
        grabState = "Up";
        // currentDiscGrabberState.setString("out");
      } else if (grabber.get() == (Value.kForward)) {
        grabber.set(DoubleSolenoid.Value.kReverse);
        grabState = "Down";
        // .setString(grabState);
      }
    }

    // disk pusher
    if (m_joy.getRawButtonReleased(Constants.dualActionGrabber)) {
      if (dualActionGrabber.get() == (Value.kForward)) {
        dualActionGrabber.set(DoubleSolenoid.Value.kReverse);
        inOutState = "In";
      } else if (dualActionGrabber.get() == (Value.kReverse)) {
        dualActionGrabber.set(DoubleSolenoid.Value.kForward);
        inOutState = "Out";
      }
    }

    //One Button Delivery
    if (onePushState == "NotExecuting") {
      oneDeliverTimer.reset();
      // onePushState = "Executing";
      if (m_joy.getRawButtonReleased(Constants.onPushDelivery)) {
        grabber.set(Value.kForward);
        onePushState = "grabberOut";
      } 
    } else if (onePushState == "grabberOut") {
      dualActionGrabber.set(Value.kReverse);
      oneDeliverTimer.start();
      onePushState = "pushOut";
    } else if (onePushState == "pushOut") {
      if (oneDeliverTimer.get() > 0.25 && oneDeliverTimer.get() < 0.75) {
        elevatorDown = true;
        onePushState = "NotExecuting";
      } else if (oneDeliverTimer.get() > 0.75) {
        
      }

    } 
    // SmartDashboard.putNumber("Timer", oneDeliverTimer.get());
    // manipulator arms
    if (m_joy.getRawButtonReleased(Constants.manipCargoPreset)){
      hold_position = -740;
    }
    else if (m_joy.getRawAxis(Constants.manipulatorUp) < 0.1 && m_joy.getRawAxis(Constants.manipulatorDown) < 0.1) {
      manipUpDown.set(ControlMode.Position, hold_position);
      // manipUpDown.set(ControlMode.PercentOutput, 0);
    } 
    else if (m_joy.getRawAxis(Constants.manipulatorUp) < 0.1) {
      manipUpDown.set(ControlMode.PercentOutput, -1 * m_joy.getRawAxis(Constants.manipulatorDown));
      hold_position = manipUpDown.getSelectedSensorPosition();
      
    }
    else if (m_joy.getRawAxis(Constants.manipulatorDown) < 0.1) {
      manipUpDown.set(ControlMode.PercentOutput, 1 * m_joy.getRawAxis(Constants.manipulatorUp));
      hold_position = manipUpDown.getSelectedSensorPosition();
      if (hold_position > -50) hold_position = -60;

    }
    // ball grabber wheels
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
    if (m_joy.getRawAxis(Constants.elevatorUp) < 0.2) {
      manipUpDown.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorDown) * 0.3);
    } else if (m_joy.getRawAxis(Constants.elevatorDown) < 0.2) {
      manipUpDown.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorUp) * -1 * 0.3);
    }
    if (m_joy.getRawButtonReleased(1)) {
      manipUpDown.setSelectedSensorPosition(0);
    } 

  }
}
