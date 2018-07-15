package com.seraco.simulator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FactoryManagedFrame;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.*;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.EcksteinHechlerPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.TimeStampedPVCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@EnableScheduling
@Controller
public class OrbitScheduler {

    @Autowired
    private SimpMessagingTemplate template;

    private static final Logger log = LoggerFactory.getLogger(OrbitScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private int cpt = 1;

    private AbsoluteDate currentDate;

    private final SatelliteRepository repository;

    private OneAxisEllipsoid earth;

    private final Frame inertialFrame;

    @Autowired
    OrbitScheduler(SatelliteRepository repository) {

        this.repository = repository;
        this.inertialFrame = FramesFactory.getEME2000();

        try {

            File home       = new File(System.getProperty("user.home"));
            File orekitData = new File(home, "orekit-data");

            if (!orekitData.exists()) {

                System.err.format(Locale.US, "Failed to find %s folder%n", orekitData.getAbsolutePath());
                System.err.format(
                        Locale.US,
                        "You need to download %s from the %s page and unzip it in %s for this tutorial to work%n",
                        "orekit-data.zip", "https://www.orekit.org/forge/projects/orekit/files",
                        home.getAbsolutePath()
                );
                System.exit(1);

            }

            DataProvidersManager manager = DataProvidersManager.getInstance();
            manager.addProvider(new DirectoryCrawler(orekitData));

            TimeScale utc = TimeScalesFactory.getUTC();
            currentDate = new AbsoluteDate(2004, 01, 01, 23, 30, 00.000, utc);

            FactoryManagedFrame ITRF = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
            this.earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                                              Constants.WGS84_EARTH_FLATTENING,
                                              ITRF);

        } catch (OrekitException oe) {

            System.err.println(oe.getMessage());

        }

    }

    public EcksteinHechlerPropagator orbitToPropagator(Satellite satellite) {

        try {

            double a = satellite.getA();
            double e = satellite.getE();
            double i = satellite.getI();
            double omega = satellite.getOmega();
            double raan = satellite.getRaan();
            double lm = satellite.getLm();

            double mu = Constants.WGS84_EARTH_MU;

            Orbit initialOrbit = new KeplerianOrbit(a, e, i, omega, raan, lm, PositionAngle.MEAN,
                    inertialFrame, currentDate, mu);

            EcksteinHechlerPropagator propag = new EcksteinHechlerPropagator(initialOrbit,
                                                                             Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                                                                             mu,
                                                                             Constants.WGS84_EARTH_C20,
                                                                             0.0,
                                                                             0.0,
                                                                             0.0,
                                                                             0.0);

            propag.setSlaveMode();

            return propag;

        } catch (OrekitException oe) {

            System.err.println(oe.getMessage());

            return null;

        }

    }

    public void propagateAndSave(EcksteinHechlerPropagator propag, Satellite satellite, double dt) {

        try {

            AbsoluteDate insideDate = currentDate.shiftedBy(dt);

            SpacecraftState currentState = propag.propagate(insideDate);
            Orbit orbit = currentState.getOrbit();

            OrbitType type = OrbitType.KEPLERIAN;
            KeplerianOrbit currentOrbit = (KeplerianOrbit) type.convertType(orbit);

            TimeStampedPVCoordinates coord = currentState.getPVCoordinates();
            Vector3D position = coord.getPosition();

            GeodeticPoint geoPoint = earth.transform(position, inertialFrame, insideDate);

            System.out.println(geoPoint.getLatitude());
            System.out.println(geoPoint.getLongitude());

            log.info(
                    "NowTime {}, Step {}, OrbitTime {}. Orbit {}",
                    dateFormat.format(new Date()),
                    cpt,
                    currentState.getDate(),
                    currentState.getOrbit()

            );

            satellite.setA(currentOrbit.getA());
            satellite.setE(currentOrbit.getE());
            satellite.setI(currentOrbit.getI());
            satellite.setOmega(currentOrbit.getPerigeeArgument());
            satellite.setRaan(currentOrbit.getRightAscensionOfAscendingNode());
            satellite.setLm(currentOrbit.getMeanAnomaly());

            repository.save(satellite);

        } catch (OrekitException oe) {

            System.err.println(oe.getMessage());

        }
    }

    @Scheduled(fixedRate = 5000)
    public void intervalExe() {

        double stepT = 10.0;

        Iterable<Satellite> satellites = repository.findAll();

        for (Satellite satellite : satellites) {

            EcksteinHechlerPropagator propag = orbitToPropagator(satellite);
            propagateAndSave(propag, satellite, stepT);

        }

        System.out.println();

        cpt++;
        currentDate = currentDate.shiftedBy(stepT);

        template.convertAndSend("/update/newParameters", "Update!");

    }

}
