package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.User;
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
public interface UserExtraService {
    void createUserExtra(UserUserExtraDTO userUserExtraDTO, User user);
    void updateUserExtra(UserUserExtraDTO userUserExtraDTO, Optional<UserDTO> user);
    Page<UserExtraDTO> getAllManagedUserExtras(Pageable pageable);
    Customer getCustomer(Optional<String> currentLogin);
}
