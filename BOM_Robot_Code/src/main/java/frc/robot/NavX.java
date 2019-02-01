//Holiday Pettijohn
//NavX board class for reading the board at an angle

package frc.robot;

import math;
import com.kauailabs.navx.frc.AHRS;
import frc.ecommons.Constants;
import frc.ecommons.RobotMap;
import edu.wpi.first.wpilibj.SerialPort.Port.kMXP;
import edu.wpi.first.wpilibj.SerialPort;


public class NavX  {
    private AHRS ahrs;
    //Tilt on the X-Y axis
    private float rioYaw = Constants.rioYaw;
    //Tilt on the Y-Z axis
    private float rioPitch = Constants.rioPitch;
    //Tilt on the X-Z axis
    private float rioRoll = Constants.rioRoll;
    public float zeroYaw = 0;
    public float zeroPitch = 0;
    public float zeroRoll = 0;

    public void NavX() {
        SerialPort serialPort = new SerialPort(Port, 56000);
        ahrs = new AHRS(serialPort);
        zeroAll();
    }

    public void zeroAll() {
        ahrs.resetDisplacement();
        zeroAngles();
    }

    public void zeroAngles() {
        zeroYaw = ahrs.getYaw();
        zeroPitch = ahrs.getPitch();
        zeroRoll = ahrs.getRoll();
    }

    public void testPeriodic() {
        table.putNumber("Yaw", getYaw());
        table.putNumber("Roll", getRoll());
        table.putNumber("Pitch", getPitch());
    }

    public float getX() {
        //Side to side
        yaw = getYaw();
        roll = getRoll();
        pitch = getPitch();
        final float rawX = ahrs.getDisplacementX();
        final float rawY = ahrs.getDisplacementY();
        final float rawZ = ahrs.getDisplacementZ();
        final float xFactor = rawX*cos(yaw)+rawX*cos(roll);
        final float yFactor = -rawY*sin(yaw);
        final float zFactor = -rawZ*sin(roll);
        float realX = xFactor+yFactor+zFactor;
    }

    public float getY() {
        //Front and back
        yaw = getYaw();
        roll = getRoll();
        pitch = getPitch();
        float rawX = ahrs.getDisplacementX();
        float rawY = ahrs.getDisplacementY();
        float rawZ = ahrs.getDisplacementZ();
        final float yFactor = rawY*sin(yaw)+rawY*cos(pitch);
        final float zFactor = -rawZ*sin(pitch);
        final float xFactor = rawX*sin(yaw);
        float realY = yFactor+zFactor+xFactor;
    }

    public float getZ() {
        //Up and down
        yaw = getYaw();
        roll = getRoll();
        pitch = getPitch();
        final float rawX = ahrs.getDisplacementX();
        final float rawY = ahrs.getDisplacementY();
        final float rawZ = ahrs.getDisplacementZ();
        final float zFactor = rawZ*cos(roll)+rawZ*cos(pitch);
        final float xFactor = rawX*sin(roll);
        final float yFactor = rawY*sin(pitch);
        float realZ = zFactor+xFactor+yFactor;
    }

    public float getRoll() {
        final float roll = ahrs.getRoll()-rioRoll-zeroRoll;
    }

    public float getYaw() {
        final float yaw = ahrs.getYaw()-rioYaw-zeroYaw;
    }

    public float getPitch() {
        final float pitch = ahrs.getPitch()-rioPitch-zeroPitch;
    }
  }
  