package cn.shinefan.fooerp.web;

import cn.shinefan.fooerp.model.Customer;
import cn.shinefan.fooerp.service.CustomerService;
import cn.shinefan.fooerp.web.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.findAll();
        List<CustomerDto> customerDtos = customers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        model.addAttribute("customers", customerDtos);
        return "customers";
    }

    @GetMapping("/new")
    public String newCustomerForm(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "customer-form";
    }

    @PostMapping
    public String createCustomer(@ModelAttribute CustomerDto customerDto) {
        Customer customer = toEntity(customerDto);
        customer.setStatus("ACTIVE");
        customerService.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/{id}/edit")
    public String editCustomerForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            return "redirect:/customers";
        }
        model.addAttribute("customer", toDto(customer));
        return "customer-form";
    }

    @PostMapping("/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute CustomerDto customerDto) {
        Customer customer = toEntity(customerDto);
        customer.setId(id);
        customerService.update(customer);
        return "redirect:/customers";
    }

    @GetMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return "redirect:/customers";
    }

    private CustomerDto toDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCompany(),
                customer.getStatus()
        );
    }

    private Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setCompany(dto.getCompany());
        customer.setStatus(dto.getStatus());
        return customer;
    }
}
