package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.model.LocationStates;


public interface CoronaRepo extends CrudRepository<LocationStates, Integer>{
	
	
	@Query(value = "select * from LOCATION_STATES where country=?", nativeQuery = true)
	public List<LocationStates> findByCountry(String country);

	@Query(value = "SELECT * FROM LOCATION_STATES order by LATEST_TOTAL_DEATHS DESC limit ?", nativeQuery = true)
	public List<LocationStates> findByLatestTotalDeaths(int latest_total_deaths);
	

}
