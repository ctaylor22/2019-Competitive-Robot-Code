package frc.utilities;

import frc.ecommons.Constants;

public class TurnRadius {
  static final double pi = 3.141592653589792384626433832;

  static public double[] calculateTurnRadius(double turnradius, double rpm, double wheelradius, double wheeloffset, double degrees, double direction) {
      /* Calculates the speeds of the left and right motors for the given turn
       * turnradius and wheelradius should be provided in inches
       * direction is true if turning right and false if turning left
       */
      turnradius *= 12;
      wheeloffset *= 12;
      final double ips = rpmToIps(rpm, wheelradius);
      final double radians = (pi*degrees)/180;
      final double main_dist = calculateTurnDistance(turnradius, radians);
      final double time = main_dist/ips;
      double[] radiuses = {0, 0, 0};
      if (direction == 1) {
          radiuses[0] = calculateTurnDistance(turnradius-wheeloffset, radians);
          radiuses[1] = calculateTurnDistance(turnradius+wheeloffset, radians);
          radiuses[2] = feetToEncoder(turnradius-wheeloffset, wheelradius);
          radiuses[3] = feetToEncoder(turnradius+wheeloffset, wheelradius);
      } else if (direction == 2) {
          radiuses[0] = calculateTurnDistance(turnradius+wheeloffset, radians);
          radiuses[1] = calculateTurnDistance(turnradius-wheeloffset, radians);
          radiuses[2] = feetToEncoder(turnradius+wheeloffset, wheelradius);
          radiuses[3] = feetToEncoder(turnradius-wheeloffset, wheelradius);
      }
      double[] radius_speeds = {radiuses[0]/time, radiuses[1]/time};
      radius_speeds[0] = ipsToRpm(radius_speeds[0], wheelradius);
      radius_speeds[1] = ipsToRpm(radius_speeds[1], wheelradius);
      return radius_speeds;
  }

  public static double turnRadiusRatio(double turnradius, double wheeloffset, double direction) {
    /* Returns how many spins the right motor needs to do for each spin of the left motor
     * turnradius: the radius of the circle to turn about, currently tangent to the robot
     * wheeloffset: the distance of the wheels from the center of the robot
     * direction: wether the robot wants to turn right or left
     */
    //Converts arguments to inches
    double ratio;
    double rightradius;
    double leftradius;
    turnradius *= 12;
    wheeloffset *= 12;
    if (direction == 1) {
        rightradius = turnradius-wheeloffset;
        leftradius = turnradius+wheeloffset;
    } else {
        rightradius = turnradius+wheeloffset;
        leftradius = turnradius-wheeloffset;
    }
    ratio = rightradius/leftradius;
    return ratio;
  }
  static double feetToEncoder(double dist, double wheelradius) {
      return (dist/(2*wheelradius*pi))*Constants.ticksPerRotation;
  }

  static double rpmToIps(double rpm, double wheelradius) {
      return 60*rpm/(2*pi*wheelradius);
  }

  static double ipsToRpm(double ips, double wheelradius) {
      return ips*(2*pi*wheelradius)/60;
  }

  static double calculateTurnDistance(double radius, double radians) {
      return radius*radians;
  }
}