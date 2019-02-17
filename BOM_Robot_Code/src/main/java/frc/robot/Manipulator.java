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
  boolean isDownFirstLoop = true;

  double motorGo = 0;

  ShuffleboardTab testMode = Shuffleboard.getTab("Test Mode");

  NetworkTableEntry grabEncoderResetEntry = testMode.add("Manip Encoder Reset", false)
                                                    .withWidget(BuiltInWidgets.kToggleButton)
                                                    .getEntry();


  SendableChooser<Boolean> Enable_OR_Disable = new SendableChooser<Boolean>();

  public void robotInit(Joystick j) {
    Enable_OR_Disable.setDefaultOption("Disable", false);
    Enable_OR_Disable.addOption("Enable", true);
    testMode.add("Manip Closed Loop Enable", Enable_OR_Disable).withWidget(BuiltInWidgets.kSplitButtonChooser);


    m_joy = j;
    grabber = new DoubleSolenoid(RobotMap.grab1, RobotMap.grab2);
    manipWheels = new VictorSPX(RobotMap.manipWheels);
    manipUpDown = new TalonSRX(RobotMap.manipUpDown);

    manipUpDown.configFactoryDefault();
    manipUpDown.setSensorPhase(true);
    manipUpDown.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    manipUpDown.setSelectedSensorPosition(0);
    manipUpDown.setNeutralMode(NeutralMode.Brake);

    manipWheels.configClosedloopRamp(0.2);

    setUpPID();

  }

  public void robotPeriodic() {

  }
  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }
  private void setUpPID() {
    /* 
     * Acceleration: started at 1067 and increased until felt right
     * Velocity:
     * 16000 units in 2 secs = 8000 units per second = 800 units per 100ms
     * HOWEVER, during testing, 10667 worked better
     *
     * talons interpret 1023 as full, 0 as neutral
     * kP = (percent_applied * 1023) / error
     * kP ~ motor output
     * 
     * kI should be a very small number
     * start with 0.0005
     * set the intergral zone based on the error left over when using just kP
     *
     * kD can help smooth abrupt transitions
     * start with x10 kP and increase
     * kD is not always useful
     */
    manipUpDown.configMotionAcceleration(50);    
    manipUpDown.configMotionCruiseVelocity(50);

    manipUpDown.config_kP(0, 1);
    manipUpDown.config_kI(0, 0);
    manipUpDown.config_kD(0, 0);
    manipUpDown.config_IntegralZone(0, 100);
  }


  /*
   *  PID settings for when the elevator is travelling DOWN
   *  UP/DOWN direction is based on the height array indexes
   */
  private void setDownPID() {
    // #TODO: THIS LOOP MAY NEED TO SOME TUNING
    // It is not strong enough to help up the elevator at position
    // see 'else' statement of the 'if(target_height_index != previous_index)' statement for potential fix

    // increase these if necessary 
    manipUpDown.configMotionAcceleration(30);    
    manipUpDown.configMotionCruiseVelocity(30);
    
    /* start with a very, very low kP since gravity helps a lot on the way down
     * kP = (percent_applied * 1023) / error
     * kP = (0.1 * 1023) / 15000     <-- .1 motor output when there is 15000 units of error
     * kP ~= 0.0068 ~= 0.006
     */
    manipUpDown.config_kP(0, 0.14);
    manipUpDown.config_kI(0, 0);
    manipUpDown.config_kD(0, 0);
    manipUpDown.config_IntegralZone(0, 0);
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

    // if (m_joy.getRawButton(Constants.manipPToggle) && !manipulator) {
    //   manipulator = true;
    //   if (grabber.get() == (Value.kReverse)) {
    //     grabber.set(DoubleSolenoid.Value.kForward);
    //   } else if (grabber.get() == (Value.kForward)) {
    //     grabber.set(DoubleSolenoid.Value.kReverse);
    //   }

    // }
    // if (!m_joy.getRawButton(Constants.manipPToggle)) {
    //   manipulator = false;
    // }

    // if (m_joy.getRawButton(Constants.manipMToggle) && !motor) {
    //   motor = true;
    //   motorGo = !motorGo;
      
    // }



    if (Enable_OR_Disable.getSelected()) {
    // if (m_joy.getRawButton(Constants.manipUpDownBut) && !manipUpDownLoop) {
    //   manipUpDownLoop = true;
    //   manipToggle = !manipToggle;
    // }

    // if (!m_joy.getRawButton(Constants.manipUpDownBut)) {
    //   manipUpDownLoop = false;
    // }

    // if (manipToggle) {
    //   if (isDownFirstLoop) {
    //     isDownFirstLoop = false;
    //     setDownPID();
      
    //   }
    //   if (manipUpDown.getSelectedSensorPosition() < -1260) {
    //     setUpPID();
    //   }
    //   manipUpDown.set(ControlMode.MotionMagic, -1330);
    // }
    // if (!manipToggle) {
    //   setUpPID();
    //   manipUpDown.set(ControlMode.MotionMagic, 0);
    //   isDownFirstLoop = true;
    // }

    setUpPID();
    manipUpDown.set(ControlMode.MotionMagic, 0 - Math.abs(m_joy.getRawAxis(3)) * 1400);
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
      manipWheels.set(ControlMode.PercentOutput, 0.5);    
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
