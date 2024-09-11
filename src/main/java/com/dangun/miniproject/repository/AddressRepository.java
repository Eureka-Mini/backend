package com.dangun.miniproject.repository;


import com.dangun.miniproject.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
