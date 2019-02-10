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
//import sun.nio.ch.Net;

public class Elevator  {
  WPI_TalonSRX m_elevator;
  Joystick m_joy;
  int mode = 0;
  double elevSpeed;

  int heights[] = new int[]{100, 2000, 4000, 6000, 8000, 10000, 12000, 14000, 15500};
  double previous_target = 1;
  double target_position = 1;
  ShuffleboardTab maxSpeedTab = Shuffleboard.getTab("Max Speed");
  NetworkTableEntry elevatorSpeedEntry = maxSpeedTab.add("Elevator Speed", 0.5)
                                  .withWidget(BuiltInWidgets.kNumberSlider)
                                  .withProperties(Map.of("Min", 0, "Max", 1))
                                  .getEntry();
  NetworkTableEntry elevatorEncoderPosition = maxSpeedTab.add("Elev Encoder Position", 0).getEntry();
  NetworkTableEntry elevatorPositionEntry = maxSpeedTab.add("Elevator Target Position", 50).withWidget(BuiltInWidgets.kTextView).getEntry();

  void setUpPID() {
    m_elevator.configMotionAcceleration(10000);    
    // 16000 units in 2 secs = 8000 units per second = 800 units per 100ms
    // HOWEVER, during testing, the more accurate number in the units per second
    m_elevator.configMotionCruiseVelocity(10667);

    m_elevator.config_kP(0, 0.38);
    m_elevator.config_kI(0, 0.0013);
    m_elevator.config_kD(0, 0);
    m_elevator.config_IntegralZone(0, 1800);
  }

  void setDownPID() {
    m_elevator.configMotionAcceleration(0);    
    m_elevator.configMotionCruiseVelocity(0);
    
    m_elevator.config_kP(0, 0);
    m_elevator.config_kI(0, 0);
    m_elevator.config_kD(0, 0);
    m_elevator.config_IntegralZone(0, 0);
  }

  public void robotInit(Joystick j) {
    m_elevator = new WPI_TalonSRX(RobotMap.elevator);
    m_elevator.configFactoryDefault();
    m_elevator.setSensorPhase(true);
    m_elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
    m_elevator.setSelectedSensorPosition(0);
    m_elevator.configClosedloopRamp(0);
    m_elevator.configAllowableClosedloopError(0, 0, 0);
    setUpPID();
    
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
    // if (dashSpeed != -1) {
      if (m_joy.getRawButton(Constants.encoderReset)) {
        m_elevator.setSelectedSensorPosition(0);
      }

      if (m_joy.getRawButton(4)) {
        previous_target = target_position;
        target_position =  elevatorPositionEntry.getDouble(0);
        if(target_position >= previous_target) {
          setUpPID();
        } else {
          setDownPID();
        }
        m_elevator.set(ControlMode.MotionMagic, heights[(int) target_position]);

      } else if (m_joy.getRawAxis(2) == 0) {
        m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(3) * elevSpeed);

      } else if (m_joy.getRawAxis(3) == 0) {
        m_elevator.set(ControlMode.PercentOutput, m_joy.getRawAxis(2) * -elevSpeed);
      }
  
  // }

  }

  public void report() {
      // dashSpeed = SmartDashboard.getNumber("Elevator Speed", 0);
      elevSpeed = elevatorSpeedEntry.getDouble(0);
      elevatorEncoderPosition.setDouble(m_elevator.getSelectedSensorPosition(0));
}


  
  public void testPeriodic() {
  }
}
