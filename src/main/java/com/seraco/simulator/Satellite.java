package com.seraco.simulator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@Entity
public class Satellite {

	private @Id @GeneratedValue Long id;
	private double a;
	private double e;
	private double i;
	private double omega;
	private double raan;
	private double lm;
	private @Version @JsonIgnore Long version;

	private Satellite() {}

	public Satellite(double semimaj, double eccen, double incli, double periarg, double rasc, double meanom) {
		this.a = semimaj;
		this.e = eccen;
		this.i = incli;
		this.omega = periarg;
		this.raan = rasc;
		this.lm = meanom;
	}

    public Long getId() {
        return id;
    }

    public double getA() {
        return a;
    }

    public double getE() {
        return e;
    }

    public double getI() {
        return i;
    }

    public double getOmega() {
        return omega;
    }

    public double getRaan() {
        return raan;
    }

    public double getLm() {
        return lm;
    }

    public void setA(double semimaj) {
	    this.a = semimaj;
    }

    public void setE(double eccen) {
        this.e = eccen;
    }

    public void setI(double incli) {
        this.i = incli;
    }

    public void setOmega(double periarg) {
        this.omega = periarg;
    }

    public void setRaan(double rasc) {
        this.raan = rasc;
    }

    public void setLm(double meananom) {
        this.lm = meananom;
    }

}