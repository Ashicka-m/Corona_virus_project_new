package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Register;
@Repository
public interface RegisterRepo extends JpaRepository<Register, Long> {
	
	public Register findByEmailAndPassword(String email, String password);


}
