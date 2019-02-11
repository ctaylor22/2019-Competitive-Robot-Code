//Conner Taylor
//Elevator System


package frc.robot;

import java.util.Map;
import java.lang.Math;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

//import sun.nio.ch.Net;

public class Elevator  {
  WPI_TalonSRX m_elevator;
  Joystick m_joy;
  int mode = 0;
  double elevSpeed;

  /*
   * height array for elevator. 
   * array values can be 0 minimum and 15500 maximum.
   * Array MUST be sorted from lowest height to tallest height
   *
   * Valid array indexes are 0 thru (length of array - 1)
   * #TODO: replace heights with competition heights (for disks and balls)
   */
  int heights[] = new int[]{100, 2000, 4000, 6000, 8000, 10000, 12000, 14000, 15500};
  double previous_index = 0;
  double target_height_index = 0;

  ShuffleboardTab maxSpeedTab = Shuffleboard.getTab("Max Speed");
  NetworkTableEntry elevatorSpeedEntry = maxSpeedTab.add("Elevator Speed", 0.5)
                                                    .withWidget(BuiltInWidgets.kNumberSlider)
                                                    .withProperties(Map.of("Min", 0, "Max", 1))
                                                    .getEntry();

  // encoder position in sensor units                                                  
  NetworkTableEntry elevatorEncoderPosition = maxSpeedTab.add("Elev Encoder Position", 0).getEntry();
  
  // VALID INDEXES ARE 0 THRU 8. this indexes the heights[] array
  NetworkTableEntry elevatorPositionEntry = maxSpeedTab.add("Elevator Target Position", 50)
                                                        .withWidget(BuiltInWidgets.kTextView)
                                                        .getEntry();

  /*
   *  PID settings for when the elevator is travelling UP
   *  UP/DOWN direction is based on the height array indexes
   */
  void setUpPID() {
    m_elevator.configMotionAcceleration(10000);    
    // 16000 units in 2 secs = 8000 units per second = 800 units per 100ms
    // HOWEVER, during testing, 10667 worked better
    m_elevator.configMotionCruiseVelocity(10667);

    m_elevator.config_kP(0, 0.38);
    m_elevator.config_kI(0, 0.0013);
    m_elevator.config_kD(0, 0);
    m_elevator.config_IntegralZone(0, 1800);
  }


  /*
   *  PID settings for when the elevator is travelling DOWN
   *  UP/DOWN direction is based on the height array indexes
   */
  void setDownPID() {
    // #TODO: THIS LOOP MAY NEED TO SOME TUNING
    // It is not strong enough to help up the elevator at position
    // see else in line 145 for potential fix
    m_elevator.configMotionAcceleration(1000);    
    m_elevator.configMotionCruiseVelocity(1000);
    
    // start with a very low kP, around 0.1
    m_elevator.config_kP(0, 0.1);
    m_elevator.config_kI(0, 0);
    m_elevator.config_kD(0, 0);
    m_elevator.config_IntegralZone(0, 0);
  }

  public void robotInit(Joystick j) {
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);
    m_elevator.configFactoryDefault();
    m_elevator.setSensorPhase(true);
    m_elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    m_elevator.setSelectedSensorPosition(0);
    m_elevator.configClosedloopRamp(0);
    m_elevator.configAllowableClosedloopError(0, 0, 0);
    // init PID with UP settings
    setUpPID();
    
    m_joy = j;
  }

  public void autonomousInit() {
    m_elevator.setSelectedSensorPosition(0);

  }
  
  public void autonomousPeriodic() {


  }

  public void teleopInit() {
  }
  
  public void teleopPeriodic() {
    // reset encoder with button 7, the small black button in the middle left 
    // 0 position should be with the elevator all the way down
    if (m_joy.getRawButton(Constants.encoderReset)) {
      m_elevator.setSelectedSensorPosition(0);
    }

    // enter closed loop position control with Y on driving joystick
    if (m_joy.getRawButton(Constants.elevatorToPositionButton)) {
      // assume elevator starting on ground with position 0
      target_height_index =  elevatorPositionEntry.getDouble(0);

      // Check if the target index has changed, and update PID values. 
      // This only runs for one loop after a change in index
      if (target_height_index != previous_index) {
        if(target_height_index > previous_index) {
          setUpPID();
        } else {
          setDownPID();
        }
        // update previous index after the comparisons
        previous_index = target_height_index;
      }
      // else, then target and previous are equal. 
      // This runs during all other loops after the change until a new index is set
      else {
        // if the closed loop error is within a threshold, re-set to the up PID values
        // uncomment and test carefully. Do not use any threshold larger than 75
        // #TODO: THIS THRESHOLD WILL NEED TO BE TUNED
        /*
        int restore_Up_PID_threshold = 10;
        if ( Math.abs(m_elevator.getClosedLoopError(0)) < restore_Up_PID_threshold) {
          // maybe put a closed loop ramp config right here
          setUpPID();
        }
        */
      }

      // get index in the heights array, valid indexes are 0 thru 8!
      m_elevator.set(ControlMode.MotionMagic, heights[(int) target_height_index]);

    } 
    // if left trigger is not on, the set elevator with right trigger
    else if (m_joy.getRawAxis(Constants.elevatorDown) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorUp) * elevSpeed);

    } 
    // if right trigger is not on, set elevator with left trigger
    else if (m_joy.getRawAxis(Constants.elevatorUp) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorDown) * -elevSpeed);
    }

  }

  public void report() {
      elevSpeed = elevatorSpeedEntry.getDouble(0);
      elevatorEncoderPosition.setDouble(m_elevator.getSelectedSensorPosition(0));
}


  
  public void testPeriodic() {
  }
}
