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
  



  public void robotInit(Joystick j) {
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);

    m_joy = j;

  }

  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

 
  
  public void teleopPeriodic() {
    
//Elevator Goes Up and Down using Triggers on xBox controller
    if (m_joy.getRawButton(Constants.elevatorUp)) {
      m_elevator.set(ControlMode.PercentOutput, 0.3);

    } else if (!m_joy.getRawButton(Constants.elevatorUp)) {
      m_elevator.set(ControlMode.PercentOutput, 0);

    }


    if (m_joy.getRawButton(Constants.elevatorDown)) {
      m_elevator.set(ControlMode.PercentOutput, -0.3);

    } else if (!m_joy.getRawButton(Constants.elevatorDown)) {
      m_elevator.set(ControlMode.PercentOutput, 0);

    }

  }

  public void report() {
      
}


  
  public void testPeriodic() {
  }
}
