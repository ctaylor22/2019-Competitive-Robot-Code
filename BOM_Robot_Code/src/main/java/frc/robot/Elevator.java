//Conner Taylor
//Elevator System


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

public class Elevator  {
  WPI_TalonSRX m_elevator;
  Joystick m_joy;
  int mode = 0;
  



  public void robotInit(Joystick j) {
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);
    m_elevator.configFactoryDefault();
    m_elevator.setSensorPhase(true);

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
    
//Elevator Goes Up and Down using Triggers on xBox controller
    if (m_joy.getRawAxis(2) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(3) * 1);

    } else if (m_joy.getRawAxis(3) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(2) * -1);
    }

  }

  public void report() {
      
}


  
  public void testPeriodic() {
  }
}
