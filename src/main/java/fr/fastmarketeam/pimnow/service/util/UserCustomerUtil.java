package fr.fastmarketeam.pimnow.service.util;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.User;
import fr.fastmarketeam.pimnow.domain.UserExtra;
import fr.fastmarketeam.pimnow.repository.UserExtraRepository;
import fr.fastmarketeam.pimnow.repository.UserRepository;
import fr.fastmarketeam.pimnow.service.errors.CustomerRequiredActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserCustomerUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExtraRepository userExtraRepository;

    public Customer findCustomer(Optional<String> currentUserLogin) {
        Optional<User> user = findUser(currentUserLogin);
        Optional<UserExtra> userExtra = user.flatMap(u -> userExtraRepository.findById(u.getId()));
        if(userExtra.isPresent() && userExtra.get().getCustomer()!=null) {
            return userExtra.get().getCustomer();
        }
        throw new CustomerRequiredActionException();
    }

    public Optional<User> findUser(Optional<String> currentUserLogin) {
        return currentUserLogin.flatMap(login -> userRepository.findOneByLogin(login));
    }
}
