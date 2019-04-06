//Conner Taylor
//FRC 2019 Robot Competitive BOM


/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;



import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;


public class Robot extends TimedRobot {
  private DriveTrain m_DriveTrain = new DriveTrain();
  private Elevator m_Elevator = new Elevator();
  
  // private Gurny m_Gurny = new Gurny();
  private Gurny_josh m_Gurny = new Gurny_josh();

  private Manipulator m_Manipulator = new Manipulator();
  private NavX m_NavX = new NavX();
  Joystick m_driveJoy;
  Joystick m_gJoy;
  Joystick m_eJoy;
  Compressor m_comp;
  Boolean compLoop = false;
  UsbCamera camera1;
  UsbCamera camera2;
  
  int ledMode = 0;
  DigitalOutput dioSlot0 = new DigitalOutput(0);
  DigitalOutput dioSlot1 = new DigitalOutput(1);
  DigitalOutput dioSlot2 = new DigitalOutput(2);

  // Watchdog watch;

  AnalogInput pressure;
  double pressureDouble;
  ShuffleboardTab tab = Shuffleboard.getTab("Beginning Game");
  NetworkTableEntry pressureEntry = tab.add("Pressure", 0)
                                       .withWidget(BuiltInWidgets.kDial)
                                       .withPosition(9, 0)
                                       .withSize(1, 1)
                                       .getEntry();
  NetworkTableEntry compressorEntry = tab.add("Compressor", false)
                                         .withPosition(10, 0)
                                         .withSize(1, 1)
                                         .withWidget(BuiltInWidgets.kBooleanBox)
                                         .getEntry();

  NetworkTableEntry cameraEntry;
  ComplexWidget cameraWidget;
  ComplexWidget cameraWidget2;

  @Override
  public void robotInit() {
    camera1 = CameraServer.getInstance().startAutomaticCapture(0);
    camera2 = CameraServer.getInstance().startAutomaticCapture(1);

    cameraWidget = tab.add("Side", camera1).withPosition(5, 3).withSize(4, 3);
    cameraWidget2 = tab.add("Center", camera2).withPosition(5, 0).withSize(4, 3);
    Shuffleboard.selectTab("Beginning Game");
    m_driveJoy = new Joystick(RobotMap.driveJoy);
    pressure = new AnalogInput(0);
    m_gJoy = new Joystick(RobotMap.gJoy);
    m_eJoy = new Joystick(RobotMap.eJoy);
    m_DriveTrain.robotInit(m_driveJoy, m_gJoy);
    m_Elevator.robotInit(m_eJoy);
    m_Gurny.robotInit(m_gJoy);
    m_Manipulator.robotInit(m_driveJoy);
  //  m_Limelight.robotInit(m_driveJoy); //functions work with gurney, got rid of joystick parameter

    m_comp = new Compressor();

    m_comp.setClosedLoopControl(false);

    //CameraServer.getInstance().startAutomaticCapture();

    // DIO config
    dioSlot0.disablePWM();
    dioSlot1.disablePWM();
    dioSlot2.disablePWM();
    ledCom(0);
  }

 
  @Override
  public void robotPeriodic() {
    double pressureCalc = 250 * (pressure.getVoltage()/5) - 25;
    m_DriveTrain.report();
    m_Elevator.report();
    m_Gurny.report();
    m_Manipulator.report();
    m_Manipulator.robotPeriodic();
    pressureEntry.setDouble(pressureCalc);
    compressorEntry.setBoolean(m_comp.getClosedLoopControl());
  }


  @Override
  public void autonomousInit() {
    m_DriveTrain.autonomousInit();
    m_Elevator.autonomousInit();
    m_Gurny.autonomousInit();
    m_Manipulator.autonomousInit();
    ledCom(1);
  }


  @Override
  public void autonomousPeriodic() {
    m_DriveTrain.autonomousPeriodic();
    m_Elevator.autonomousPeriodic();
    m_Gurny.autonomousPeriodic();
    m_Manipulator.autonomousPeriodic();
  }

  @Override
  public void teleopInit() {
    m_Elevator.teleopInit();
    m_Manipulator.teleopInit();
    m_DriveTrain.teleopInit();
    m_Gurny.teleopInit();
  }


  @Override
  public void teleopPeriodic() {
    m_DriveTrain.teleopPeriodic();
    m_Elevator.teleopPeriodic();
    m_Gurny.teleopPeriodic();
    m_Manipulator.teleopPeriodic();

    if (m_driveJoy.getRawButton(Constants.compressor) && compLoop) {
      compLoop = false;
      m_comp.setClosedLoopControl(!m_comp.getClosedLoopControl());
    }
    if (!m_driveJoy.getRawButton(Constants.compressor)) {
      compLoop = true;
    }
  }

  @Override
  public void testInit() {
    m_DriveTrain.testInit();
  }

  @Override
  public void testPeriodic() {
    m_DriveTrain.testPeriodic();
    m_Elevator.testPeriodic();
    m_Gurny.testPeriodic();
    m_Manipulator.testPeriodic();
    m_NavX.testPeriodic();
  }

  void ledCom(int i) {
    ledMode = i;
    if (ledMode == 0) {
    dioSlot0.set(false);
    dioSlot1.set(false);
    dioSlot2.set(false);
    } else if (ledMode == 1) {
    dioSlot0.set(false);
    dioSlot1.set(false);
    dioSlot2.set(true);
    } else if (ledMode == 2) {
    dioSlot0.set(false);
    dioSlot1.set(true);
    dioSlot2.set(false);
    } else if (ledMode == 3) {
    dioSlot0.set(false);
    dioSlot1.set(true);
    dioSlot2.set(true);
    } else if (ledMode == 4) {
    dioSlot0.set(true);
    dioSlot1.set(false);
    dioSlot2.set(false);
    } else if (ledMode == 5) {
    dioSlot0.set(true);
    dioSlot1.set(false);
    dioSlot2.set(true);
    } else if (ledMode == 6) {
    dioSlot0.set(true);
    dioSlot1.set(true);
    dioSlot2.set(false);
    } else if (ledMode == 7) {
    dioSlot0.set(true);
    dioSlot1.set(true);
    dioSlot2.set(true);
    }
  }
}
