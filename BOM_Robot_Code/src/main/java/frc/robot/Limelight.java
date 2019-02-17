/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;


public class Limelight {
    //Inits n such
    Joystick m_Joystick;

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight"); //LimelightNetworkTable

    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tv = table.getEntry("tv");
    NetworkTableEntry getPipe = table.getEntry("getpipe");
    NetworkTableEntry ledMode = table.getEntry("ledMode");

    double x;
    double y;
    double area; 
    double skew; 
    double pipeline;
    double lightMode;

    public void robotInit(Joystick joy){
        m_Joystick = joy;
    }

    public void autonomousInit() {
    }

    public void autonomousPeriodic(){
    }

    public void teleopPeriodic(){
        
        //Setting variables values
        x = tx.getDouble(0.0);
        y = ty.getDouble(0.0);
        area = ta.getDouble(0.0);
        skew = tv.getDouble(0.0);
        pipeline = getPipe.getDouble(0.0);
        lightMode = ledMode.getDouble(0.0);

        //Switching Limelight Light Modes
        if (m_Joystick.getRawButtonPressed(8)){
            if (lightMode<=3) {lightMode++;}
            else {lightMode = 0;}
            ledMode.setNumber(lightMode);
            System.out.println("Limelight in mode: " + lightMode);
        }
        
        //Placing Limelight Values
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelightArea", area);
        SmartDashboard.putNumber("LimelightSkew", skew);
        SmartDashboard.putNumber("LimelightPipeline", pipeline);
        SmartDashboard.putNumber("LimeLightLedMode", lightMode);
    }
    
    public void testPeriodic() {   
    }
    

}
