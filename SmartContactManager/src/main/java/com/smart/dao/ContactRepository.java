package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.model.Contact;
import com.smart.model.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// Current page,
	// Contact Per page
	@Query("select c from Contact c where c.user.id=:userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
	
	
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
	
}
