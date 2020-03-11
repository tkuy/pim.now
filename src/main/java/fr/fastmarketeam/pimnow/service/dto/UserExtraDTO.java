package fr.fastmarketeam.pimnow.service.dto;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.UserExtra;

import java.util.Objects;

/**
 * A DTO representing a user and his extra information.
 */
public class UserExtraDTO {

    private Long id;

    private String phone;

    private UserDTO user;

    private Customer customer;

    public UserExtraDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserExtraDTO(UserExtra userExtra) {
        this.id = userExtra.getId();
        this.phone = userExtra.getPhone();
        this.customer = userExtra.getCustomer();
        this.user = new UserDTO(userExtra.getUser());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserExtraDTO that = (UserExtraDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(user, that.user) &&
            Objects.equals(customer, that.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, user, customer);
    }

    @Override
    public String toString() {
        return "UserExtraDTO{" +
            "id=" + id +
            ", phone='" + phone + '\'' +
            ", user=" + user +
            ", customer=" + customer +
            '}';
    }
}
