package com.seraco.simulator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

// tag::code[]
@Data
@Entity
public class Satellite {

	private @Id @GeneratedValue Long id;
	private double a; // semi major axis in meters
	private double e; // eccentricity
	private double i; // inclination
	private double omega; // perigee argument
	private double raan; // right ascension of ascending node
	private double lM; // mean anomaly

	private Satellite() {}

	public Satellite(double semimaj, double eccen, double incli, double periarg, double rasc, double meanom) {
		this.a = semimaj;
		this.e = eccen;
		this.i = incli;
		this.omega = periarg;
		this.raan = rasc;
		this.lM = meanom;
	}
}
// end::code[]