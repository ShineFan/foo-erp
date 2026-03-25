package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.CustomerMapper;
import cn.shinefan.fooerp.model.Customer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Company");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("123-456-7890");
        testCustomer.setAddress("123 Test St");
        testCustomer.setCompany("Test Corp");
        testCustomer.setStatus("ACTIVE");
    }

    @Test
    void save_shouldInsertCustomerAndReturn() {
        // Arrange
        when(customerMapper.insert(any(Customer.class))).thenReturn(1);

        // Act
        Customer result = customerService.save(testCustomer);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer.getName(), result.getName());
        verify(customerMapper).insert(testCustomer);
    }

    @Test
    void update_shouldUpdateCustomerAndReturn() {
        // Arrange
        when(customerMapper.updateById(any(Customer.class))).thenReturn(1);

        // Act
        Customer result = customerService.update(testCustomer);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer.getName(), result.getName());
        verify(customerMapper).updateById(testCustomer);
    }

    @Test
    void delete_shouldDeleteCustomerById() {
        // Arrange
        when(customerMapper.deleteById(1L)).thenReturn(1);

        // Act
        customerService.delete(1L);

        // Assert
        verify(customerMapper).deleteById(1L);
    }

    @Test
    void findById_shouldReturnCustomerWhenExists() {
        // Arrange
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Company", result.getName());
        verify(customerMapper).selectById(1L);
    }

    @Test
    void findById_shouldReturnNullWhenNotExists() {
        // Arrange
        when(customerMapper.selectById(999L)).thenReturn(null);

        // Act
        Customer result = customerService.findById(999L);

        // Assert
        assertNull(result);
        verify(customerMapper).selectById(999L);
    }

    @Test
    void findByEmail_shouldReturnCustomerWhenExists() {
        // Arrange
        when(customerMapper.findByEmail("test@example.com")).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(customerMapper).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_shouldReturnNullWhenNotExists() {
        // Arrange
        when(customerMapper.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Act
        Customer result = customerService.findByEmail("nonexistent@example.com");

        // Assert
        assertNull(result);
        verify(customerMapper).findByEmail("nonexistent@example.com");
    }

    @Test
    void findAll_shouldReturnAllCustomers() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Another Company");
        customer2.setEmail("another@example.com");
        customer2.setStatus("ACTIVE");

        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerMapper.selectList(null)).thenReturn(customers);

        // Act
        List<Customer> result = customerService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerMapper).selectList(null);
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoCustomers() {
        // Arrange
        when(customerMapper.selectList(null)).thenReturn(Arrays.asList());

        // Act
        List<Customer> result = customerService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerMapper).selectList(null);
    }

    @Test
    void findByStatus_shouldReturnCustomersWithMatchingStatus() {
        // Arrange
        List<Customer> activeCustomers = Arrays.asList(testCustomer);
        when(customerMapper.selectList(any(QueryWrapper.class))).thenReturn(activeCustomers);

        // Act
        List<Customer> result = customerService.findByStatus("ACTIVE");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
        verify(customerMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    void findByStatus_shouldReturnEmptyListWhenNoMatchingStatus() {
        // Arrange
        when(customerMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());

        // Act
        List<Customer> result = customerService.findByStatus("INACTIVE");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerMapper).selectList(any(QueryWrapper.class));
    }
}
