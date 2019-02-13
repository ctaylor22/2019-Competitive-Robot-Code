//Holiday Pettijohn
//NavX board class for reading the board at an angle

package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SPI;
import frc.ecommons.Constants;

public class NavX {
    private NetworkTableInstance instance = NetworkTableInstance.getDefault();
    private NetworkTable table = instance.getTable("/gurney");
    private AHRS navX = new AHRS(SPI.Port.kMXP);
    //Tilt on the X-Y axis
    private double rioYaw = Constants.rioYaw;
    //Tilt on the Y-Z axis
    private double rioPitch = Constants.rioPitch;
    //Tilt on the X-Z axis
    private double rioRoll = Constants.rioRoll;
    public double zeroYaw = 0;
    public double zeroPitch = 0;
    public double zeroRoll = 0;
    public NetworkTableEntry tableYaw = table.getEntry("Yaw");
    public NetworkTableEntry tableRoll = table.getEntry("Roll");
    public NetworkTableEntry tablePitch = table.getEntry("Pitch");

    public void NavX() {
        navX.reset();
        navX.getYaw();
        zeroAll();
    }

    public void autonomousInit() {
        zeroAll();
    }
    public void autonomousPeriodic() {
    }

    public void teleopInit() {
        zeroAll();
    }
    public void teleopPeriodic() {
    }

    public void testInit() {
        zeroAll();
    }

    public void testPeriodic() {
        Number yaw = (Number) getYaw();
        Number roll = (Number) getRoll();
        Number pitch = (Number) getPitch();
        tableYaw.setNumber(yaw);
        tableRoll.setNumber(roll);
        tablePitch.setNumber(pitch);
    }

    public void zeroAll() {
        navX.resetDisplacement();
        zeroAngles();
    }

    public void zeroAngles() {
        zeroYaw = navX.getYaw();
        zeroPitch = navX.getPitch();
        zeroRoll = navX.getRoll();
    }

    public double getX() {
        //Side to side
        double yaw = getYaw();
        double roll = getRoll();
        final double rawX = navX.getDisplacementX();
        final double rawY = navX.getDisplacementY();
        final double rawZ = navX.getDisplacementZ();
        final double xFactor = rawX*Math.cos(yaw)+rawX*Math.cos(roll);
        final double yFactor = -rawY*Math.sin(yaw);
        final double zFactor = -rawZ*Math.sin(roll);
        double realX = xFactor+yFactor+zFactor;
        return realX;
    }

    public double getY() {
        //Front and back
        double yaw = getYaw();
        double pitch = getPitch();
        final double rawX = navX.getDisplacementX();
        final double rawY = navX.getDisplacementY();
        final double rawZ = navX.getDisplacementZ();
        final double yFactor = rawY*Math.sin(yaw)+rawY*Math.cos(pitch);
        final double zFactor = -rawZ*Math.sin(pitch);
        final double xFactor = rawX*Math.sin(yaw);
        double realY = yFactor+zFactor+xFactor;
        return realY;
    }

    public double getZ() {
        //Up and down
        double roll = getRoll();
        double pitch = getPitch();
        final double rawX = navX.getDisplacementX();
        final double rawY = navX.getDisplacementY();
        final double rawZ = navX.getDisplacementZ();
        final double zFactor = rawZ*Math.cos(roll)+rawZ*Math.cos(pitch);
        final double xFactor = rawX*Math.sin(roll);
        final double yFactor = rawY*Math.sin(pitch);
        double realZ = zFactor+xFactor+yFactor;
        return realZ;
    }

    public double getPitch() {
        //Rotation about the x axis
        final double rawPitch = navX.getPitch()-zeroPitch;
        /*
        final double rawRoll = navX.getRoll()-zeroRoll;
        final double rawYaw = navX.getYaw()-zeroYaw;
        //The trigonometcially determined factor each raw value played in the real value
        final double pitchFactor = rawPitch*Math.cos(rioRoll)+rawPitch*Math.cos(rioYaw);
        final double rollFactor = rawRoll*Math.sin(rioYaw);
        final double yawFactor = rawYaw*Math.sin(rioRoll);
        final double pitch = (pitchFactor+rollFactor+yawFactor)/2;
        */
        final double pitch = rawPitch;
        return pitch;
    }

    public double getRoll() {
        //Rotation about the y axis
        final double rawRoll = navX.getRoll()-zeroRoll;
        /*
        final double rawYaw = navX.getYaw()-zeroYaw;
        final double rawPitch = navX.getPitch()-zeroPitch;
        //The trigonometcially determined factor each raw value played in the real value
        final double rollFactor = (rawRoll*Math.cos(rioPitch)+rawRoll*Math.cos(rioYaw))/2;
        final double yawFactor = rawYaw*Math.sin(rioPitch);
        final double pitchFactor = rawPitch*Math.sin(rioYaw);
        final double roll = (rollFactor+yawFactor+pitchFactor)/2;
        */
        final double roll = rawRoll;
        return roll;
    }

    public double getYaw() {
        //Rotation about the z axis
        final double rawYaw = navX.getYaw()-zeroYaw;
        /*
        final double rawPitch = navX.getPitch()-zeroPitch;
        final double rawRoll = navX.getRoll()-zeroRoll;
        //The trigonometcially determined factor each raw value played in the real value
        final double yawFactor = (rawYaw*Math.cos(rioPitch)+rawYaw*Math.cos(rioRoll))/2;
        final double pitchFactor = rawPitch*Math.sin(rioRoll);
        final double rollFactor = rawRoll*Math.sin(rioPitch);
        final double yaw = (yawFactor+pitchFactor+rollFactor)/2;
        */
        final double yaw = rawYaw;
        return yaw;
    }


  }
  