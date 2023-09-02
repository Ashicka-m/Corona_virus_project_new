package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.model.OrderHistory;

@Repository
public interface HistoryRepo extends JpaRepository<OrderHistory, Long> {
	
	@Query(value = "select * from history where Register_bid=? ORDER BY h_id DESC", nativeQuery = true)
	public List<OrderHistory> getAllHistory(long id);

}