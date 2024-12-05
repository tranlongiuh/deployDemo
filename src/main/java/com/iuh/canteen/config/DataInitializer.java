package com.iuh.canteen.config;

import com.iuh.canteen.entity.Category;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.repository.CategoryRepository;
import com.iuh.canteen.repository.ScheduleRepository;
import com.iuh.canteen.repository.StallRepository;
import com.iuh.canteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * DataInitializer Khởi tạo dữ liệu ban đầu của hệ thống
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StallRepository StallRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void run(String... args) throws Exception {

        if (categoryRepository.findAll()
                              .isEmpty()) {
            Category drinks = new Category();
            drinks.setName("Đồ uống");
            Category snacks = new Category();
            snacks.setName("Thức ăn vặt");
            Category meals = new Category();
            meals.setName("Món chính");
            categoryRepository.saveAll(Arrays.asList(drinks, snacks, meals));
            System.out.println("Initialized categories.");
        }
        if (userRepository.findAll()
                          .isEmpty()) {
            User cashier = new User();
            cashier.setUsername("cashier");
            cashier.setPassword(new BCryptPasswordEncoder().encode("12345678"));
            cashier.setEmail("emailcashier");
            cashier.setPhone("phonecashier");
            cashier.setRoles("ROLE_CASHIER");
            cashier.setBalance(new BigDecimal("0.0"));
            cashier.setEnabled(true);
//            cashier = userRepository.save(cashier);
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(new BCryptPasswordEncoder().encode("12345678"));
            admin.setEmail("emailadmin");
            admin.setPhone("phoneadmin");
            admin.setRoles("ROLE_ADMIN");
            admin.setBalance(new BigDecimal("0.0"));
            admin.setEnabled(true);
            List<User> users = Arrays.asList(admin, cashier);
            userRepository.saveAll(users);
            System.out.println("Initialized Account.");
        }
    }
}
