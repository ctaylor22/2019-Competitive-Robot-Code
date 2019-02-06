//Conner Taylor
//Elevator System


package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.RobotMap;

public class Elevator  {
  WPI_TalonSRX m_elevator;
  Joystick m_joy;
  int mode = 0;
  double dashSpeed;

  ShuffleboardTab maxSpeed;
  NetworkTableEntry elevatorSpeedEntry;
  



  public void robotInit(Joystick j) {
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);
    m_elevator.configFactoryDefault();
    m_elevator.setSensorPhase(true);

    m_joy = j;

    maxSpeed = Shuffleboard.getTab("Max Speed");
    
    elevatorSpeedEntry = maxSpeed.add("Elevator Speed", 0.4)
                                  .withWidget(BuiltInWidgets.kNumberSlider)
                                  .withProperties(Map.of("min", 0, "max", 1))
                                  .getEntry();

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
      dashSpeed = elevatorSpeedEntry.getDouble(0);
}


  
  public void testPeriodic() {
  }
}
