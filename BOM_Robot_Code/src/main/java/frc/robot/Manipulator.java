//Conner Taylor
//Manipulator of Robot


package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;

public class Manipulator  {
  Joystick m_joy;
  DoubleSolenoid grabber;
  VictorSPX grabWheels;
  boolean manipulator = false;
  boolean motor = false;



  public void robotInit(Joystick j) {
    // m_joy = j;
    // grabber = new DoubleSolenoid(RobotMap.grab1, RobotMap.grab2);
    // grabWheels = new VictorSPX(RobotMap.grabWheels);
  }


  
  public void autonomousInit() {

  }


  
  public void autonomousPeriodic() {


  }

 public void teleopInit() {
//    grabber.set(DoubleSolenoid.Value.kForward);
  }
  
   public void teleopPeriodic() {
//     if (m_joy.getRawButton(Constants.manipPToggle) && !manipulator) {
//       manipulator = true;
//       if (grabber.get() == (Value.kReverse)) {
//         grabber.set(DoubleSolenoid.Value.kForward);
//       } else if (grabber.get() == (Value.kForward)) {
//         grabber.set(DoubleSolenoid.Value.kReverse);
//       }

//     }
//     if (!m_joy.getRawButton(Constants.manipPToggle)) {
//       manipulator = false;
//     }

//     if (m_joy.getRawButton(Constants.manipMToggle) && !motor) {
//       motor = true;
//       grabWheels.set(ControlMode.PercentOutput, 0.3);
//     }

//     if (!m_joy.getRawButton(Constants.manipMToggle)) {
//       motor = false;
//     }
  }

  public void report() {
      
}


  
  public void testPeriodic() {

  }
}
