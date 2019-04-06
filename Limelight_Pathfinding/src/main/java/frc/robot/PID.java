package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

class PID {
    double m_p;
    double m_i;
    double m_d;
    double m_k;
    double i_buildup;
    double last_time;
    double last_error;
    Timer m_timer;
    ShuffleboardTab pid_tab = null;
    NetworkTableEntry p_entry = null;
    NetworkTableEntry i_entry = null;
    NetworkTableEntry d_entry = null;

    public void robotInit(double p, double i, double d, double k) {
        m_p = p;
        m_i = i;
        m_d = d;
        m_k = k;
        m_timer = new Timer();
        last_time = 0;
        last_error = 0;
    }

    public void robotInit(double p, double i, double d, double k, boolean tuneing) {
        //Tuning version of pid initializer
        m_p = p;
        m_i = i;
        m_d = d;
        m_k = k;
        m_timer = new Timer();
        last_time = 0;
        last_error = 0;
        pid_tab = Shuffleboard.getTab("PID Tuner");
        p_entry = pid_tab.add("Tune P", 1)
                                    .withSize(4, 1)
                                    .withPosition(0, 0)
                                    .withWidget(BuiltInWidgets.kNumberSlider)
                                    .getEntry();
        i_entry = pid_tab.add("Tune P", 1)
                                    .withSize(4, 1)
                                    .withPosition(0, 1)
                                    .withWidget(BuiltInWidgets.kNumberSlider)
                                    .getEntry();
        d_entry = pid_tab.add("Tune P", 1)
                                    .withSize(4, 1)
                                    .withPosition(0, 2)
                                    .withWidget(BuiltInWidgets.kNumberSlider)
                                    .getEntry();
    }

    public double getPID(double error) {
        double pid = (getP(error)+getI(error)+getD(error))*m_k;
        last_time = m_timer.get();
        last_error = error;
        if (pid_tab != null) {
            updateWithNetworkTables();
        }
        return pid;
    }

    public double getP(double error) {
        return m_p*error;
    }

    public double getI(double error) {
        i_buildup += (m_timer.get()-last_time)*error;
        return m_i*i_buildup;
    }

    public double getD(double error) {
        return m_d*((error-last_error)/(m_timer.get()-last_time));
    }

    public void updateWithNetworkTables() {
        m_p = p_entry.getDouble(m_p);
        m_i = i_entry.getDouble(m_i);
        m_d = d_entry.getDouble(m_d);
    }

}