//Conner Taylor
//Elevator System


package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

public class Elevator  {
  WPI_TalonSRX m_elevator;
  Joystick m_joy;
  int mode = 0;
  double elevSpeed;

  ShuffleboardTab maxSpeedTab = Shuffleboard.getTab("Max Speed");
  NetworkTableEntry elevatorSpeedEntry = maxSpeedTab.add("Elevator Speed", 0.5)
                                  .withWidget(BuiltInWidgets.kNumberSlider)
                                  .withProperties(Map.of("Min", 0, "Max", 1))
                                  .getEntry();

  public void robotInit(Joystick j) {
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);
    m_elevator.configFactoryDefault();
    m_elevator.setSensorPhase(true);
    m_elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    m_elevator.setSelectedSensorPosition(0);
    m_elevator.configClosedloopRamp(0.25);

    m_joy = j;
  }

  
  public void autonomousInit() {
    m_elevator.setSelectedSensorPosition(0);

  }


  
  public void autonomousPeriodic() {


  }

  public void teleopInit() {
  }
  
  public void teleopPeriodic() {
    m_elevator.config_kP(0, 0.1);
    //m_elevator.config_kI(0, 0);
    m_elevator.config_kD(0, 0);
    //m_elevator.config_IntegralZone(0, 0);

    SmartDashboard.putNumber("Elevator Encoder", m_elevator.getSelectedSensorPosition());
    
    // if (dashSpeed != -1) {
      if (m_joy.getRawButton(Constants.encoderReset)) {
        m_elevator.setSelectedSensorPosition(0);
      }

      // if (m_joy.getRawButton(4)) {
      //   m_elevator.set(ControlMode.Position, 10000);
      // } else 
      if (m_joy.getRawAxis(2) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(3) * elevSpeed);

    } else if (m_joy.getRawAxis(3) == 0) {
      m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(2) * -elevSpeed);
    }
  
  // }

  }

  public void report() {
      // dashSpeed = SmartDashboard.getNumber("Elevator Speed", 0);
      elevSpeed = elevatorSpeedEntry.getDouble(0);
}


  
  public void testPeriodic() {
  }
}
