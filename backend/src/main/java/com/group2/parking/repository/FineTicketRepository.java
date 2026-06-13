package com.group2.parking.repository;

import com.group2.parking.entity.FineTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineTicketRepository extends JpaRepository<FineTicket, Long> {}


