package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.CustomerMapper;
import cn.shinefan.fooerp.model.Customer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    @Transactional
    public Customer save(Customer customer) {
        customerMapper.insert(customer);
        return customer;
    }

    @Override
    @Transactional
    public Customer update(Customer customer) {
        customerMapper.updateById(customer);
        return customer;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        customerMapper.deleteById(id);
    }

    @Override
    public Customer findById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public Customer findByEmail(String email) {
        return customerMapper.findByEmail(email);
    }

    @Override
    public List<Customer> findAll() {
        return customerMapper.selectList(null);
    }

    @Override
    public List<Customer> findByStatus(String status) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        return customerMapper.selectList(queryWrapper);
    }
}
