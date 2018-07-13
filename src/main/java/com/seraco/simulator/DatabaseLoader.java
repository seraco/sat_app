package com.seraco.simulator;

import org.hipparchus.util.FastMath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// tag::code[]
@Component
public class DatabaseLoader implements CommandLineRunner {

	private final SatelliteRepository repository;

	@Autowired
	public DatabaseLoader(SatelliteRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... strings) throws Exception {
		this.repository.save(new Satellite(
				24396159,
				0.72831215,
				FastMath.toRadians(7),
				FastMath.toRadians(180),
				FastMath.toRadians(261),
				0
        ));

        this.repository.save(new Satellite(
                33432159,
                0.88831215,
                FastMath.toRadians(9),
                FastMath.toRadians(145),
                FastMath.toRadians(230),
                0
        ));
	}
}
// end::code[]