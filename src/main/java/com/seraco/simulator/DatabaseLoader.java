package com.seraco.simulator;

import org.hipparchus.util.FastMath;
import org.orekit.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final SatelliteRepository repository;

	@Autowired
	public DatabaseLoader(SatelliteRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... strings) throws Exception {

	    double ae = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

        double ra = 800.0 * 1000.0;
        double rp = 700.0 * 1000.0;

        double a = (rp + ra + 2 * ae) / 2.0;
        double e = 1.0 - (rp + ae) / a;

		this.repository.save(new Satellite(
				a,
                e,
				FastMath.toRadians(98.55),
				FastMath.toRadians(90.0),
				FastMath.toRadians(5.1917),
                FastMath.toRadians(359.93),
				"#d62d20"
        ));

        this.repository.save(new Satellite(
                a,
                e,
                FastMath.toRadians(9.0),
                FastMath.toRadians(145.0),
                FastMath.toRadians(230.0),
                FastMath.toRadians(10.0),
				"#008744"
        ));

        this.repository.save(new Satellite(
                a,
                e,
                FastMath.toRadians(30.0),
                FastMath.toRadians(55.0),
                FastMath.toRadians(10.0),
                FastMath.toRadians(100.0),
				"#ffa700"
        ));

        this.repository.save(new Satellite(
                a,
                e,
                FastMath.toRadians(75.0),
                FastMath.toRadians(140.0),
                FastMath.toRadians(2.0),
                FastMath.toRadians(233.0),
                "#0057e7"
        ));
	}
}