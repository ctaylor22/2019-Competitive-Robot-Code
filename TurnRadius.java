

public class TurnRadius {
  final double pi = 3.141592653589792384626433832;

  double[] calculateTurnRadius(double turnradius, double rpm, double wheelradius, double wheeloffset, double degrees, boolean direction) {
      /* Calculates the speeds of the left and right motors for the given turn
       * turnradius and wheelradius should be provided in inches
       * direction is true if turning right and false if turning left
       */
      final double radians = (pi*degrees)/180;
      final double main_dist = calculateTurnDistance(wheelradius, radians);
      final double time = main_dist/velocity;
      if (direction) {
          final double[] radiuses = {calculateTurnDistance(wheelradius-wheeloffset, radians), calculateTurnDistance(wheelradius-wheeloffset, radians)};
      }
      double[] radius_speeds = {radiuses[0]/time, radiuses[1]/time};
      radius_speeds[0] = ipsToRpm(radius_speeds[0], wheelradius);
      radius_speeds[1] = ipsToRpm(radius_speeds[1], wheelradius);
      return radius_speeds;
  }

  double rpmToIps(double rpm, double wheelradius) {
      return 60*rpm/(2*pi*wheelradius);
  }

  double ipsToRpm(double ips, double wheelradius) {
      return ips*(2*pi*wheelradius)/60;
  }

  double calculateTurnDistance(double radius, double radians) {
      return radius*radians;
  }
}