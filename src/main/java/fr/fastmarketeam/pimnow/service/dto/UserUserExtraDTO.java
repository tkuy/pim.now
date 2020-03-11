package fr.fastmarketeam.pimnow.service.dto;

import fr.fastmarketeam.pimnow.domain.Customer;

public class UserUserExtraDTO {

    private String phone;
    private UserDTO user;
    private Customer customer;

    public UserUserExtraDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserUserExtraDTO(String phone, UserDTO user, Customer customer) {
        this.phone = phone;
        this.user = user;
        this.customer = customer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "UserUserExtraDTO{" +
            "phone='" + phone + '\'' +
            ", userDTO=" + user +
            ", customer=" + customer +
            '}';
    }
}
