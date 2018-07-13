package com.seraco.simulator;

import org.springframework.data.repository.CrudRepository;

// tag::code[]
public interface SatelliteRepository extends CrudRepository<Satellite, Long> {

}
// end::code[]
