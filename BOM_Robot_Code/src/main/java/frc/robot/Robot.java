//Conner Taylor
//FRC 2019 Robot Competitive BOM


/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.TimedRobot;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  /**Classes
   * Subsystems of Robots Import
   */
  private DriveTrain m_DriveTrain = new DriveTrain();
  private Elevator m_Elevator = new Elevator();
  private Gurny m_Gurny = new Gurny();
  private Manipulator m_Manipulator = new Manipulator();
  


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_DriveTrain.robotInit();
    m_Elevator.robotInit();
    m_Gurny.robotInit();
    m_Manipulator.robotInit();

  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    m_DriveTrain.report();
    m_Elevator.report();
    m_Gurny.report();
    m_Manipulator.report();

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_DriveTrain.autonomousInit();
    m_Elevator.autonomousInit();
    m_Gurny.autonomousInit();
    m_Manipulator.autonomousInit();

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    m_DriveTrain.autonomousPeriodic();
    m_Elevator.autonomousPeriodic();
    m_Gurny.autonomousPeriodic();
    m_Manipulator.autonomousPeriodic();


  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    m_DriveTrain.teleopPeriodic();
    m_Elevator.teleopPeriodic();
    m_Gurny.teleopPeriodic();
    m_Manipulator.teleopPeriodic();
  
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    m_DriveTrain.testPeriodic();
    m_Elevator.testPeriodic();
    m_Gurny.testPeriodic();
    m_Manipulator.testPeriodic();
  }
}
