package com.example.filedemo.controller;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.filedemo.controller.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long>{
    List<Customer> findByLastName(String lastName);
}
