//Conner Taylor
//Drive Train of Robot


package frc.robot;

import java.sql.Time;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.RobotMap;
import frc.ecommons.Constants;
import frc.utilities.TurnRadius;

// import edu.wpi.first.wpilibj.shuffleboard.BuiltInTypes;

// import edu.wpi.first.wpilibj.shuffleboard;
 
public class DriveTrain  {

  String lowGear = "Low Gear";
  String highGear = "High Gear";

  NetworkTable limelight_table = NetworkTableInstance.getDefault().getTable("limelight"); //LimelightNetworkTable


  // Joysticks/Controllers
  Joystick m_joy;
  Joystick m_gJoy;
  //Talons
  WPI_TalonSRX m_rMaster;
  WPI_TalonSRX m_lMaster;
  
  //Victors
  WPI_VictorSPX m_rSlave1;
  WPI_VictorSPX m_rSlave2;
  WPI_VictorSPX m_lSlave1;

  //Solenoids
  DoubleSolenoid dogGearSolenoid;

  //Loops
  boolean dgLoop = false;
  boolean driveTestLoop = false;

  double driveSpeed = 0.5;

  Timer run;
  ShuffleboardTab testMode = Shuffleboard.getTab("Test Mode");
  NetworkTableEntry testDriveEntry = testMode.add("Drive Test", false)
                                             .withWidget(BuiltInWidgets.kToggleButton)
                                             .getEntry();
  NetworkTableEntry testTimerEntry = testMode.add("Timer", 0)
                                             .withWidget(BuiltInWidgets.kTextView)
                                             .getEntry();
  
  ShuffleboardTab tab = Shuffleboard.getTab("Beginning Game");

  ShuffleboardTab motor = Shuffleboard.getTab("Motors");
  

  NetworkTableEntry rightEncoderEntry = tab.add("Right Encoder", 0)
                                           .withSize(1, 1)
                                           .withPosition(1, 1) 
                                           .getEntry();

  NetworkTableEntry leftEncoderEntry = tab.add("Left Encoder", 0)
                                          .withSize(1, 1)
                                          .withPosition(0, 1)
                                          .getEntry();


  double[][] paths = {{2.0, 300.0, 3.0, 1.0, 90.0, 1.0}/*,
                      {0.0, 100.0, 3.0}*/};

  double[][] motorSpeeds = {{0, 0, 0, 0}};

  int turnPath = 0;

  public void TalonConfig() {
    //Configs Talon to default
    m_rMaster.configFactoryDefault();
    m_lMaster.configFactoryDefault();
    m_rSlave1.configFactoryDefault();
    m_rSlave2.configFactoryDefault();
    m_lSlave1.configFactoryDefault();
    m_rSlave1.follow(m_rMaster);
    m_rSlave2.follow(m_rMaster);
    m_lSlave1.follow(m_lMaster);
    //Motors go right way
    m_rMaster.setSensorPhase(false);
    m_lMaster.setSensorPhase(false);


    m_rMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    m_lMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

    m_rMaster.setInverted(false);
    m_rSlave1.setInverted(InvertType.FollowMaster);
    m_rSlave2.setInverted(InvertType.FollowMaster);

    m_lMaster.setInverted(false);
    m_lSlave1.setInverted(InvertType.FollowMaster);

    m_rMaster.setNeutralMode(NeutralMode.Brake);
    m_lMaster.setNeutralMode(NeutralMode.Brake);
    m_lSlave1.setNeutralMode(NeutralMode.Brake);
    m_rSlave1.setNeutralMode(NeutralMode.Brake);
    m_rSlave2.setNeutralMode(NeutralMode.Brake);
    
  
    //ARGS (Slot, Value)
    m_rMaster.config_kF(0, 0.0);
    m_rMaster.config_kP(0, 0);
    m_rMaster.config_kI(0, 0);
    m_rMaster.config_kD(0, 0);
    m_rMaster.config_IntegralZone(0, 1000);

    m_lMaster.config_kF(0, 0.00);
    m_lMaster.config_kP(0, 0);
    m_lMaster.config_kI(0, 0);
    m_lMaster.config_kD(0, 0);
    m_lMaster.config_IntegralZone(0, 1000);


    int sensorUnitsPer100ms = 8000;
    m_rMaster.configMotionCruiseVelocity(sensorUnitsPer100ms);
    m_lMaster.configMotionCruiseVelocity(sensorUnitsPer100ms);

    int sensorUnitsPer100ms_per_sec = 10240;
    m_rMaster.configMotionAcceleration(sensorUnitsPer100ms_per_sec);
    m_lMaster.configMotionAcceleration(sensorUnitsPer100ms_per_sec);


  }


  public void robotInit(Joystick j, Joystick gJ) {
    run = new Timer();
    //Joysticks
    m_joy = j;
    m_gJoy = gJ;

    //Talons - IDS found in ecommons.RobotMap
    m_rMaster = new WPI_TalonSRX(RobotMap.rMaster);
    m_lMaster = new WPI_TalonSRX(RobotMap.lMaster);

    //Victors - IDS found in ecommons.RobotMap
    m_rSlave1 = new WPI_VictorSPX(RobotMap.rSlave1);
    m_rSlave2 = new WPI_VictorSPX(RobotMap.rSlave2);
    m_lSlave1 = new WPI_VictorSPX(RobotMap.lSlave1);

    //DoubleSolenoid
    dogGearSolenoid = new DoubleSolenoid(RobotMap.dogGearSolenoid1, RobotMap.dogGearSolenoid2);

    TalonConfig();

 
  }

  public void autonomousInit() {
    // double[] piece;
    // for (int ind = 0; ind < paths.length; ind++) {
    //   piece = paths[ind];
    //   if (piece[0] != 0) {
    //     motorSpeeds[ind] = TurnRadius.calculateTurnRadius(piece[0], piece[1], piece[2], piece[3], piece[4], piece[5]);
    //   } else {
    //     //Default Velocity
    //     motorSpeeds[ind][0] = piece[1];
    //     motorSpeeds[ind][1] = piece[1];
    //     //Encoder Offset
    //     motorSpeeds[ind][2] = piece[2];
    //     motorSpeeds[ind][3] = piece[2];
    //   }
    // }
    teleopInit();
  }
  
  public void autonomousPeriodic() {
    // followTurnPath();
    teleopPeriodic();
  }

  public void followTurnPath() {
    if (turnPath == paths.length) {
      turnPath = -1;
    } else if (turnPath == -1) {
      return;
    } else {
      if (followPath(turnPath)) {
        turnPath += 1;
      }
    }
  }

  
  public boolean followPath(int pathNum) {
    boolean rightIsComplete = false;
    boolean leftIsComplete = false;
    double rightEncoderGoal = motorSpeeds[pathNum][2] + m_rMaster.getSelectedSensorPosition();
    double leftEncoderGoal = motorSpeeds[pathNum][3] + m_lMaster.getSelectedSensorPosition();
    double righterr = rightEncoderGoal - m_rMaster.getSelectedSensorPosition();
    double lefterr = leftEncoderGoal - m_lMaster.getSelectedSensorPosition();
    //Velocity in RPM
    righterr = rightEncoderGoal - m_rMaster.getSelectedSensorPosition();
    lefterr = rightEncoderGoal - m_lMaster.getSelectedSensorPosition();
    if (Math.abs(righterr) <= 10) {
      m_rMaster.set(ControlMode.Velocity, motorSpeeds[pathNum][0]*(righterr/1000));
    } else {
      rightIsComplete = true;
      m_rMaster.set(ControlMode.PercentOutput, 0);
    }
    if (Math.abs(lefterr) <= 10) {
      m_lMaster.set(ControlMode.Velocity, motorSpeeds[pathNum][1]*(lefterr/1000));
    } else {
      leftIsComplete = true;
      m_lMaster.set(ControlMode.PercentOutput, 0);
    }
    if (rightIsComplete && leftIsComplete) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  
  public void teleopInit() {
    dogGearSolenoid.set(DoubleSolenoid.Value.kForward);
  }

  public void teleopPeriodic() {

    //Equation for ARCADE DRIVE
    double xAxis, yAxis;
    xAxis = 0.35 * m_joy.getRawAxis(Constants.xAxis);
    // * -1 to correct axis sign
    yAxis = -1*m_joy.getRawAxis(Constants.yAxis);
    
    //Equation for Arcade Drive
    double leftSide, rightSide;
    rightSide = -(yAxis - xAxis);
    leftSide = yAxis + xAxis;
    
    // if button, limelight turn
    if (m_joy.getRawButton(Constants.limelightAutoTurn) && limelight_table.getEntry("tv").getDouble(0)) {
      rightSide;
      leftSide;
      double limelight_kP = 0.05;
      double limelight_kF = 0.05;
      double heading_error = limelight_table.getEntry("tx").getDouble(0.0);
      double steering_adjust = 0.0;
      if (Math.abs(heading_error) > 0.05)
      {
        steering_adjust = limelight_kP*heading_error + limelight_kF;
      }
      // #TODO: check these signs
      leftSide -= steering_adjust;
      rightSide -= steering_adjust;

      // clamp the output to +/- 0.7 
      double min = -.7;
      double max = 0.7;
      leftSide = Math.max(min, Math.min(max, leftSide));
      rightSide = Math.max(min, Math.min(max, rightSide));
    }
    // else percent out
    else {
      
    }   

    m_lMaster.set(ControlMode.PercentOutput, leftSide);
    m_rMaster.set(ControlMode.PercentOutput, rightSide);
  }

  public void report() {
    
    rightEncoderEntry.setDouble(m_rMaster.getSelectedSensorPosition());
    leftEncoderEntry.setDouble(m_lMaster.getSelectedSensorPosition());

    SmartDashboard.putNumber("Sensor Velocity", m_rMaster.getSelectedSensorVelocity());
  }

  /**
   * This function is called periodically during test mode.
   */
  public void testInit() {
    run.reset();
    testDriveEntry.setBoolean(false);
    
  }
  public void testPeriodic() {
    m_lSlave1.follow(m_lMaster);
    m_rSlave1.follow(m_rMaster);
    m_rSlave2.follow(m_rMaster);

    //Test to see if driving works
    boolean driveTestCheck = testDriveEntry.getBoolean(false);
    testTimerEntry.setDouble(run.get());
    if (driveTestCheck && !driveTestLoop) {
      driveTestLoop = true;
      run.reset();
      run.start();

    }
    if (run.get() < 3 && run.get() > 0.1) {
      m_rMaster.set(ControlMode.PercentOutput, 0.7);
      m_lMaster.set(ControlMode.PercentOutput, 0.7);
    } else if (run.get() > 3 && run.get() < 6) {
      m_rMaster.set(ControlMode.PercentOutput, -0.7);
      m_lMaster.set(ControlMode.PercentOutput, -0.7);
    } else if (run.get() >= 6) {
      m_lMaster.set(ControlMode.PercentOutput, 0);
      m_rMaster.set(ControlMode.PercentOutput, 0);
      testDriveEntry.setBoolean(false);
    }
    if (!driveTestCheck) {
      driveTestLoop = false;
      m_lMaster.set(ControlMode.PercentOutput, 0);
      m_rMaster.set(ControlMode.PercentOutput, 0);
      run.stop();
      run.reset();
     }

  }
}
