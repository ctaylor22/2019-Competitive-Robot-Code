//Conner Taylor
//FRC 2019 Robot Competitive BOM


/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;



import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;



public class Robot extends TimedRobot {

  /**Classes
   * Subsystems of Robots Import
   */
  private DriveTrain m_DriveTrain = new DriveTrain();
  private Elevator m_Elevator = new Elevator();
  private Gurny m_Gurny = new Gurny();
  private Manipulator m_Manipulator = new Manipulator();

  Joystick m_driveJoy;
  Joystick m_gJoy;
  Compressor m_comp;
  Boolean compLoop = true;
  
  

  



  @Override
  public void robotInit() {
    m_driveJoy = new Joystick(RobotMap.driveJoy);

    m_gJoy = new Joystick(RobotMap.gJoy);
    m_DriveTrain.robotInit(m_driveJoy);
    m_Elevator.robotInit(m_driveJoy);
    m_Gurny.robotInit(m_gJoy);
    m_Manipulator.robotInit(m_gJoy);

  }

 
  @Override
  public void robotPeriodic() {
    m_DriveTrain.report();
    m_Elevator.report();
    m_Gurny.report();
    m_Manipulator.report();

  }


  @Override
  public void autonomousInit() {
    m_DriveTrain.autonomousInit();
    m_Elevator.autonomousInit();
    m_Gurny.autonomousInit();
    m_Manipulator.autonomousInit();

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

    // m_comp.setClosedLoopControl(false);
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
  public void testPeriodic() {
    m_DriveTrain.testPeriodic();
    m_Elevator.testPeriodic();
    m_Gurny.testPeriodic();
    m_Manipulator.testPeriodic();
  }
}
