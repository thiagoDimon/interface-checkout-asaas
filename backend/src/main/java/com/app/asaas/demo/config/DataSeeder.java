package com.app.asaas.demo.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.asaas.demo.customers.entity.CustomerEntity;
import com.app.asaas.demo.customers.repository.CustomerRepository;
import com.app.asaas.demo.product.entity.ProductEntity;
import com.app.asaas.demo.product.repository.ProductRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DataSeeder(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.saveAll(getInitialProducts());
        }

        if (customerRepository.count() == 0) {
            customerRepository.save(getInitialCustomer());
        }
    }

    private List<ProductEntity> getInitialProducts() {
        ProductEntity tshirt = new ProductEntity();
        tshirt.setName("Camiseta Masculina Casual Premium Fit");
        tshirt.setDescription("Modelo de corte ajustado, mangas longas raglan em cor contrastante.");
        tshirt.setPrice(new BigDecimal("89.90"));
        tshirt.setImage("https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_t.png");

        ProductEntity jacket = new ProductEntity();
        jacket.setName("Jaqueta Masculina");
        jacket.setDescription("Ótima jaqueta para primavera, outono e inverno.");
        jacket.setPrice(new BigDecimal("129.90"));
        jacket.setImage("https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_t.png");

        ProductEntity backpack = new ProductEntity();
        backpack.setName("Mochilla Foldsack");
        backpack.setDescription("Mochila perfeita para o dia a dia.");
        backpack.setPrice(new BigDecimal("99.90"));
        backpack.setImage("https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_t.png");

        return List.of(tshirt, jacket, backpack);
    }

    private CustomerEntity getInitialCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setName("João Machado Silva");
        customer.setEmail("joao.silva@email.com");
        customer.setCpf("24971563792");
        return customer;
    }

}
