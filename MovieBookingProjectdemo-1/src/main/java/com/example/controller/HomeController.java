package com.example.controller;

import java.io.FileInputStream;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.dao.RegisterDao;
import com.example.demo.MailService;
import com.example.demo.MovieRepo;
import com.example.model.MovieDetails;
import com.example.model.OrderHistory;
import com.example.model.Register;
import com.example.model.Seat;




@Controller
public class HomeController {

	@Autowired
	private RegisterDao dao;

	@Autowired
	private MovieRepo movieRepo;
	
	@Autowired
	private MailService mser;
	

	
	private final Logger log=LoggerFactory.getLogger(this.getClass());
	@GetMapping("/")
	public String home(Model m, HttpSession session) {

		String movie = (String) session.getAttribute("movieName");
		System.out.println(movie + "========Index");
		List<MovieDetails> movie2 = dao.getAllMovie();
		m.addAttribute("movieList", movie2);
		m.addAttribute("menu", "home");

		return "index";
	}
	@GetMapping("/register")
	public String register(Model m) {

		m.addAttribute("menu", "Register");
		return "Register";
	}
	@PostMapping("/save")
	public String save(@ModelAttribute("register") Register register) {
		dao.save(register);
		return "redirect:/register";

	}
	
	@GetMapping("/loginForm")
	public String loginForm(Model m) {
		m.addAttribute("menu", "login");
		return "login";
	}
	@GetMapping("/adminaddpage")
	public String adminaddpage(Model m) {
		MovieDetails movedet=new MovieDetails();
		m.addAttribute("addmov", movedet);
		return "adminaddpage";
	}
	@PostMapping("/processing")
	public String login(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session, Model m) {

		Register object = (Register) session.getAttribute("user");
		//Register reg = dao.login(email, password);
		 
		if(email.equals("admin@gamil.com")&&password.equals("1234"))
		{
			//m.addAttribute("menu", "allmovie");

			return "adminaddpage";
			
		}
		if (object != null) {
			return "redirect:/booking-seat";
		} else {

			Register register = dao.login(email, password);

			if (register == null) {
				m.addAttribute("failed", "Invalied login");
				return "login";
			} else {
				session.setAttribute("user", register);
			}
			return "redirect:/home";
		}
	}
	@GetMapping("/home")
	public String mainDashboard(HttpSession session, Model m) {
		session.removeAttribute("bookingdate");
		session.removeAttribute("bookingtime");
		session.removeAttribute("movieName");
		m.addAttribute("menu", "home");

		String message = (String) session.getAttribute("msg");
		m.addAttribute("message", message);
		session.removeAttribute("msg");
//		System.out.println(message);
		List<MovieDetails> movie2 = dao.getAllMovie();
		m.addAttribute("listMovie", movie2);

		return "main-dashboard";
	}
	
	
	@GetMapping("/booking")
	public String bookingCheck(@RequestParam("movieName") String movieName, Model m, HttpSession session) {
		System.out.println("11111111");
		List<MovieDetails> movie2 = dao.getAllMovie();
		List<String> checkMovie = new ArrayList<>();
		for (MovieDetails string : movie2) {
			checkMovie.add(string.getMovieName());
		}
		if (checkMovie.contains(movieName)) {
			session.setAttribute("movieName", movieName);
			System.out.println(movieName);
			LocalDate now = LocalDate.now();
			LocalDate monthLimit = LocalDate.now();
			String time = "09:00 am";
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			m.addAttribute("date", now);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("time", time);
			m.addAttribute("seats", seatNo1);
			return "bookinghome";

		} else {
			return "redirect:/";

		}

	}
	@GetMapping("/booking-seat")
	public String getUser(@RequestParam("movieName") String movieName, HttpSession session, Model m) {
		List<MovieDetails> movie2 = dao.getAllMovie();
		List<String> checkMovie = new ArrayList<>();
		for (MovieDetails string : movie2) {
			
			checkMovie.add(string.getMovieName());
		}
		
		if (checkMovie.contains(movieName)) {
			
			System.out.println("Bookin-seat");
			session.setAttribute("movieName", movieName);

			LocalDate now = LocalDate.now();
			LocalDate monthLimit = LocalDate.now();
			String time = "09:00 am";

			Register customer = (Register) session.getAttribute("user");
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> seat = customer.getSeat();

			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			m.addAttribute("date", now);
			m.addAttribute("time", time);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("seats", seatNo1);
			m.addAttribute("seat", seat);
			session.setAttribute("user", customer);
			return "dashboard";
		} else {
			return "redirect:/home";
		}

	}
	
	
	
	
	@PostMapping("/book-seat")
	public String bookSeat(@ModelAttribute("Seat") Seat seat, @RequestParam("movieName") String movieName,
			HttpSession session, Model m) {
		LocalDate currentDate = LocalDate.now();
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Date todayDate = Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant());
		LocalDate date = (LocalDate) session.getAttribute("bookingdate");
		String time = (String) session.getAttribute("bookingtime");
		System.out.println(movieName+"checkin++++++++"+seat.getSeatNo());
		System.out.println(seat.getSeatNo().equals(null) + " wooo" + movieName.equals(null));
		Register object = (Register) session.getAttribute("user");
      //   System.out.println(object+"reigeter-----");
		if (object == null) {
			return "redirect:/loginForm";
		} else if ((seat.getSeatNo().isEmpty()) && (movieName.equals(null))) {
			System.out.println("Seat is null");
			return "redirect:/home";
		} else if (date == null) {
			date = currentDate;
			time = "09:00 am";
			if (((date.isAfter(currentDate)) || (date.equals(currentDate)))
					&& (date.isBefore(currentDate.plusMonths(1)) || date.equals(currentDate.plusMonths(1)))) {

				Date date2 = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
				List<Double> price = new ArrayList<Double>();
				double sum = 0;
				double p = 525.22d;
				for (String s : seat.getSeatNo()) {
					sum = sum + p;
					price.add(p);
				}
				seat.setTotal(sum);
				seat.setPrice(price);
				//Register customer = (Register) session.getAttribute("user");
               
				OrderHistory history = new OrderHistory(seat.getSeatNo(), price, sum, movieName, todayDate, date2, time,object);
				dao.saveSeat(seat, object, date2, time);
				dao.saveHistory(history, object);
				List<String> seatNo1 = new ArrayList<String>();
				List<Register> all = dao.getAll();
				//String login=dao.login(login, login);
				//List<Seat> fullhistory =dao.getAllSeat(date, time);
				
				for (Register c : all) {
					for (Seat s : c.getSeat()) {
						
						for (String s1 : s.getSeatNo()) {
							seatNo1.add(s1);
						//	System.out.println(seatNo1+"---------");
						}

					}
				}
				System.out.println(movieName+"checking====");
				mser.sendEamil(object.getEmail(),"Booked your seat"," Name = "+object.getName() + " Movie Name = "+movieName);
			//	List<Seat> fullhistory =dao.getAllSeat(date, time);
				m.addAttribute("seats", seatNo1);
				session.setAttribute("user", object);
				session.setAttribute("msg", "your seat book successsfully");
				
				
				
				return "redirect:/home";

			} else {
				System.out.println("ye date current date se pahle ki date hai");
				return "redirect:/booking-seat?movieName=" + movieName;

			}
		} else {
			if (((date.isAfter(currentDate)) || (date.equals(currentDate)))
					&& (date.isBefore(currentDate.plusMonths(1)) || date.equals(currentDate.plusMonths(1)))) {
				Date date2 = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
				List<Double> price = new ArrayList<Double>();
				double sum = 0;
				double p = 525.22d;
				for (String s : seat.getSeatNo()) {
					sum = sum + p;
					price.add(p);
				}
				seat.setTotal(sum);
				seat.setPrice(price);

				OrderHistory history = new OrderHistory(seat.getSeatNo(), price, sum, movieName, todayDate, date2, time,
						object);
				dao.saveSeat(seat, object, date2, time);
				dao.saveHistory(history, object);
				List<String> seatNo1 = new ArrayList<String>();
				List<Register> all = dao.getAll();
				for (Register c : all) {
					for (Seat s : c.getSeat()) {
						for (String s1 : s.getSeatNo()) {
							seatNo1.add(s1);
						}

					}
				}

				m.addAttribute("seats", seatNo1);
				session.setAttribute("user", object);
				session.setAttribute("msg", "your seat book successsfully");
				mser.sendEamil(object.getEmail(),"Booked your seat"," Name = "+object.getName() + " Movie Name = "+ movieName);
				return "redirect:/home";

			} else {
				System.out.println("ye date current date se pahle ki date hai");
				return "redirect:/booking-seat?movieName=" + movieName;

			}
		}

	}
	
	@PostMapping("/check")
	public String checkDate(@RequestParam("localdate") String date, @RequestParam("localtime") String time, Model m,
			HttpSession session) {
		Register object = (Register) session.getAttribute("user");
		String movie = (String) session.getAttribute("movieName");
		LocalDate monthLimit = LocalDate.now();
		if (movie.equals(null)) {
			return "home";

		} else if (object == null) {
			LocalDate now = LocalDate.parse(date);
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			session.setAttribute("bookingdate", now);
			session.setAttribute("bookingtime", time);
			m.addAttribute("date", now);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("time", time);
			m.addAttribute("seats", seatNo1);

			return "home";
		} else {
			LocalDate now = LocalDate.parse(date);
			List<String> seatNo1 = new ArrayList<String>();
			List<Seat> all = dao.getAllSeat(now, time);

			for (Seat s : all) {
				for (String s1 : s.getSeatNo()) {
					seatNo1.add(s1);
				}

			}

			session.setAttribute("bookingdate", now);
			session.setAttribute("bookingtime", time);
			m.addAttribute("date", now);
			m.addAttribute("max", monthLimit.plusMonths(1));
			m.addAttribute("min", monthLimit);
			m.addAttribute("time", time);
			m.addAttribute("seats", seatNo1);

			return "dashboard";
		}

	}
	
	
	@GetMapping("/order-history")
	public String history(HttpSession session, Model m) {
		Date todayDate = new Date();
		Register object = (Register) session.getAttribute("user");
		session.setAttribute("user", object);
		List<OrderHistory> list = dao.getAllHistory(object.getBid());
		m.addAttribute("hList", list);
		m.addAttribute("todaydate", todayDate);

		LocalDate date = (LocalDate) session.getAttribute("bookingdate");
		System.out.println(date);
		m.addAttribute("menu", "order");
		return "history";
	}
	@GetMapping("/ViewAllMovies")
	public String ViewAllMovies(HttpSession session, Model m) {
		//Date todayDate = new Date();
		System.out.println("viewAllMovie------");
	//	MovieDetails object = (MovieDetails) session.getAttribute("user");
	//	session.setAttribute("user", object);
		List<MovieDetails> list = dao.getAllMovie();
		m.addAttribute("hList", list);
		m.addAttribute("menu", "allmovie");

	//	LocalDate date = (LocalDate) session.getAttribute("bookingdate");
	//	System.out.println(date);
		//m.addAttribute("menu", "order");
		return "viewAllMovie";
	}
	@PostMapping("/moviedetailssave")
	public String moviedetailssave(@RequestParam("imagFile") MultipartFile image,@RequestParam("mdetails") String movie_details,@RequestParam("mname") String movie_name,HttpServletRequest request) {
		//movieRepo.save(moviedetails);
		try {
			dao.saveMove(image, movie_details, movie_name);
		
			return "adminaddpage";
		}
		catch(Exception e)
		{
			return "index";
		}
		

	}
	

	@PostMapping("/mdsave")
	public String mdsave(@ModelAttribute("menu") MovieDetails moviedetails) {
		//movieRepo.save(moviedetails);
		try {
			dao.saveMoveDet(moviedetails);
		
			return "adminaddpage";
		}
		catch(Exception e)
		{
			return "index";
		}
		

	}
	@GetMapping("/editMovies/{movieId}")
	public String editMovie(@PathVariable("movieId") long movieId, Model m)
	{
		MovieDetails movdet = movieRepo.findById(movieId)
			      .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + movieId));
		//MovieDetails movdet=movieRepo.getById(movieId);
		System.out.println("edit controller");
		m.addAttribute("history",movdet);
		return "editMovie";
	}
	
	@PostMapping("/moviedetailsupdate")
	public String update(@RequestParam("imagFile") MultipartFile image,@RequestParam("mdetails") String movie_details,@RequestParam("mname") String movie_name,@RequestParam("mvid") Long movie_id,HttpServletRequest request) {
		//movieRepo.save(moviedetails);
		try {
		System.out.println("update controller");
		dao.updateMovieDetails(image, movie_details, movie_name, movie_id);
	return "editMovie";
		}
		catch(Exception e)
		{
			return "editMovie";
		}

	}
	@GetMapping("/deleteMovie/{movieId}")
	public String deleteMovie(@PathVariable("movieId") Long movieId) {
		MovieDetails movdet = movieRepo.findById(movieId)
			      .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + movieId));
		movieRepo.delete(movdet);
		return "redirect:/ViewAllMovies";
	}
	@GetMapping("/setting")
	public String getSetting(Model m, HttpSession session) {
		Register register = (Register) session.getAttribute("user");
		m.addAttribute("user", register);
		m.addAttribute("menu", "setting");
		return "setting";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");

		session.removeAttribute("bookingdate");
		session.removeAttribute("bookingtime");
		session.removeAttribute("movieName");

		return "redirect:/";
	}
	
	@GetMapping("/all-customers-records")
	public String allRecords(Model m, HttpSession session) {
		Register object = (Register) session.getAttribute("user");
		long bid = object.getBid();
		if (bid == 1) {
			List<Register> all = dao.getAll();
			m.addAttribute("records", all);
			m.addAttribute("menu", "allusers");
			return "user_records";
		} else {
			return "redirect:/booking-seat";
		}
	}

}
