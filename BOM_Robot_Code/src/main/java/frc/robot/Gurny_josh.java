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
  
  NavX m_navX = new NavX();
  ShuffleboardTab gurneyTab = Shuffleboard.getTab("End Game");
  NetworkTableEntry gurneyPitchEntry = gurneyTab.add("Gurney Pitch", 0)
                                      .withSize(1, 1)
                                      .withPosition(2, 0)
                                      .withWidget(BuiltInWidgets.kDial)
                                      .getEntry();

 
  SendableChooser<Boolean> manual_or_hold_chooser = new SendableChooser<Boolean>();

  // Climbing routine functions
  int current_postion;
  boolean climbing = false;
  int climbingstage = 0;


  // ShuffleboardTab MaxSpeedTab = Shuffleboard.getTab("Max Speed");
  // NetworkTableEntry frontGurneyEntry;
  // NetworkTableEntry backGurneyEntry;
  // NetworkTableEntry driveGurneyEntry;
  SendableChooser<Boolean> front_locked_chooser = new SendableChooser<Boolean>();
  SendableChooser<Boolean> isSafeToRaiseFront = new SendableChooser<Boolean>();
  
  int hold_position;
  double filtered_error = m_navX.getPitch();

  NetworkTableEntry frontGurneyEntry = gurneyTab.add("Front Speed", 0.5)
                                        .withWidget(BuiltInWidgets.kNumberSlider)
                                        .withPosition(0, 2)
                                        .withSize(2, 1)
                                        .getEntry();
  NetworkTableEntry backGurneyEntry = gurneyTab.add("Back Speed", 0.5)
                                        .withWidget(BuiltInWidgets.kNumberSlider)
                                        .withPosition(2, 2)
                                        .withSize(2, 1)
                                        .getEntry();
  NetworkTableEntry driveGurneyEntry = gurneyTab.add("Drive Speed", 0.5)
                                        .withWidget(BuiltInWidgets.kNumberSlider)
                                        .withPosition(4, 2)
                                        .withSize(2, 1)
                                        .getEntry();

  NetworkTableEntry gurneyEncoderEntry = gurneyTab.add("Gurney Encoder", 0)
  .withWidget(BuiltInWidgets.kTextView)
  .withPosition(3, 0)
  .withSize(2, 1)
  .getEntry();


  public void robotInit(Joystick j) {

    m_joy = j;

    gBack = new WPI_TalonSRX(RobotMap.gBack);

    gFront = new WPI_TalonSRX(RobotMap.gFront);

    gDrive = new WPI_VictorSPX(RobotMap.gDrive);

    gDrive.configFactoryDefault();
    gBack.configFactoryDefault();
    gFront.configFactoryDefault();

    gBack.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    gBack.setSensorPhase(true);
    gBack.setSelectedSensorPosition(0);
    hold_position = gBack.getSelectedSensorPosition();
    gBack.configNominalOutputForward(0);
    gBack.configNominalOutputReverse(0);
    gBack.configPeakOutputForward(1);
    gBack.configPeakOutputReverse(-1);
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

    front_locked_chooser.setDefaultOption("Lock", true);
    front_locked_chooser.addOption("Unlock", false);
    gurneyTab.add("Front Gurney Level", front_locked_chooser)
             .withSize(2, 1)
             .withPosition(0, 0)
             .withWidget(BuiltInWidgets.kSplitButtonChooser);

    isSafeToRaiseFront.setDefaultOption("Not Safe", false);
    isSafeToRaiseFront.addOption("Safe over Platfrom", true);
    gurneyTab.add("Safe to Raise Front", isSafeToRaiseFront)
            .withSize(2, 1)
            .withPosition(0, 1)
            .withWidget(BuiltInWidgets.kSplitButtonChooser);
  }

  private void setUpPID() {
    gBack.config_kP(0, 0.02);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0.003);
    gBack.config_IntegralZone(0, 10);
    gBack.configMotionAcceleration(10000);
    gBack.configMotionCruiseVelocity(1000);
  }
  
  private void setDownPID() {
    gBack.config_kP(0, 0.01);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0);
    gBack.config_IntegralZone(0, 10);
    gBack.configMotionAcceleration(15000);
    gBack.configMotionCruiseVelocity(9000);
  }

  // DO NOT TOUCH. ASK JOSH
  private void setHoldPID() {
    gBack.config_kP(0, 0.03);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0.003);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(10000);
    gBack.configMotionCruiseVelocity(300);
  }
  
  private void breakPID() {
    //Breaks the PID loop and stops the back motor
    //May be dangerous. Needs to be looked over by Josh
    gBack.config_kP(0, 0);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0);
    gBack.config_IntegralZone(0, 0);
    gBack.configMotionAcceleration(0);
    gBack.configMotionCruiseVelocity(0);
    gBack.set(ControlMode.PercentOutput, 0);
  }

  private double deadband(double input) {
    /*
     * Takes an input and returns 0 if it is within the deadband
     */
    if (Math.abs(input) < 0.1) return 0;
    else return input;
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
    /*if (climbing) {
      if (m_joy.getRawButton(Constants.gStopRoutine)) {
        climbing = false;
        climbingstage = 0;
      } if (m_joy.getRawButton(Constants.gRevertStage)) {
        if (climbingstage == 2) {
          climbingstage = 1;
        } else if (climbingstage == 5) {
          climbingstage = 3;
        }
      } else if (climbingstage == 0) {
        //Robot hasn't climbed
        climbUp();
        if (Math.abs(gBack.getSelectedSensorPosition()-36000) <= 50) {
          climbingstage += 1;
        }
      } else if (climbingstage == 1) {
        //Robot has climbed needs to be driven onto platform
        driveWhileRaised();
        if (m_joy.getRawButton(Constants.gContinueRoutine)) {
          climbingstage += 1;
          breakPID();
        }
      } if (climbingstage == 2) {
        //Robot is raising front gurney
        driveFrontGurney();
        if (!m_joy.getRawButton(Constants.gContinueRoutine)) {
          gFront.set(ControlMode.PercentOutput, 0);
          climbingstage += 1;
        }
      } if (climbingstage == 3) {
        //Robot needs to be driven foward
        driveWhileRaised();
        if (m_joy.getRawButton(Constants.gContinueRoutine)) {
          climbingstage += 1;
        }
      } if (climbingstage == 4) {
        //Robot is raising back gurney
        gBack.set(ControlMode.PercentOutput, Constants.gDriveUp);
        if (gBack.getSelectedSensorPosition() <= Constants.gBackUp) {
          gBack.set(ControlMode.PercentOutput, 0);
          climbingstage = 0;
          climbing = false;
        }
      }
    } else if (m_joy.getRawButton(Constants.gContinueRoutine)) {*/
    // if (m_joy.getRawButton(Constants.gContinueRoutine)) {
    //   climbing = true;
    //   climbingstage = 0;
    if (m_joy.getRawButton(Constants.gurneyEncoderReset)) {
      gBack.setSelectedSensorPosition(0);
    }

    // drive
    double dashSpeed = driveGurneyEntry.getDouble(0.6);
    gDrive.set(ControlMode.PercentOutput, deadband(m_joy.getRawAxis(1)));
    
    // add low pass filtered error
    // offset is necessary for robot to tilt forward and not tip over
    double pitch_error = m_navX.getPitch()-3;
    filtered_error = low_pass(pitch_error, filtered_error);
    

    // #TODO: add switch button on shuffleboard to have 'fast' / 'slow' mode
    if (m_joy.getRawButton(Constants.gurneyGoUp)) {
      Shuffleboard.selectTab("End Game");
      /* Gurney UP on Button 4, Y
       * 
       * call motion magic with a set point of ~36000
       *
       * Robot tilts forward ==> positive pitch ==> add to front output
       * #TODO: set max height on encoder for top of gurney
       */
      setUpPID();
      gBack.set(ControlMode.MotionMagic, 80000);

      // accelerometer PID for front
      double front_kF = 0.35;
      double front_kP = 0.1;
      double output = front_kF + (front_kP)*filtered_error;
      gFront.set(ControlMode.PercentOutput, output);

      /* #TODO: test adding an offset to help the hold PID
       * this would be the steady state error of the UP
       */
      int experimental_steady_state_error = 400;
      hold_position = gBack.getSelectedSensorPosition() + experimental_steady_state_error;
    }
    else if (m_joy.getRawButton(Constants.gurneyGoDown)) {
      /* Gurney DOWN on Button 3, X
       *
       * call motion magic with a target position of 0
       *
       */
      setDownPID();
      gBack.set(ControlMode.MotionMagic, 0);
      double front_kF = 0.155;
      double front_kP = 0.1;
      double output = front_kF + (front_kP)*filtered_error;
      gFront.set(ControlMode.PercentOutput, output);

      hold_position = gBack.getSelectedSensorPosition();
    }
    else if (front_locked_chooser.getSelected() && gBack.getSelectedSensorPosition() > 2648) {
      /* hold position when encoder reads a rotation or so above 0 position
       * 
       * #TODO: replace holdPID with upPID. inconjuction with adding the SS error to 'current_position'
       * #TODO: allow manual control while also holding position
       *        - move JS to adjust height, then holds that height when JS reset to 0
       *        - maybe with config kF? set to kF then set back to 0
       * #TODO: add 'unlock front' option to allow front to raise 
       *        - in the ideal case, the drive wheels are on the platform and front gurney can just raise with JS axis
       * #TODO: possible hold angle after front adjustment
       */

      // accelerometer PID for front
      double front_kF = 0.2;
      double front_kP = 0.12;
      double output = front_kF + (front_kP)*filtered_error;
      setHoldPID();
      gBack.set(ControlMode.Position, hold_position);
      
      // front output math. includes raising front for platform climb
      double front_yaxis = 0;
      if (m_joy.getRawButton(Constants.raiseBackGurney)) {
        front_yaxis = -0.3;
      } else if (m_joy.getRawButton(Constants.lowerBackGurney)) {
        front_yaxis = 0.3;
      }

      if (isSafeToRaiseFront.getSelected()) {
        gFront.set(ControlMode.PercentOutput, 0.5*front_yaxis);
      }
      else {
        gFront.set(ControlMode.PercentOutput, output);
      }
     
    }
    else {
      /* manual control
       *
       * joystick Y axis * -1 to make up direction output a positive number
       */
      double front_yaxis = 0;
      if (m_joy.getRawButton(3)) {
        front_yaxis = -0.2;
      } else if (m_joy.getRawButton(5)) {
        front_yaxis = 0.2;
      }
      gFront.set(ControlMode.PercentOutput, 0);
      gBack.set(ControlMode.PercentOutput, front_yaxis);
    }
  }

  public void driveWhileRaised() {
    /* Return codes:
     * 0: Success
     * 1: Fatal Error
     */
    double axis = m_joy.getRawAxis(Constants.gDrive);
    if (Math.abs(axis) >= .1) {
      gDrive.set(ControlMode.PercentOutput, axis);
    } else {
      gDrive.set(ControlMode.PercentOutput, 0);
    }
  }

  public void driveFrontGurney() {
    if (m_joy.getRawButton(Constants.gFrontUp)) {
      gFront.set(ControlMode.PercentOutput, Constants.gDriveUp);
    } else {
      gFront.set(ControlMode.PercentOutput, 0);
    }
  }

  public void climbUp() {
    /* Gurney UP climing routine
       * 
       * call motion magic with a set point of ~36000
       *
       * Robot tilts forward ==> positive pitch ==> add to front output
       * #TODO: set max height on encoder for top of gurney
       */
      setUpPID();
      gBack.set(ControlMode.MotionMagic, 36000);

      // accelerometer PID for front
      double pitch_error = m_navX.getPitch() - 2;
      double front_kF = 0.5;
      double front_kP = 0.13;
      double output = front_kF + (front_kP)*pitch_error;
      gFront.set(ControlMode.PercentOutput, output);

      /* #TODO: test adding an offset to help the hold PID
       * this would be the steady state error of the UP
       */
      int experimental_steady_state_error = 0;
      current_postion = gBack.getSelectedSensorPosition() + experimental_steady_state_error;
  }

  public void letDown() {
    //Lets robot down without crashing it
    /* Gurney DOWN on Button 3, X
       *
       * call motion magic with a target position of 0
       *
       */
      setDownPID();
      gBack.set(ControlMode.Velocity, -10);
      double front_kF = 0.25;
      double front_kP = 0.14;
      double output = front_kF + (front_kP)*filtered_error;
      gFront.set(ControlMode.PercentOutput, output);

      hold_position = gBack.getSelectedSensorPosition();
  }

  public void report() {
    gurneyPitchEntry.setDouble(m_navX.getPitch());
    gurneyEncoderEntry.setDouble(gBack.getSelectedSensorPosition());
  }

  public void testInit() {

  }
  
  public void testPeriodic() {
  }

  private double low_pass(double input, double output) {
    output = output + 0.85 * (input - output);
    return output;
  }

}
