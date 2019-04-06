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
    NetworkTable messageTable = NetworkTableInstance.getDefault().getTable("visionmessages");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tv = table.getEntry("tv");
    NetworkTableEntry getPipe = table.getEntry("getpipe");
    NetworkTableEntry ledMode = table.getEntry("ledMode");
    //Raw network table data
    NetworkTableEntry a0 = table.getEntry("ta0");
    NetworkTableEntry a1 = table.getEntry("ta1");
    //Message table data
    NetworkTableEntry ortho = messageTable.getEntry("ortho");
    NetworkTableEntry xe = messageTable.getEntry("xerror");
    NetworkTableEntry ye = messageTable.getEntry("yerror");

    double x;
    double y;
    double area; 
    double skew; 
    double pipeline;
    double lightMode;
    boolean target;
    //Raw network table values
    double m_area0;
    double m_area1;
    //Goal points for target alignment
    double xgoal = .7;
    double ygoal = 4;
    //Robot angle relitive to being orthoganal with targets
    double m_orthoError;

    // Joystick init
    public Limelight(Joystick joystick){
        m_Joystick = joystick;
    }

    public void robotInit() {

    }

    public void autonomousInit() {
    }

    public void autonomousPeriodic(){
    }

    public void teleopInit() {

    }

    public void teleopPeriodic(){
        double xerror, yerror;
        //Setting variables values
        x = tx.getDouble(0.0);
        y = ty.getDouble(0.0);
        area = ta.getDouble(0.0);
        skew = tv.getDouble(0.0);
        pipeline = getPipe.getDouble(0.0);
        lightMode = ledMode.getDouble(0.0);

        if (skew == 0) {
            target = false;
        } else {
            target = true;
        }
        //Switching Limelight Light Modes
        if (m_Joystick.getRawButtonPressed(8)){
            //If the limelight is active
            //Turn on the lights
            if (lightMode<=3) {lightMode++;} else {lightMode = 0;}
            ledMode.setNumber(lightMode);
            System.out.println("Limelight in mode: " + lightMode);
            //Finding the angle at which the robot approches the targets
            if (tv.getBoolean(false)) {
                //If the limelight can see targets
                //Assumes that if the robot can see one target, it can see both
                m_area0 = a0.getDouble(0.0);
                m_area1 = a1.getDouble(0.0);
                if (m_area0 != 0.0 && m_area1 != 0.0) {
                    //If the targets have a valid area value (to prevent division by 0)
                    m_orthoError = findOrthoError(m_area0, m_area1);
                    ortho.setNumber(m_orthoError);
                }
            }
        } else {/*Turn off the lights*/}
        //If the limelight can see targets
        //Assumes that if the robot can see one target, it can see both
        m_area0 = (double) a0.getNumber(0.0);
        m_area1 = (double) a1.getNumber(0.0);
        if (m_area0 != 0.0 && m_area1 != 0.0) {
            //If the targets have a valid area value (to prevent division by 0)
            m_orthoError = findOrthoError(m_area0, m_area1);
            ortho.setNumber(m_orthoError);
        }
        xerror = x-xgoal;
        yerror = y-ygoal;
        xe.setNumber(xerror);
        ye.setNumber(yerror);
        //Placing Limelight Values
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelightArea", area);
        SmartDashboard.putNumber("LimelightSkew", skew);
        SmartDashboard.putNumber("LimelightPipeline", pipeline);
        SmartDashboard.putNumber("LimeLightLedMode", lightMode);
    }
    
    public void testInit() {

    }

    public void testPeriodic() {

    }

    private double findOrthoError(double area0, double area1) {
        double ratio, error;
        boolean flipped = false;
        ratio = area0/area1;
        //Makes error smaller as the ratio between areas reaches 1
        if (ratio > 1) {
            ratio = 1/ratio;
            flipped = true;
        }
        error = 1-ratio;
        if (flipped) {
            error *= -1;
        }
        return error;
    }
    //Guacamole
    

}
