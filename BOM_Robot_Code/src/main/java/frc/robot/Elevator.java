//Conner Taylor
//Elevator System


package frc.robot;

import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


//import sun.nio.ch.Net;

public class Elevator  {
  WPI_TalonSRX m_elevator;
  VictorSPX m_elevatorFollow;
  Joystick m_joy;
  int mode = 0;
  boolean posTog = false;
  String elevModeString = "Error";
  boolean elevMode = false;
  

  // private Robot m_Robot = new Robot();


  /*
   * height array for elevator. 
   * array values can be 0 minimum and 17500 maximum.
   * Array MUST be sorted from lowest height to tallest height
   *
   * Valid array indexes are 0 thru (length of array - 1)
   * #TODO: replace heights with competition heights (for disks and balls)
   */
  // used for the elevator index
  // ground, low hatch, hatch grab, mid ball, cargo ball, mid hatch, top ball, top hatch
  // 0, 1, 2, 3, 4, 5, 6, 7                        Hatch Mode = 0, 2, 4, 8       Ball Mode = 0, 3, 5, 8
  
  //Comp Heights
  // int heights[] = new int[]{10, 3500, 3800, 4900, 8700, 12400, 14500, 16300};
  
  //Practice Heights
  int heights[] = new int[]{10, 2600, 2900, 4900, 8700, 12400, 15500, 16200};
  Integer previous_index = 0;
  public static Integer target_height_index = 0;

  ShuffleboardTab testMode = Shuffleboard.getTab("Test Mode");

  NetworkTableEntry resetEncoderEntry = testMode.add("Reset Elevator Encoder", false)
                                                .withWidget(BuiltInWidgets.kToggleButton)
                                                .getEntry();

  ShuffleboardTab maxSpeedTab = Shuffleboard.getTab("Beginning Game");

  // encoder position in sensor units                                                  
  NetworkTableEntry elevatorEncoderPosition_Entry = maxSpeedTab.add("Elev Encoder Position", 0)
                                                               .withSize(2, 1)
                                                               .withPosition(3, 1)
                                                               .getEntry();
  NetworkTableEntry targetHeightIndexEntry = maxSpeedTab.add("Target Position Index", 0)
                                                        .withPosition(2, 2)
                                                        .getEntry();

  NetworkTableEntry elevatorCurrentEntry = testMode.add("elevator Current", 0).withPosition(0, 5).getEntry();
  NetworkTableEntry elevatorVoltageEntry = testMode.add("elevator Voltage", 0).withPosition(1, 5).getEntry();

  // see robot init for added options.
  // use elevator_position_chooser.getSelected() to get selected value
  SendableChooser<Integer> elevator_position_chooser = new SendableChooser<Integer>();
  
  /*
   *  PID settings for when the elevator is travelling UP
   *  UP/DOWN direction is based on the height array indexes
   */
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
    m_elevator.configMotionAcceleration(9000);   
    m_elevator.configMotionCruiseVelocity(11000);

    m_elevator.config_kP(0, 2.5); //0.028 Dual 775 Pro
    m_elevator.config_kI(0, 0);
    m_elevator.config_kD(0, 180);
    m_elevator.config_IntegralZone(0, 600);
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
    m_elevator.configMotionAcceleration(9000);    
    m_elevator.configMotionCruiseVelocity(10000);
    
    /* start with a very, very low kP since gravity helps a lot on the way down
     * kP = (percent_applied * 1023) / error
     * kP = (0.1 * 1023) / 15000     <-- .1 motor output when there is 15000 units of error
     * kP ~= 0.0068 ~= 0.006
     */
    m_elevator.config_kP(0, 0.15);
    m_elevator.config_kI(0, 0);
    m_elevator.config_kD(0, 20);
    m_elevator.config_IntegralZone(0, 0);
  }

  public void robotInit(Joystick j) {
    m_elevatorFollow = new VictorSPX(RobotMap.elevFollow);
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);

    m_elevatorFollow.follow(m_elevator);
    // m_elevatorFollow.configFactoryDefault();
    // m_elevatorFollow.follow(m_elevator);
    m_elevator.setSafetyEnabled(true);
    m_elevator.configFactoryDefault();
    m_elevator.setSensorPhase(true);
    m_elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    m_elevator.setSelectedSensorPosition(0);
    m_elevator.configClosedloopRamp(0.25);
    m_elevator.configAllowableClosedloopError(0, 0, 0);

    m_elevator.configForwardSoftLimitEnable(true);
    m_elevator.configReverseSoftLimitEnable(true);
    m_elevator.configForwardSoftLimitThreshold(16400);
    m_elevator.configReverseSoftLimitThreshold(50);
    

    m_elevator.configNominalOutputForward(0);
    m_elevator.configNominalOutputReverse(0);
    // m_elevator.configContinuousCurrentLimit(10);
    // m_elevator.configPeakCurrentLimit(14);
    m_elevator.configPeakCurrentDuration(2000);
    m_elevator.enableCurrentLimit(false);
    // m_elevator.configPeakOutputForward(0.4);
    // m_elevator.configPeakOutputReverse(-0.05);


    // add position options to chooser
    // VALID INDEXES ARE 0 THRU 8 since this indexes the heights[] array
    elevator_position_chooser.setDefaultOption("Height 0", 0);
    elevator_position_chooser.addOption("Height 1", 1);
    elevator_position_chooser.addOption("Height 2", 2);
    elevator_position_chooser.addOption("Height 3", 3);
    elevator_position_chooser.addOption("Height 4", 4);
    elevator_position_chooser.addOption("Height 5", 5);
    elevator_position_chooser.addOption("Height 6", 6);
    elevator_position_chooser.addOption("Height 7", 7);
    elevator_position_chooser.addOption("Height 8", 8);

    maxSpeedTab.add("DO NOT TOUCH... Elevator Position - Chooser", elevator_position_chooser)
               .withWidget(BuiltInWidgets.kComboBoxChooser)
               .withPosition(3, 2)
               .withSize(2, 1);
  
    // init PID with UP settings
    setUpPID();
    
    m_joy = j;
  }

  public void autonomousInit() {
    target_height_index = 0;
    teleopInit();

  }
  
  public void autonomousPeriodic() {
    teleopPeriodic();

  }

  public void teleopInit() {
    
    m_elevator.set(ControlMode.PercentOutput, 0);
  }
  
  public void teleopPeriodic() {
    if (m_joy.getRawButtonReleased(Constants.elevMode)) {
      elevMode = !elevMode;
    } 
    if (m_joy.getRawButtonReleased(Constants.elevatorBot)) {
      Manipulator.elevatorDown = false;
      Manipulator.hatchIsDown = false;
      target_height_index = 0;
    } else if (m_joy.getRawButtonReleased(Constants.elevatorMid)) {
      Manipulator.elevatorDown = false;
      Manipulator.hatchIsDown = false;
      target_height_index = 3;
    } else if (m_joy.getRawButtonReleased(Constants.elevatorCargo)) {
      Manipulator.elevatorDown = false;
      Manipulator.hatchIsDown = false;
      target_height_index = 4;
    } else if (m_joy.getRawButtonReleased(Constants.elevatorTop)) {
      Manipulator.elevatorDown = false;
      Manipulator.hatchIsDown = false;
      target_height_index = 7;
    }


    // if (target_height_index != 0) {
    //   m_Robot.ledCom(3);
    // } else {
    //   m_Robot.ledCom(0);
    // }

    // reset encoder with button 7, the small black button in the middle left 
    // 0 position should be with the elevator all the way down
    if (m_joy.getRawButtonPressed(6)) {
      Manipulator.elevatorDown = false;
      target_height_index++;
    }
    else if (m_joy.getRawButtonReleased(5)) {
      Manipulator.elevatorDown = false;
      target_height_index--;
    }
    if (target_height_index < 0) {
      target_height_index = 0;
    }
    if (target_height_index > 7) {

      target_height_index = 7;
    }

    int position = getPositionAndSetPID();
    // if (m_joy.getRawAxis(Constants.elevatorUp) < 0.02 && m_joy.getRawAxis(Constants.elevatorDown) < 0.02) {
      // get index in the heights array, valid indexes are 0 thru 8!
      if (Manipulator.elevatorDown) {
        setDownPID();
        m_elevator.set(ControlMode.Position, position - 200);
      } else if (Manipulator.hatchIsDown && target_height_index == 0){
        setUpPID();
        m_elevator.set(ControlMode.Position, position + Manipulator.elevatorUpBecauseOfHatch);
      } else {
        m_elevator.set(ControlMode.Position, position);
      }
      
      
  
      // m_elevator.set(ControlMode.PercentOutput, 0);
    // }
    // if left trigger is not on, the set elevator with right trigger
    // else if (m_joy.getRawAxis(Constants.elevatorDown) < 0.02) {
    //   m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorUp) * elevatorSpeed_Entry.getDouble(0.5));
    // } 
    // // // if right trigger is not on, set elevator with left trigger
    // else if (m_joy.getRawAxis(Constants.elevatorUp) < 0.02) {
    //   m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorDown) * -1 *elevatorSpeed_Entry.getDouble(0.5));
    // }

  }

  public void report() {
    elevatorEncoderPosition_Entry.setDouble(m_elevator.getSelectedSensorPosition(0));
    targetHeightIndexEntry.setDouble(target_height_index);
    elevatorVoltageEntry.setDouble(m_elevator.getMotorOutputVoltage());
    elevatorCurrentEntry.setDouble(m_elevator.getOutputCurrent());
    // SmartDashboard.putNumber("Elevator Encoder", m_elevator.getSelectedSensorPosition());
  }


  
  public void testPeriodic() {
        // if left trigger is not on, the set elevator with right trigger
    if (m_joy.getRawAxis(Constants.elevatorDown) < 0.02) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorUp) * 0.5);
    } 
    // // if right trigger is not on, set elevator with left trigger
    else if (m_joy.getRawAxis(Constants.elevatorUp) < 0.02) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.elevatorDown) * -1 * 0.3);
    }

    if (m_joy.getRawButtonReleased(1)) {
      m_elevator.setSelectedSensorPosition(0);
    }

  }

  private int getPositionAndSetPID() {
    // assume elevator starting on ground with position 0
    // target_height_index = elevator_position_chooser.getSelected().intValue();
    int encoder_position = m_elevator.getSelectedSensorPosition(0);

  
    int target_position = heights[target_height_index];


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
      // if the closed loop error is within a threshold, re-set to the 'up' PID values
      // uncomment and test carefully. Do not use any threshold larger than 50
      // #TODO: THIS THRESHOLD WILL NEED TO BE TUNED

      /* UNCOMMENT THIS */
      int restore_Up_PID_threshold = 30;
      if ( ( encoder_position - target_position ) < restore_Up_PID_threshold) {
        // maybe put a closed loop ramp config right here
        setUpPID();
      }
    }
    return target_position;
  }
}

