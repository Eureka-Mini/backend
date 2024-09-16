package com.dangun.miniproject.member.repository;


import com.dangun.miniproject.member.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    void deleteByMemberId(Long memberId);
}