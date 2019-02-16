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
    gBack.config_kP(0, 0.5);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0.15);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(700);
    gBack.configMotionCruiseVelocity(1000);
  }
  
  private void setDownPID() {
    gBack.config_kP(0, 0);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0.1);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(700);
    gBack.configMotionCruiseVelocity(1200);
  }

  // DO NOT TOUCH. ASK JOSH
  private void setHoldPID() {
    gBack.config_kP(0, 0.8);
    gBack.config_kI(0, 0);
    gBack.config_kD(0, 0);
    gBack.config_kF(0, 0.7);
    gBack.config_IntegralZone(0, 500);
    gBack.configMotionAcceleration(700);
    gBack.configMotionCruiseVelocity(1000);
  }
  
  private double deadband(double input) {
    /*
     * Takes an input and returns 0 if it is within the deadband
     */
    if (Math.abs(input) < 0.05) return 0;
    else return input;
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
    double dashSpeed = driveGurneyEntry.getDouble(0.6);
    if (m_joy.getRawAxis(Constants.gDriveBack) < 0.1) {
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveForward) * dashSpeed);
    } else if (m_joy.getRawAxis(Constants.gDriveForward) < 0.1) {
      gDrive.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gDriveBack) * (-1) * dashSpeed);
    }

    // add low pass filtered error
    double pitch_error = m_navX.getPitch()-2;
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
      gBack.set(ControlMode.Velocity, 800);

      // accelerometer PID for front
      double front_kF = 0.7;
      double front_kP = 0.15;
      double output = front_kF + (front_kP)*filtered_error;
      gFront.set(ControlMode.PercentOutput, output);

      /* #TODO: test adding an offset to help the hold PID
       * this would be the steady state error of the UP
       */
      int experimental_steady_state_error = 0;
      hold_position = gBack.getSelectedSensorPosition() + experimental_steady_state_error;
    }
    else if (m_joy.getRawButton(Constants.gurneyGoDown)) {
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
    else if (front_locked_chooser.getSelected() && gBack.getSelectedSensorPosition() > 2048) {
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
      setHoldPID();
      double yaxis = (-1) *  m_joy.getRawAxis(Constants.gUpBack);

      // accelerometer PID for front
      double front_kF = 0.4;
      double front_kP = 0.13;
      double output = front_kF + (front_kP)*filtered_error;

      // adjust height with joystick
      if (deadband(yaxis) != 0) {
        if ((deadband(yaxis) < 0 && gBack.getSelectedSensorPosition() < 100)) {
          gBack.set(ControlMode.Velocity, 0);
        }
        else {
          gBack.set(ControlMode.Velocity, 100+100*yaxis);
        }
      }
      // holding height velocity with a kF of 0.75
      else {
        gBack.set(ControlMode.Velocity, 100);
      }

      // front output math. includes raising front for platform climb
      double front_yaxis = -1 * deadband(m_joy.getRawAxis(Constants.gUpFront));
      if (isSafeToRaiseFront.getSelected()) {
        gFront.set(ControlMode.PercentOutput, 0.5*front_yaxis);
      }
      else {
        gFront.set(ControlMode.PercentOutput, output + 0.5*front_yaxis);
      }

    }
    else {
      /* manual control
       *
       * joystick Y axis * -1 to make up direction output a positive number
       */
      gFront.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpFront) * (-1) * frontGurneyEntry.getDouble(0.6));
      gBack.set(ControlMode.PercentOutput, m_joy.getRawAxis(Constants.gUpBack) * (-1)* backGurneyEntry.getDouble(0.4));
    }
    
  }

  public void report() {

    gurneyPitchEntry.setDouble(m_navX.getPitch());
    
  }

  public void testInit() {

  }
  
  public void testPeriodic() {
  }

  private double low_pass(double input, double output) {
    output = output + 0.9 * (input - output);
    return output;
  }

}
