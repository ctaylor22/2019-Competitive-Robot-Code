//Conner Taylor
//Gurny/Climbing System


package frc.robot;

import java.time.Clock;
import java.time.Clock;
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
 
  SendableChooser<boolean> manual_or_hold_chooser = new SendableChooser<boolean>();
  int current_postion;

  int climbingStatus = 0;
  // ShuffleboardTab MaxSpeedTab = Shuffleboard.getTab("Max Speed");
  // NetworkTableEntry frontGurneyEntry;
  // NetworkTableEntry backGurneyEntry;
  // NetworkTableEntry driveGurneyEntry;


  public void robotInit(Joystick j) {

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

    m_navX = new NavX();
    double filtered_error = m_navX.getPitch();
    manual_or_hold_chooser.setDefaultOption("Manual", true);
    manual_or_hold_chooser.addOption("Hold Height", false);
    // gurneyTab.add("Mode", manual_or_hold_chooser).withWidget(BuiltInWidgets.kSplitButtonChooser);
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
    gBack.config_kP(0, 2.7);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 10);
    gBack.config_kF(0, 0);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(700);
    gBack.configMotionCruiseVelocity(1000);
  }

  public void autonomousInit() {
    gBack.setSelectedSensorPosition(0);
  }


  
  public void autonomousPeriodic() {
  }

  public void teleopInit() {
  }
  
  public void teleopPeriodic() {
    // reset encoder
    if (m_joy.getRawButton(Constants.gurneyEncoderReset)) {
      gBack.setSelectedSensorPosition(0);
    }

    // drive    
    dashFrontSpeed = gurneyFrontSpeedEntry.getDouble(0.5);
    dashBackSpeed = gurneyBackSpeedEntry.getDouble(0.3);
    dashDriveSpeed = gurneyDriveSpeedEntry.getDouble(0.7);
    if (m_joy.getRawAxis(Constants.gDriveBack) < 0.1) {
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
    } else if (m_joy.getRawAxis(Constants.gDriveForward) < 0.1) {
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashDriveSpeed);
    }

    /* #TODO: add low pass filtered error
    double pitch_error = m_navX.getPitch();
    filtered_error = low_pass(pitch_error, filtered_error);
    */

    // #TODO: add switch button on shuffleboard to have 'fast' / 'slow' mode
    if (m_joy.getRawButton(Constants.gurneyGoUp)) {
      /* Gurney UP on Button 4, Y
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
    else if (m_joy.getRawButton(Constants.gurneyGoDown)) {
      /* Gurney DOWN on Button 3, X
       *
       * call motion magic with a target position of 0
       *
       */
      setDownPID();
      gBack.set(ControlMode.MotionMagic, 10);
      double pitch_error = m_navX.getPitch() - 2;
      double front_kF = 0.35;
      double front_kP = 0.11;
      double output = front_kF + (front_kP)*pitch_error;
      gFront.set(ControlMode.PercentOutput, output);

      current_postion = gBack.getSelectedSensorPosition();
    }
    else if (gBack.getSelectedSensorPosition() > 4096) {
      /* hold position when encoder reads a rotation or so above 0 position
       * allows for driving of the robot from the back gurney
       * 
       * #TODO: replace holdPID with upPID. inconjuction with adding the SS error to 'current_position'
       * #TODO: allow manual control while also holding position
       *        - move JS to adjust height, then holds that height when JS reset to 0
       *        - maybe with config kF? set to kF then set back to 0
       * #TODO: add 'unlock front' option to allow front to raise 
       *        - in the ideal case, the drive wheels are on the platform and front gurney can just raise with JS axis
       * #TODO: possible hold angle after front adjustment
       */
      setHoldPID();
      gBack.set(ControlMode.MotionMagic, current_postion);

      // accelerometer PID for front
      double pitch_error = m_navX.getPitch() - 2;
      double front_kF = 0.4;
      double front_kP = 0.12;
      double output = front_kF + (front_kP)*pitch_error;
      gFront.set(ControlMode.PercentOutput, output);
      
      // gurney drive foward/backward
      driveGurneyFoward();
    } /*else if (m_joy.getRawButton(Constants.gFrontUp)) {
      gFront.set(ControlMode.PercentOutput, Constants.gDriveUp);
    } else if (m_joy.getRawButton(Constants.gBackUp)) {
      gBack.set(ControlMode.PercentOutput, Constants.gDriveUp);
    }*/
    else {
      /* manual control
       *
       * joystick Y axis * -1 to make up direction output a positive number
       */
      gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * (-1) * dashFrontSpeed);
      gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * (-1)* dashBackSpeed);
    }
    
  }

  public void report() {

    gurneyPitchEntry.setDouble(m_navX.getPitch());
    
}

  public void testInit() {

  }
  
  public void testPeriodic() {
  }
  
  public void climb() {
    //Semi-autonomous climbing
    climbUp();
    climbMode();
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
  public void climbMode() {
    int success = 0;
    if (climbingStatus <= 1) {
      success = driveWhileRaised();
      if (success == 1) {
        return;
      }
      success = tryRaiseGurney(gFront);
      if (success == 1) {
        return;
      } else if (success == 2) {
        climbingStatus = 1;
        climbMode();
      }
    }
    success = driveWhileRaised();
    if (success == 1) {
      return;
    }
    success = tryRaiseGurney(gBack);
    if (success == 1) {
      return;
    } else if (success == 2) {
      climbingStatus = 2;
      climbMode();
    }
    climbingStatus = 0;
  }

  public int driveWhileRaised() {
      /* Return codes:
       * 0: Success
       * 1: Fatal Error
       */
      boolean isDone = false;
      while (!isDone) {
        driveGurneyFoward();
        isDone = m_joy.getRawButton(Constants.gContinueRoutine);
        if (checkForAbort()) {
              return 1;
        }
    }
    return 0;
  }
  
  public int tryRaiseGurney(WPI_TalonSRX gurney) {
      /* Return codes:
       * 0: Success
       * 1: Fatal Error
       * 2: Rewind: Go back to drive-while-raised mode
       */
      int status = -1;
      boolean raiseable = false;
      while (!raiseable) {
          raiseable = canRaiseGurney(gurney);
          if (checkForAbort()) {
                return 1;
          } else if (checkForRewind()) {
              //Checks if user wants to go back to drive-while-raised mode
                return 2;
          }
      }
      status = raiseGurney(gurney);
      return status;
  }
  
  public boolean canRaiseGurney(WPI_TalonSRX gurney) {
    //Checks if the front can be raised without the robot tipping
    //if (gurney == gBack) {
    //breakPID();
    //}
    double pitchBefore = m_navX.getPitch();
    gurney.set(ControlMode.PercentOutput, Constants.gDriveUp);
    Thread.sleep(Constants.gWaitTime);
    double pitch = m_navX.getPitch();
    if (Math.abs(pitch-pitchBefore) > Constants.gPitchTolerance) {
      while (Math.abs(pitch-pitchBefore) > Constants.gPitchTolerance) {
        if (gurney == gFront) {
          if (pitchBefore-pitch > 1) {
            //If robot is tipping fowards
            setFrontUpPID();
          } else if (pitchBefore-pitch < 1) {
            //If robot is tipping backwards
            setFrontDownPID();
          }
        } else if (gurney == gBack) {
          setUpPID();
        }
        pitch = m_navX.getPitch();
      }
      if (gurney == gFront) {
        setFrontHoldPID();
      } else if (gurney == gBack) {
        setHoldPID();
      }
      return false;
    }
    return true;
  }
  
  public int raiseGurney(WPI_TalonSRX gurney) {
      /* Return codes:
       * 0: Success
       * 1: Fatal Error
       * 2: Rewind: Go back to drive-while-raised mode
       */
      boolean isDone = false;
      while (!isDone) {
          if (checkForAbort()) {
                return 1;
          } else if (checkForRewind()) {
              //Checks if user wants to go back to drive-while-raised mode
              return 2;
          }
          gFront.set(ControlMode.PercentOutput, Constants.gDriveUp);
          //TODO: Detect when the front is raised with magnetic sensors
          isDone = m_joy.getRawButton(Constants.gContinueRoutine); //sensor.detectWhenPipeIsUp()
      }
      return 0;
  }
  
  public boolean checkForAbort() {
      if (m_joy.getRawButton(Constants.gStopRoutine)) {
          safeLower();
          return true;
      } else {
          return false;
      }
  }

  public boolean checkForRewind() {
    if (m_joy.getRawButton(Constants.gRewindRoutine)) {
        return true;
    } else {
        return false;
    }
}
  
  public void safeLower() {
      //TODO: Make this detect if robot cannot safely lower
      /* Gurney DOWN in subroutine
       *
       * call motion magic with a target position of 0
       *
       */
      long start_time = System.currentTimeMillis();
      int last_position = current_postion;
      while (current_postion > Constants.gSafeHeight) {
        setDownPID();
        gBack.set(ControlMode.MotionMagic, 10);
        double pitch_error = m_navX.getPitch() - 2;
        double front_kF = 0.35;
        double front_kP = 0.11;
        double output = front_kF + (front_kP)*pitch_error;
        gFront.set(ControlMode.PercentOutput, output);
        current_postion = gBack.getSelectedSensorPosition();
        //Breaks loop if robot is lodged after a wait time
        if (System.currentTimeMillis()-start_time > Constants.gWaitTime) {
          if (Math.abs(current_postion - last_position) < Constants.gValueChange) {
            break;
          }
        }
      }
      //breakPID();
  }
  
  public void driveGurneyFoward() {
      if (m_joy.getRawAxis(Constants.gDriveBack) < 0.1) {
        gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashDriveSpeed);
      } else if (m_joy.getRawAxis(Constants.gDriveForward) < 0.1) {
        gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * -dashDriveSpeed);
      }
  }

  public void setFrontUpPID() {
    // accelerometer PID for front
    double pitch_error = m_navX.getPitch() - 2;
    double front_kF = 0.5;
    double front_kP = 0.13;
    double output = front_kF + (front_kP)*pitch_error;
    gFront.set(ControlMode.PercentOutput, output);
  }

  public void setFrontHoldPID() {
    // accelerometer PID for front
    double pitch_error = m_navX.getPitch() - 2;
    double front_kF = 0.4;
    double front_kP = 0.12;
    double output = front_kF + (front_kP)*pitch_error;
    gFront.set(ControlMode.PercentOutput, output);
  }

  public void setFrontDownPID() {
    // accelerometer PID for front
    double pitch_error = m_navX.getPitch() - 2;
    double front_kF = 0.35;
    double front_kP = 0.11;
    double output = front_kF + (front_kP)*pitch_error;
    gFront.set(ControlMode.PercentOutput, output);
  }
  
  private double low_pass(double input, double output) {
    if (output == null) { return input; }

    output = output + 0.8*(input - output);
    return output;
  }

}