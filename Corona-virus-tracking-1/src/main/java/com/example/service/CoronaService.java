package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.model.LocationStates;





@Service
public class CoronaService {
	
private CoronaRepo coronarepo;
	
	public CoronaService(CoronaRepo coronarepo)
	{
		System.out.println("Product service created");
		this.coronarepo=coronarepo;
	}
	
	public void saveProduct(List<LocationStates> p)
	{
		System.out.println("Product savexxx");
		coronarepo.saveAll(p);
		
	}
	public List<LocationStates> findProduct(int pid)
	{
		
		Object find_product=coronarepo.findById(pid);
		return (List<LocationStates>) find_product;
	}
	
	public List<LocationStates> findall()
	{
		List<LocationStates> allst=(List<LocationStates>) coronarepo.findAll();
	
		return allst ;
	}
	
	public LocationStates findBycountry(String country)
	{
		List<LocationStates> findBycountry=coronarepo.findByCountry(country);
	
		return (LocationStates) findBycountry;
	}

}
