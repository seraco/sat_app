package com.seraco.simulator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.hipparchus.util.FastMath;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrbitScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrbitScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private int cpt = 1;

    private AbsoluteDate currentDate;

    private KeplerianPropagator kepler;

    OrbitScheduler() {

        try {

            // configure Orekit
            File home       = new File(System.getProperty("user.home"));
            File orekitData = new File(home, "orekit-data");
            if (!orekitData.exists()) {
                System.err.format(Locale.US, "Failed to find %s folder%n",
                        orekitData.getAbsolutePath());
                System.err.format(Locale.US, "You need to download %s from the %s page and unzip it in %s for this tutorial to work%n",
                        "orekit-data.zip", "https://www.orekit.org/forge/projects/orekit/files",
                        home.getAbsolutePath());
                System.exit(1);
            }
            DataProvidersManager manager = DataProvidersManager.getInstance();
            manager.addProvider(new DirectoryCrawler(orekitData));

            // Initial date in UTC time scale
            TimeScale utc = TimeScalesFactory.getUTC();
            currentDate = new AbsoluteDate(2004, 01, 01, 23, 30, 00.000, utc);

            // Initial orbit parameters
            double a = 24396159; // semi major axis in meters
            double e = 0.72831215; // eccentricity
            double i = FastMath.toRadians(7); // inclination
            double omega = FastMath.toRadians(180); // perigee argument
            double raan = FastMath.toRadians(261); // right ascension of ascending node
            double lM = 0; // mean anomaly

            // gravitation coefficient
            double mu =  3.986004415e+14;


            // Inertial frame
            Frame inertialFrame = FramesFactory.getEME2000();

            // Orbit construction as Keplerian
            Orbit initialOrbit = new KeplerianOrbit(a, e, i, omega, raan, lM, PositionAngle.MEAN,
                    inertialFrame, currentDate, mu);

            // Simple extrapolation with Keplerian motion
            kepler = new KeplerianPropagator(initialOrbit);

            // Set the propagator to slave mode (could be omitted as it is the default mode)
            kepler.setSlaveMode();

        } catch (OrekitException oe) {

            System.err.println(oe.getMessage());

        }

    }

    @Scheduled(fixedRate = 5000)
    public void orbitStep() {

        try {

            // Step duration in seconds
            double stepT = 60.;

            SpacecraftState currentState = kepler.propagate(currentDate);
            
            // System.out.println("step " + cpt++);
            // System.out.println(" time : " + currentState.getDate());
            // System.out.println(" " + currentState.getOrbit());

            log.info(
                    "NowTime {}, Step {}, OrbitTime {}. Orbit {}",
                    dateFormat.format(new Date()),
                    cpt++,
                    currentState.getDate(),
                    currentState.getOrbit()
            );

            currentDate = currentDate.shiftedBy(stepT);

        } catch (OrekitException oe) {

            System.err.println(oe.getMessage());

        }

    }

}
