package osipov.evgeny.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import osipov.evgeny.entity.Customer;
import osipov.evgeny.entity.Freelancer;
import osipov.evgeny.entity.Order;
import osipov.evgeny.repository.CustomerRepo;
import osipov.evgeny.repository.FreelanceRepo;
import osipov.evgeny.repository.OrderRepo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserAndOrderController {

    public Long getIdFromCookieOrReturnMinusOne(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        long userId = -1L;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("userId")) {
                userId = Long.parseLong(cookie.getValue());
            }
        }
        return userId;
    }

    public String getRoleFromCookieOrReturnEmptyString(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String role = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("role")) {
                role = cookie.getValue();
            }
        }
        System.out.println(role);
        return role;
    }

    @GetMapping("/registration/check/")
    @ResponseBody
    public String registration(Model model, HttpServletResponse response, @RequestParam String fio,
                               @RequestParam String username, @RequestParam String password,
                               @RequestParam String description, @RequestParam String phone,
                               @RequestParam String email, @RequestParam String role) {
        if (role.equals("freelancer"))
            if (FreelanceRepo.getFreelancerByUsernameOrEmptyEntity(username).getId() == null) {
                FreelanceRepo.setFreelancer(new Freelancer(fio, username, password, 0L, description, phone, email));
            } else return "registration";
        else if (role.equals("customer"))
            if (CustomerRepo.getCustomerByCustomerNameOrEmptyEntity(username).getId() == null) {
                CustomerRepo.setCustomer(new Customer(fio, username, password, 0L, description, phone, email));
            } else return "registration";
        return "/";
    }

    @PostMapping("/auth_check/")
    @ResponseBody
    public String authorization(Model model, HttpServletRequest request, HttpServletResponse response,
                                @RequestParam String username, @RequestParam String password, @RequestParam String role) {
        if (role.equals("freelancer"))
            if (FreelanceRepo.getFreelancerByUsernameOrEmptyEntity(username).getPassword() != null) {
                if (FreelanceRepo.getFreelancerByUsernameOrEmptyEntity(username).getPassword().equals(password)) {
                    Cookie cookieId = new Cookie("userId", FreelanceRepo.getFreelancerByUsernameOrEmptyEntity(username).getId().toString());
                    cookieId.setPath("/");
                    response.addCookie(cookieId);
                    Cookie cookieRole = new Cookie("role", role);
                    cookieRole.setPath("/");
                    response.addCookie(cookieRole);
                } else return "/";
            } else return "/";
        else if (role.equals("customer"))
            if (CustomerRepo.getCustomerByCustomerNameOrEmptyEntity(username).getPassword() != null) {
                if (CustomerRepo.getCustomerByCustomerNameOrEmptyEntity(username).getPassword().equals(password)) {
                    Cookie cookieId = new Cookie("userId", CustomerRepo.getCustomerByCustomerNameOrEmptyEntity(username).getId().toString());
                    cookieId.setPath("/");
                    response.addCookie(cookieId);
                    Cookie cookieRole = new Cookie("role", role);
                    cookieRole.setPath("/");
                    response.addCookie(cookieRole);
                } else return "/";
            } else return "/";
        return "/home";
    }

    @GetMapping("/order/create/creating")
    @ResponseBody
    public String registration(Model model, HttpServletResponse response, @RequestParam String category,
                               @RequestParam String name, @RequestParam String description,
                               @RequestParam String time, @RequestParam String price,
                               @RequestParam Long customer_id) {
        if (CustomerRepo.getCustomerByIdOrEmptyEntity(customer_id) == null | customer_id == -1) {
            return "/";
        }
        OrderRepo.setOrder(new Order(CustomerRepo.getCustomerByIdOrEmptyEntity(customer_id), "published",
                                     category, name, description, time, price));
        return "/order/active";
    }

    @PostMapping("/get_role")
    public String getRole(Model model, HttpServletRequest request) {
        return getRoleFromCookieOrReturnEmptyString(request);
    }

    @PostMapping("/get_id")
    public Long getId(Model model, HttpServletRequest request) {
        return getIdFromCookieOrReturnMinusOne(request);
    }

    @GetMapping("/profile/{id}/get_profile_info")
    public String getProfileInfoByIdOrEmptyString(Model model, HttpServletResponse response, @PathVariable Long id) {
        if (FreelanceRepo.getFreelancerByIdOrEmptyEntity(id) != null) {
            Freelancer freelancer = FreelanceRepo.getFreelancerByIdOrEmptyEntity(id);
            System.out.println(freelancer.toJSON());
            return freelancer.toJSON();
        } else if (CustomerRepo.getCustomerByIdOrEmptyEntity(id) != null) {
            Customer customer = CustomerRepo.getCustomerByIdOrEmptyEntity(id);
            return customer.toJSON();
        }
        return "";
    }
}
