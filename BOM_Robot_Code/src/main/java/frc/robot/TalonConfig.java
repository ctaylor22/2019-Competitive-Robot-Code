//Conner Taylor
//Starting Config of Tallons


package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class TalonConfig  {
    WPI_TalonSRX motor1;
    WPI_TalonSRX motor2;
    WPI_TalonSRX motor3;
    WPI_TalonSRX motor4;
    WPI_TalonSRX motor5;
    WPI_TalonSRX motor6;


  public void driveMotors(WPI_TalonSRX talon1, WPI_TalonSRX talon2, WPI_TalonSRX talon3, WPI_TalonSRX talon4, WPI_TalonSRX talon5, WPI_TalonSRX talon6) {



    motor1.configFactoryDefault();
    motor2.configFactoryDefault();
    motor3.configFactoryDefault();
    motor4.configFactoryDefault();
    motor5.configFactoryDefault();
    motor6.configFactoryDefault();



  }
}