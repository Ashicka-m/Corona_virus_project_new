package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.MovieDetails;


@Repository
public interface MovieRepo extends JpaRepository<MovieDetails, Long> {
	
	
}
