package com.example.dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.HistoryRepo;
import com.example.demo.MovieRepo;
import com.example.demo.RegisterRepo;
import com.example.demo.SeatRepo;
import com.example.model.CurrentDateOperation;
import com.example.model.MovieDetails;
import com.example.model.OrderHistory;

import com.example.model.Register;
import com.example.model.Seat;



@Component
public class RegisterDao {
	
	@Autowired
	private RegisterRepo repo;
	
	@Autowired
	private SeatRepo repo1;
	
	
	
	@Autowired
	private HistoryRepo repo2;
	
	@Autowired
	private MovieRepo movieRepo;
	
	
	public int save(Register register) {
		
		repo.save(register);
		return 1;
		
	}
	public int saveSeat(Seat seat, Register register, Date date, String time){
		List<Seat> list = new ArrayList<Seat>();
		list.add(seat);
		register.setSeat(list);
		CurrentDateOperation cdo= new CurrentDateOperation();
		cdo.setShowDate(date);
		cdo.setShowTime(time);
		cdo.setSeat(list);
		
		seat.setOperation(cdo);
		seat.setOperation(cdo);
		seat.setRegister(register);
		Seat save = repo1.save(seat);
		return 1;
	}
	public Register login(String email, String password) {
		Register register = repo.findByEmailAndPassword(email, password);
		return register;
	}
	public List<MovieDetails> getAllMovie(){
		List<MovieDetails> list = this.movieRepo.findAll();
		
		return list;
	}
	public List<Seat> getAllSeat(LocalDate date, String time){
		List<Seat> list = repo1.getAllByDate(date, time);
		return list;
	}
	public List<Register> getAll(){
		List<Register> findAll = repo.findAll();
		return findAll;
	}
	public OrderHistory saveHistory(OrderHistory history, Register register) {
		register.setHistory(history);
		OrderHistory save = repo2.save(history);
		return save;
	}
	
	public List<OrderHistory> getAllHistory(long id){
		List<OrderHistory> list = repo2.getAllHistory(id);		
		return list;
	}
	
	  
	  public void saveMove(MultipartFile image,String movie_details,String movie_name)
	  {
		  MovieDetails m=new MovieDetails();
		  String fileName=image.getOriginalFilename();
		  if(fileName.contains(".."))
		  {
			  System.out.println("not a valid file");
		  }
		  try {
			m.setImage(Base64.getEncoder().encodeToString(image.getBytes()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  m.setMovieDetails(movie_details);
		  m.setMovieName(movie_name);
		  movieRepo.save(m);
	  }
	  
	  
	  public void movieDetailsUpdate(MovieDetails md)
		{
			System.out.println(md.getMovieId()+"\t"+md.getMovieDetails()+"\t"+md.getImage()+"\t"+md.getMovieName());
			 MovieDetails m=movieRepo.findById(md.getMovieId()).get();
			 m.setMovieId(m.getMovieId());
			 System.out.println(m.getMovieId()+"55555");
			 m.setMovieDetails(m.getMovieDetails());
			 m.setImage(m.getImage());
			 m.setMovieName(m.getMovieName());
			 movieRepo.save(m);
		
			
		}
	  
	  public void saveMoveDet(MovieDetails movedetails)
	  {
		  this.movieRepo.save(movedetails);
	  }
	  
	  public void updateMovieDetails(MultipartFile image,String movie_details,String movie_name,Long movie_id)
	  {
		  MovieDetails m=new MovieDetails();
		  String fileName=image.getOriginalFilename();
		 
		//  MovieDetails mov=movieRepo.findById(moviedetails.getMovieId()).get();
		  if(fileName.contains(".."))
		  {
			  System.out.println("not a valid file");
		  }
		  try {
			 m.setImage((Base64.getEncoder().encodeToString(image.getBytes())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 m.setMovieId(movie_id);
		 // mov.setImage(moviedetails.getImage());
		  m.setMovieDetails(movie_details);
		  m.setMovieName(movie_name);
		  movieRepo.save(m);
		 
		  
	  }
	
	  
	  
	 
	  }
	 


