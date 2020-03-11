package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.User;
import fr.fastmarketeam.pimnow.domain.UserExtra;
import fr.fastmarketeam.pimnow.repository.UserExtraRepository;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.service.UserExtraService;
import fr.fastmarketeam.pimnow.service.UserService;
import fr.fastmarketeam.pimnow.service.dto.UserDTO;
import fr.fastmarketeam.pimnow.service.dto.UserExtraDTO;
import fr.fastmarketeam.pimnow.service.dto.UserUserExtraDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserExtraServiceImpl implements UserExtraService {

    private UserService userService;

    private UserExtraRepository userExtraRepository;

    public UserExtraServiceImpl(UserExtraRepository userExtraRepository, UserService userService) {
        this.userExtraRepository = userExtraRepository;
        this.userService = userService;
    }

    @Override
    public Customer getCustomer(Optional<String> currentLogin) {
        if(currentLogin.isPresent()){
            Optional<UserExtra> currentUserExtra = userExtraRepository.findByUserLogin(currentLogin.get());
            if(currentUserExtra.isPresent()){
                return currentUserExtra.get().getCustomer();
            } else {
                throw new IllegalStateException("Could not found the customer of the user");
            }
        } else {
            throw new IllegalStateException("Could not find the user's login");
        }
    }

    @Override
    @Transactional
    public void createUserExtra(UserUserExtraDTO userUserExtraDTO, User user) {
        UserExtra newUserExtra = new UserExtra();
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if(currentUser.isPresent()){
            Optional<UserExtra> userExtraCurrent = userExtraRepository.findById(currentUser.get().getId());
            if(userExtraCurrent.isPresent()){
                if(userExtraCurrent.get().getUser().getAuthorities().stream().noneMatch(a -> a.getName().equals(AuthoritiesConstants.ADMIN))){
                    newUserExtra.setCustomer(userExtraCurrent.get().getCustomer());
                } else {
                    newUserExtra.setCustomer(userUserExtraDTO.getCustomer());
                }
            }
        }
        newUserExtra.setPhone(userUserExtraDTO.getPhone());
        newUserExtra.setUser(user);
        userExtraRepository.save(newUserExtra);
    }

    @Override
    public void updateUserExtra(UserUserExtraDTO userUserExtraDTO, Optional<UserDTO> user) {
        UserExtra newUserExtra = new UserExtra();
        newUserExtra.setPhone(userUserExtraDTO.getPhone());
        newUserExtra.setCustomer(userUserExtraDTO.getCustomer());
        user.ifPresent(userDTO -> newUserExtra.setId(userDTO.getId()));
        userExtraRepository.save(newUserExtra);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserExtraDTO> getAllManagedUserExtras(Pageable pageable) {
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if(currentUser.isPresent() && currentUser.get().getAuthorities().stream().anyMatch(a -> a.getName().equals(AuthoritiesConstants.ADMIN_FONC))){
            Optional<UserExtra> currentUserExtra = userExtraRepository.findById(currentUser.get().getId());
            if (!currentUserExtra.isPresent()) throw new IllegalStateException("There are no user extra for this User");
            return userExtraRepository.findAllByCustomerId(pageable, currentUserExtra.get().getCustomer().getId()).map(UserExtraDTO::new);
        } else {
            return userExtraRepository.findAll(pageable).map(UserExtraDTO::new);
        }
    }
}
