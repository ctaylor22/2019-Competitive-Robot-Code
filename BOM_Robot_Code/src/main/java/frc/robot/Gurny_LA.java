// Gurney -- New Version for LA
// 3/13/19 -- DLB, Version 1


package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.IMUProtocol.YPRUpdate;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;
import frc.robot.NavX;

// Possible Modes are:
// OFF    == Gurney OFF, No gurney motor can be active
// ABORT  == Aborting... Trying or have landed bot back to floor
// RISING == Both Front and Back trying to raise the robot.
//           Can have pauses or "holds" in this mode 
// FRONT_RETRACT == Front legs are retracting.
// BACK_RETRACT  == Back legs can be retracting.
// SETUP         == Allows setting of parameters with joystick
// CALIBRATE     == Allows running of the main gurney motors, slowly.


public class Gurny_LA  {

  public static final int GN_OFF           = 0;
  public static final int GN_ABORT         = 1;
  public static final int GN_RISING        = 2;
  public static final int GN_FRONT_RETRACT = 3;
  public static final int GN_BACK_RETRACT  = 4;
  public static final int GN_CALIBRATE     = 5;
  public static final int GN_SETUP         = 6;

  public static final int BUTTON_GO         = 1;
  public static final int BUTTON_ABORT      = 2;
  public static final int BUTTON_LEFT_UP    = 3;
  public static final int BUTTON_LEFT_DOWN  = 5;
  public static final int BUTTON_RIGHT_UP   = 4;
  public static final int BUTTON_RIGHT_DOWN = 6;
  public static final int BUTTON_CALIBRATE  = 8;
  public static final int BUTTON_SETUP      = 7;
  public static final int BUTTON_SAVE       = 10;
  public static final int BUTTON_SAFE_FRONT = 11;
  public static final int BUTTON_SAFE_BACK  = 12;

  private static final double cBiasInc = 0.01;

  Joystick m_joy;
  DriveTrain m_DriveTrain;   
  WPI_TalonSRX gBack;
  WPI_TalonSRX gFront;
  WPI_VictorSPX gDrive;
  int m_Mode = GN_OFF;
  int m_Hold_Front = 0;
  int m_Hold_Back = 0;
  int m_Counter = 0;

  // Default Parameter Settings...
  static final double default_KUp = -0.95;          // Overall Percentage to rise.
  static final double default_KHold = -0.15;        // Overall Percentage to hold.
  static final double default_KDown = -0.085;       // Overall Percentage to lower.
  static final double default_FrontRiseBias = -0.02; // Bias in Percentage.
  static final double default_BackRiseBias = -0.01;  // Bias in Percentage.
  static final double default_FrontHoldBias = -0.02; // Bias in Percentage.
  static final double default_BackHoldBias = 0.11;   // Bias in Percentage.

  // ********************** Important Parameters Below -- See LoadDefaut()
  double m_KUp;           // Overall Percentage to go UP.
  double m_KHold;         // Overall Percentage to HOLD.
  double m_KDown;         // Overall Percentage to go DOWN.
  double m_FrontRiseBias; // Bias in Percentage.
  double m_BackRiseBias;  // Bias in Percentage.
  double m_FrontHoldBias; // Bias in Percentage.
  double m_BackHoldBias;  // Bias in Percentage.
  // *************************************************

  double m_FrontOut = 0.0;  // Actual Percent Output to Front Gurney Motor
  double m_BackOut = 0.0; // Actual Percent Output to Back Gurney Motor
  boolean m_AbortButtonClear = true;   // For Abort Double Click.
  
  ShuffleboardTab gurneyTab = Shuffleboard.getTab("Gurny-LA");
  NetworkTableEntry stab_Mode = gurneyTab.add("Mode", "??")
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(0, 0)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_Input = gurneyTab.add("Joy-Input", 0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(0, 1)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_FrontEnc = gurneyTab.add("Front Enc", 0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(3, 1)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_BackEnc = gurneyTab.add("Back Enc", 0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(3, 2)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_Counter = gurneyTab.add("Counter", 0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(6, 1)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_KUp = gurneyTab.add("KUp", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(5, 0)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_FrontRiseBias = gurneyTab.add("FrontRiseBias", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(5, 1)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_BackRiseBias = gurneyTab.add("BackRiseBias", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(5, 2)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_KHold = gurneyTab.add("KHold", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(6, 0)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_FrontHoldBias = gurneyTab.add("FrontHoldBias", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(6, 1)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_BackHoldBias = gurneyTab.add("BackHoldBias", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(6, 2)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_FrontOut = gurneyTab.add("Front Output", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(8, 0)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_BackOut = gurneyTab.add("Back Outout", 0.0)
    .withWidget(BuiltInWidgets.kTextView)
    .withPosition(9, 0)
    .withSize(1, 1)
    .getEntry();
  NetworkTableEntry stab_UseingDefaults = gurneyTab.add("Using Defaults", false)
    .withWidget(BuiltInWidgets.kToggleButton)
    .withPosition(1, 0)
    .withSize(1, 1)
    .getEntry();

  private void Init(Joystick j, DriveTrain d) {
    m_joy = j;
    m_DriveTrain = d;
    LoadDefaults();
    GetSettings();

    gBack = new WPI_TalonSRX(RobotMap.gBack);
    gFront = new WPI_TalonSRX(RobotMap.gFront);
    gDrive = new WPI_VictorSPX(RobotMap.gDrive);

    gDrive.configFactoryDefault();
    gFront.configFactoryDefault();
    gBack.configFactoryDefault();

    gFront.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    gFront.setSensorPhase(true);
    gBack.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    gBack.setSensorPhase(true);

    m_Hold_Front = gFront.getSelectedSensorPosition();
    m_Hold_Back  = gBack.getSelectedSensorPosition();

    gFront.configNominalOutputForward(0);
    gFront.configNominalOutputReverse(0);
    gFront.configPeakOutputForward(1);
    gFront.configPeakOutputReverse(-1);
    
    gBack.configNominalOutputForward(0);
    gBack.configNominalOutputReverse(0);
    gBack.configPeakOutputForward(1);
    gBack.configPeakOutputReverse(-1);

  }

  private String GetStringMode() {
    switch(m_Mode) {
      case GN_OFF: return "OFF";
      case GN_ABORT: return "ABORT";
      case GN_RISING: return "RISING";
      case GN_FRONT_RETRACT: return "FRONT RETRACT";
      case GN_BACK_RETRACT: return "BACK RETRACT";
      case GN_CALIBRATE: return "CALIBRATE";
      case GN_SETUP: return "SETUP";
    }
    return "??";
  }

  private void LoadDefaults() {
    m_KUp            = default_KUp;  // Percentage to go UP.
    m_KHold          = default_KHold; // Percentage to HOLD.
    m_KDown          = default_KDown; // Percentage to go DOWN.
    m_FrontRiseBias  = default_FrontRiseBias; // Bias in Percentage.
    m_BackRiseBias   = default_BackRiseBias;  // Bias in Percentage.
    m_FrontHoldBias  = default_FrontHoldBias; // Bias in Percentage.
    m_BackHoldBias   = default_BackHoldBias;  // Bias in Percentage.
  }

  private boolean UsingDefaults() {
    if (m_KUp != default_KUp) {
      return false;
    }
    if (m_KHold != default_KHold) {
      return false;
    }
    if (m_KDown != default_KDown) {
      return false;
    }
    if (m_FrontRiseBias != default_FrontRiseBias) {
      return false;
    }
    if (m_BackRiseBias != default_BackRiseBias) {
      return false;
    }
    if (m_FrontHoldBias != default_FrontHoldBias) {
      return false;
    }
    if (m_BackHoldBias != default_BackHoldBias) {
      return false;
    }
    return true;
  }

  private void GetSettings() {
    Preferences p = Preferences.getInstance();
    double x;
    x = p.getDouble("Gurny-la-KUp", default_KUp);
    if (x > -1.0 && x < -0.1) {
      m_KUp = x;
    }
    x = p.getDouble("Gurny-la-KHold", default_KHold);
    if (x > -0.5 && x < -0.05) {
      m_KHold = x;
    }
    x = p.getDouble("Gurny-la-KDown", default_KDown);
    if (x > -0.5 && x < 0.00) {
      m_KDown = x;
    }
    x = p.getDouble("Gurny-la-FrontRiseBias", default_FrontRiseBias);
    if (x > -0.5 && x < 0.5) {
      m_FrontRiseBias = x;
    }
    x = p.getDouble("Gurny-la-BackRiseBias", default_BackRiseBias);
    if (x > -0.5 && x < 0.5) {
      m_BackRiseBias = x;
    }
    x = p.getDouble("Gurny-la-FrontHoldBias", default_FrontHoldBias);
    if (x > -0.5 && x < 0.5) {
      m_FrontHoldBias = x;
    }
    x = p.getDouble("Gurny-la-BackHoldBias", default_BackHoldBias);
    if (x > -0.5 && x < 0.5) {
      m_BackHoldBias = x;
    }
  }

  private void SaveSettings() {
    Preferences p = Preferences.getInstance();
    p.putDouble("Gurny-la-KUp", m_KUp);
    p.putDouble("Gurny-la-KHold", m_KHold);
    p.putDouble("Gurny-la-KDown", m_KDown);
    p.putDouble("Gurny-la-FrontRiseBias", m_FrontRiseBias);
    p.putDouble("Gurny-la-BackRiseBias", m_BackRiseBias);
    p.putDouble("Gurny-la-FrontHoldBias", m_FrontHoldBias);
    p.putDouble("Gurny-la-BackHoldBias", m_BackHoldBias);
  }

  private void ResetGurny() {
    gFront.setSelectedSensorPosition(0);
    gBack.setSelectedSensorPosition(0);
    m_Mode = GN_OFF;
  }

  private void SetMotors(double f, double b) {
    m_FrontOut = f;
    m_BackOut = b;
    gFront.set(ControlMode.PercentOutput, f);
    gBack.set(ControlMode.PercentOutput, b);
  }

  private void KillMotors() {
    SetMotors(0.0, 0.0);
    gDrive.set(ControlMode.PercentOutput, 0.0);
  }
  
  private void DriveBot() {
    if (m_Mode == GN_RISING || m_Mode == GN_CALIBRATE ||
      m_Mode == GN_FRONT_RETRACT || m_Mode == GN_BACK_RETRACT) {
        double z = m_joy.getZ();
        double y = m_joy.getY();
        gDrive.set(ControlMode.PercentOutput, y);
        if (m_Mode == GN_RISING || m_Mode == GN_FRONT_RETRACT || m_Mode == GN_BACK_RETRACT) {
          m_DriveTrain.gurneyTakeControl();
          double left = -0.6*(y - 0.5*z);
          double right = 0.6*(y + 0.5*z);
          m_DriveTrain.gurneyControl(left, right);
        }
      }
    else {
      m_DriveTrain.gurneyReleaseControl();
      gDrive.set(ControlMode.PercentOutput, 0.0);
    }
  }

  private boolean m_Setup_Buttons_Clear = true;
  private void DoSetup() {
    // Clear for next command if all buttons are up...
    if (!m_joy.getRawButton(BUTTON_GO) && !m_joy.getRawButton(BUTTON_LEFT_UP) &&
    !m_joy.getRawButton(BUTTON_LEFT_DOWN) && !m_joy.getRawButton(BUTTON_RIGHT_UP) &&
    !m_joy.getRawButton(BUTTON_RIGHT_DOWN) && !m_joy.getRawButton(BUTTON_SAVE) &&
    !m_joy.getRawButton(BUTTON_SAFE_FRONT)) {
      m_Setup_Buttons_Clear = true; 
      return;
    }
    // Don't do anything if we are not clear.
    if (!m_Setup_Buttons_Clear) {
      return;
    }
    if (m_joy.getRawButton(BUTTON_SAFE_FRONT)) {
      LoadDefaults();
      m_Setup_Buttons_Clear = false;
      return;
    }
    // Do the KUp value...  Joystick pushed in Y.
    if (m_joy.getY() > 0.5) {
      if (m_joy.getRawButton(BUTTON_GO)) {
        m_KUp += 0.05;
        if (m_KUp > 1.0) {
          m_KUp = 1.0;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
    }
    if (m_joy.getY() < -0.5) {
      if (m_joy.getRawButton(BUTTON_GO)) {
        m_KUp -= 0.05;
        if (m_KUp < -1.0) {
          m_KUp = -1.0;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
    }
    // Do the KHold value...  Joystick pushed in X.
    if (m_joy.getX() > 0.5) {
      if (m_joy.getRawButton(BUTTON_GO)) {
        m_KHold += 0.01;
        if (m_KHold > 1.0) {
          m_KHold = 1.0;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
    }
    if (m_joy.getX() < -0.5) {
      if (m_joy.getRawButton(BUTTON_GO)) {
        m_KHold -= 0.01;
        if (m_KHold < -1.0) {
          m_KHold = -1.0;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
    }

    // Do the Rise Bias.
    if ((m_joy.getY() > 0.5)  || (m_joy.getY() < -0.5)) {
      if (m_joy.getRawButton(BUTTON_LEFT_UP)) {
        m_FrontRiseBias += 0.01;
        if (m_FrontRiseBias > 0.25) {
          m_FrontRiseBias = 0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
      if (m_joy.getRawButton(BUTTON_LEFT_DOWN)) {
        m_FrontRiseBias -= 0.01;
        if (m_FrontRiseBias < -0.25) {
          m_FrontRiseBias = -0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
      if (m_joy.getRawButton(BUTTON_RIGHT_UP)) {
        m_BackRiseBias += 0.01;
        if (m_BackRiseBias > 0.25) {
          m_BackRiseBias = 0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
      if (m_joy.getRawButton(BUTTON_RIGHT_DOWN)) {
        m_BackRiseBias -= 0.01;
        if (m_BackRiseBias < -0.25) {
          m_BackRiseBias = -0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
    }

    // Do the Hold Bias.
    if ((m_joy.getX() > 0.5)  || (m_joy.getX() < -0.5)) {
      if (m_joy.getRawButton(BUTTON_LEFT_UP)) {
        m_FrontHoldBias += 0.01;
        if (m_FrontHoldBias > 0.25) {
          m_FrontHoldBias = 0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
      if (m_joy.getRawButton(BUTTON_LEFT_DOWN)) {
        m_FrontHoldBias -= 0.01;
        if (m_FrontHoldBias < -0.25) {
          m_FrontHoldBias = -0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
      if (m_joy.getRawButton(BUTTON_RIGHT_UP)) {
        m_BackHoldBias += 0.01;
        if (m_BackHoldBias > 0.25) {
          m_BackHoldBias = 0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
      if (m_joy.getRawButton(BUTTON_RIGHT_DOWN)) {
        m_BackHoldBias -= 0.01;
        if (m_BackHoldBias < -0.25) {
          m_BackHoldBias = -0.25;
        }
        m_Setup_Buttons_Clear = false;
        return;
      }
    }
    // Check for Saving the data in persistant storge...
    if (m_joy.getRawButton(BUTTON_SAVE)) {
      m_Setup_Buttons_Clear = false;
      SaveSettings();
      return;
    }
  }

  private void DoCalibrate() {
    double x = 0.0;
    double y = 0.0;
    if (m_joy.getRawButton(BUTTON_LEFT_DOWN)) {
      x = -0.1;
    }
    if (m_joy.getRawButton(BUTTON_LEFT_UP)) {
      x = 0.1;
    }
    if (m_joy.getRawButton(BUTTON_RIGHT_DOWN)) {
      y = -0.1;
    }
    if (m_joy.getRawButton(BUTTON_RIGHT_UP)) {
      y = 0.1;
    }
    SetMotors(x, y);
  }

  private void DoAbort() {
    // Just allow motors to drop bot slowly.  Operator is to click
    // abort button again when bot is on the ground.
    m_DriveTrain.gurneyReleaseControl();
    SetMotors(m_KDown, m_KDown);
  }

  private void DoRising() {
    if (m_joy.getRawButton(BUTTON_GO)) {
      SetMotors(m_KUp + m_FrontRiseBias, m_KUp + m_BackRiseBias);
    } else {
      // Hold...
      double f = m_KHold + m_FrontHoldBias;
      double b = m_KHold + m_BackHoldBias;
      int pov = m_joy.getPOV();
      if (pov == 0) {
        f += 0.2;
      }
      if (pov == 180) {
        b += 0.14;
      }
      SetMotors(f, b);
    }
  }

  private void DoFrontRetract() {
    int pov = m_joy.getPOV();
    boolean go = m_joy.getRawButton(BUTTON_GO);
    boolean retract = m_joy.getRawButton(BUTTON_SAFE_FRONT);
    double back = 0.0;
    double front = 0.0;
    if (go) {
      back = m_KUp + m_BackRiseBias;
    } else {
      back = m_KHold + m_BackHoldBias;
      if (pov == 180) {
        back += 0.1;
      }
    }
    if (retract) {
      front = 0.75;
    } else {
      front = 0.0;
      if (m_joy.getRawButton(BUTTON_LEFT_UP)) {
        front = -0.25;
      }
      if (m_joy.getRawButton(BUTTON_LEFT_DOWN)) {
        front = 0.25;
      }
    }
    SetMotors(front, back);
  }

  private void DoBackRetract() {
    int pov = m_joy.getPOV();
    boolean retract = m_joy.getRawButton(BUTTON_SAFE_BACK);
    double back = 0.0;
    double front = 0.0;
    if (retract) {
      back = 0.75;
    } else {
      back = 0.0;
      if (m_joy.getRawButton(BUTTON_RIGHT_UP)) {
        back = -0.25;
      }
      if (m_joy.getRawButton(BUTTON_RIGHT_DOWN)) {
        back = 0.52;
      }
    }
    if (m_joy.getRawButton(BUTTON_LEFT_UP)) {
      front = -0.25;
    }
    if (m_joy.getRawButton(BUTTON_LEFT_DOWN)) {
      front = 0.25;
    }
    SetMotors(front, back);
  }

  private boolean m_FrontBiasClear = true;
  private boolean m_BackBiasClear  = true;

  private void SetBias() {
    if (!m_joy.getRawButton(BUTTON_LEFT_UP) && !m_joy.getRawButton(BUTTON_LEFT_DOWN)) {
      m_FrontBiasClear = true;
    }
    if (!m_joy.getRawButton(BUTTON_RIGHT_UP) && !m_joy.getRawButton(BUTTON_RIGHT_DOWN)) {
      m_BackBiasClear = true;
    }
    // We can only set the Bias in the RISING MODE.
    if (m_Mode != GN_RISING) {
      return;
    }
    double fbias = 0.0;
    double bbias = 0.0;
    if (m_FrontBiasClear) {
      if (m_joy.getRawButton(BUTTON_LEFT_UP)) {
        fbias += cBiasInc;
        m_FrontBiasClear = false;
      }
      if (m_joy.getRawButton(BUTTON_LEFT_DOWN)) {
        fbias -= cBiasInc;
        m_FrontBiasClear = false;
      }
    }
    if (m_BackBiasClear) {
      if (m_joy.getRawButton(BUTTON_RIGHT_UP)) {
        bbias += cBiasInc;
        m_BackBiasClear = false;
      }
      if (m_joy.getRawButton(BUTTON_RIGHT_DOWN)) {
        bbias -= cBiasInc;
        m_BackBiasClear = false;
      }
    }
    // Apply bias to proper sub-mode...
    if (m_joy.getRawButton(BUTTON_GO)) {
      m_FrontRiseBias += fbias;
      m_BackRiseBias += bbias;
    } else {
      m_FrontHoldBias += fbias;
      m_BackHoldBias += bbias;
    }
  }
  
  // RunMode() is the main function that controls the Gurney Motors.
  // It should be called during every cycle of the teleop period
  // so that it can manage the condition of the motors.
  private void RunMode() {
    if (m_Mode == GN_OFF) {
      KillMotors();
      m_DriveTrain.gurneyReleaseControl();
      return;
    }
    if (m_Mode == GN_CALIBRATE) {
      DoCalibrate();
      return;
    }
    if (m_Mode == GN_SETUP) {
      DoSetup();
      return;
    }
    if (m_Mode == GN_ABORT) {
      DoAbort();
      return;
    }
    if (m_Mode == GN_RISING) {
      DoRising();
      return;
    }
    if (m_Mode == GN_FRONT_RETRACT) {
      DoFrontRetract();
      return;
    }
    if (m_Mode == GN_BACK_RETRACT) {
      DoBackRetract();
      return;
    }
  }

// Does the state machine for mode...
  private void SetMode() {
    if (m_Mode == GN_OFF) {
      m_AbortButtonClear = true;
      // From OFF, one of three modes can be started:
      if (m_joy.getRawButton(BUTTON_SETUP)) {
        m_Mode = GN_SETUP;
        return;
      }
      if (m_joy.getRawButton(BUTTON_CALIBRATE)) {
        m_Mode = GN_CALIBRATE;
        return;
      }
      if (m_joy.getRawButton(BUTTON_GO)) {
        m_Mode = GN_RISING;
      }
    }
    if (m_Mode == GN_CALIBRATE) {

      // To stay in Calibrate, must hold the Calibrate button down.
      if (!m_joy.getRawButton(BUTTON_CALIBRATE)) {
        m_Mode = GN_OFF;
      }
      return;
    }
    if (m_Mode == GN_SETUP) {
      // To stay in Setup, must hold the Setup button down.
      if (!m_joy.getRawButton(BUTTON_SETUP)) {
        m_Mode = GN_OFF;
      }
      return;
    }
    if (m_Mode == GN_ABORT) {
      // Second click of Abort will go to OFF.
      if (m_joy.getRawButton(BUTTON_ABORT)) {
        if (m_AbortButtonClear) {
          m_Mode = GN_OFF;
          m_AbortButtonClear = false;
        }
      } else {
        m_AbortButtonClear = true;
      }
    }
    if (m_Mode == GN_RISING) {
      if (m_joy.getRawButton(BUTTON_ABORT)) {
        m_AbortButtonClear = false;
        m_Mode = GN_ABORT;
        return; 
      }
      if (m_joy.getRawButton(BUTTON_SAFE_FRONT)) {
        if (m_joy.getRawButton(BUTTON_ABORT)) {
          m_AbortButtonClear = false;
          m_Mode = GN_ABORT;
          return; 
        }
        m_Mode = GN_FRONT_RETRACT;
      }
    }
    if (m_Mode == GN_FRONT_RETRACT) {
      if (m_joy.getRawButton(BUTTON_ABORT)) {
        m_AbortButtonClear = false;
        m_Mode = GN_ABORT;
      }
      if (m_joy.getRawButton(BUTTON_SAFE_BACK)) {
        m_Mode = GN_BACK_RETRACT;
      }
      return;
    }
    if (m_Mode == GN_BACK_RETRACT) {
      if (m_joy.getRawButton(BUTTON_ABORT)) {
        m_Mode = GN_OFF;
      }
    }
  }
  // These functions define the external Interface to this class.
  // They are called by Robot.java.

  public void report() {
    stab_Mode.setString(GetStringMode());
    stab_Input.setDouble(m_joy.getThrottle());
    stab_FrontEnc.setDouble(gFront.getSelectedSensorPosition());
    stab_BackEnc.setDouble(gBack.getSelectedSensorPosition());
    stab_Counter.setDouble(m_Counter);
    stab_KUp.setDouble(m_KUp);
    stab_KHold.setDouble(m_KHold);
    stab_FrontRiseBias.setDouble(m_FrontRiseBias);
    stab_BackRiseBias.setDouble(m_BackRiseBias);
    stab_FrontHoldBias.setDouble(m_FrontHoldBias);
    stab_BackHoldBias.setDouble(m_BackHoldBias);
    stab_FrontOut.setDouble(m_FrontOut);
    stab_BackOut.setDouble(m_BackOut);
    stab_UseingDefaults.setBoolean(UsingDefaults());
  }

  public void robotInit(Joystick j, DriveTrain d) {
    Init(j, d);
  }

  public void autonomousInit() {
    ResetGurny();
  }

  public void autonomousPeriodic() {
  }

  public void teleopInit() {
    ResetGurny();
    m_Mode = GN_OFF;
  }
  
  public void teleopPeriodic() {
    SetBias();
    SetMode();
    RunMode();
    DriveBot();
  }

  public void testInit() {
  }
  
  public void testPeriodic() {
  }

}








