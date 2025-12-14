package com.nikhil.project.uber.uberApp.exceptions;

import com.nikhil.project.uber.uberApp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
