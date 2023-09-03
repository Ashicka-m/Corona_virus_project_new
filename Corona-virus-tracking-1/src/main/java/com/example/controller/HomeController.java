package com.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import com.example.model.LocationStates;
import com.example.service.CoronaRepo;
import com.example.service.CoronaService;
import com.example.service.CoronaVirusDataServices;


@Controller
public class HomeController 
{
	
	private CoronaService crn;
	
	CoronaVirusDataServices crnService;
	
	private CoronaRepo rep;
	@Autowired
	public HomeController(CoronaService crn,CoronaRepo rep)
	{
		this.crn=crn;
		this.rep=rep;
		System.out.println("Front-controller created");
	}
	
	@Autowired
	public void setCrnService(CoronaVirusDataServices crnService) {
		this.crnService = crnService;
	}


	@RequestMapping("/")
	public String home(Model model)
	{
		List<LocationStates> allstates=crnService.getAllstates();
		int totalDeathsReported=allstates.stream().mapToInt(stat->stat.getLatestTotalDeaths()).sum();
		model.addAttribute("LocationStates",allstates);
		model.addAttribute("totalDeathsReported",totalDeathsReported);
		System.out.println("hommee");
		rep.saveAll(allstates);
		return "home";
		
	}
	@RequestMapping(value="/collectChartData",produces= {"application/json"})
	@ResponseBody
	public List<LocationStates> viewChart(Model model)
	{
		List<LocationStates> allstates=(List<LocationStates>) crn.findall();
		int totalDeathsReported=allstates.stream().mapToInt(stat->stat.getLatestTotalDeaths()).sum();
	
		model.addAttribute("LocationStates",allstates);
		model.addAttribute("totalDeathsReported",totalDeathsReported);
		return allstates;
	}
	
	@RequestMapping(value="/viewChart",method=RequestMethod.GET)
	public ModelAndView viewCharts()
	{
		
		return new ModelAndView("viewChart").addObject("myURL",new String("http://localhost:8081/collectChartData"));
	}
	
	
	@RequestMapping(value="/multiplelinechart",produces= {"application/json"})
	@ResponseBody
	public ResponseEntity<?> getDataForMultipleLine() {
		 List<LocationStates> allstates=crnService.getAllstates();
	//	List<MultipleData> dataList = multipleDataDAO.findAll();
		Map<String, List<LocationStates>> mappedData = new HashMap<>();
		for(LocationStates data : allstates) {
			
			if(mappedData.containsKey(data.getCountry())) {
				mappedData.get(data.getCountry()).add(data);
			}else {
				List<LocationStates> tempList = new ArrayList<LocationStates>();
				tempList.add(data);
				mappedData.put(data.getCountry(), tempList);
			}
			
		}
		return new ResponseEntity<>(mappedData, HttpStatus.OK);
	}
	
	@RequestMapping(value="/multiplelinecharts/{countryId}",produces= {"application/json"})
	@ResponseBody
	public Optional<LocationStates> getCountry(@PathVariable("countryId") int pid) {
		Optional<LocationStates> allstates=rep.findById(pid);	//	List<MultipleData> dataList = multipleDataDAO.findAll();
	
		
		return allstates;
	}
	
	@RequestMapping(value="/multiplelinecharts/country={country}",produces= {"application/json"})
	@ResponseBody
	public ResponseEntity<?> getCountry(@PathVariable("country") String name) {
		List<LocationStates> allstates=rep.findByCountry(name);	//	List<MultipleData> dataList = multipleDataDAO.findAll();
		Map<String, List<LocationStates>> mappedData = new HashMap<>();
		for(LocationStates data : allstates) {
			
			if(mappedData.containsKey(data.getCountry())) {
				mappedData.get(data.getCountry()).add(data);
			}else {
				List<LocationStates> tempList = new ArrayList<LocationStates>();
				tempList.add(data);
				mappedData.put(data.getCountry(), tempList);
			}
			
		}
		return new ResponseEntity<>(mappedData, HttpStatus.OK);
	}

	@RequestMapping(value="/multiplelinechart/TOP={count}",produces= {"application/json"})
	@ResponseBody
	public List<LocationStates> getLatestDeaths(@PathVariable("count") int count) {
		List<LocationStates> allstates=rep.findByLatestTotalDeaths(count);//	List<MultipleData> dataList = multipleDataDAO.findAll();
		
		return allstates;
	}
	
		
    }



