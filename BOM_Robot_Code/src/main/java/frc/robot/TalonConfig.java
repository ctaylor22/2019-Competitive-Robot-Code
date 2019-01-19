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



  public void TalonInit(WPI_TalonSRX... talon) {

    motor1 = talon[1];
    motor2 = talon[2];
    motor3 = talon[3];
    motor4 = talon[4];
    motor5 = talon[5];
    motor6 = talon[6];

    motor1.configFactoryDefault();
    motor2.configFactoryDefault();
    motor3.configFactoryDefault();
    motor4.configFactoryDefault();
    motor5.configFactoryDefault();
    motor6.configFactoryDefault();



  }
}