package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer save(Customer customer);
    Customer update(Customer customer);
    void delete(Long id);
    Customer findById(Long id);
    Customer findByEmail(String email);
    List<Customer> findAll();
    List<Customer> findByStatus(String status);
}
