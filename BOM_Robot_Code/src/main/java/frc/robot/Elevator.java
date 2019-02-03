//Conner Taylor
//Elevator System


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

public class Elevator  {
  WPI_TalonSRX m_elevator;
  Joystick m_joy;
  int mode = 0;
  double dashSpeed;
  



  public void robotInit(Joystick j) {
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);
    m_elevator.configFactoryDefault();
    m_elevator.setSensorPhase(true);
    SmartDashboard.putNumber("Elevator Speed", 0.25);

    m_joy = j;

  

  }

  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

  public void teleopInit() {
    m_elevator.setSelectedSensorPosition(0);
  }
  
  public void teleopPeriodic() {
    // if (dashSpeed != -1) {
    dashSpeed = 0.4;
//Elevator Goes Up and Down using Triggers on xBox controller
    if (m_joy.getRawAxis(2) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(3) * dashSpeed);

    } else if (m_joy.getRawAxis(3) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(2) * -dashSpeed);
    }
  // }

  }

  public void report() {
      // dashSpeed = SmartDashboard.getNumber("Elevator Speed", 0);
      SmartDashboard.putNumber("dashSpeed", dashSpeed);
}


  
  public void testPeriodic() {
  }
}
