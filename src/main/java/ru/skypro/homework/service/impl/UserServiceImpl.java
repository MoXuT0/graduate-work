package ru.skypro.homework.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.mapper.UserMapper;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    //@Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User with username " + username + " doesn't exists"));
    }

    @Override
    public boolean isAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated();
    }

    @Override
    public boolean isAuth(Authentication authentication) {
        return authentication.isAuthenticated();
    }

    @Override
    public Optional<User> findAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return userRepository.findByEmail(currentPrincipalName);
    }

    @Override
    public boolean isCurrentPassTrue(NewPasswordDto newPasswordDto, String email) {
        //Optional<User> currentUser = userRepository.findByEmail(email);
        Optional<User> currentUser = findAuthUser();
        if (currentUser.isPresent()) {
            User currentUserValue = currentUser.get();
            return (currentUserValue.getPassword().equals(newPasswordDto.getCurrentPassword()));
        }
        return false;
    }

    @Override
    public UserDto getUserDto() {
        Optional<User> currentUser = findAuthUser();
        UserDto currentUserDto = new UserDto();
        if (currentUser.isPresent()) {
            currentUserDto = userMapper.mapToUserDto(currentUser.get());
        }
        return currentUserDto;
    }

    @Override
    public UserDto getUserDto(Authentication authentication) {
        UserDto currentUserDto = new UserDto();
        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser.isPresent()) {
            currentUserDto = userMapper.mapToUserDto(currentUser.get());
        }
        return currentUserDto;
    }

    @Override
    public UserDto updateUserDto(UserDto newUserDto) {
        Optional<User> currentUser = findAuthUser();
        User newCurrentUser = new User();
        if (currentUser.isPresent()) {
            newCurrentUser = currentUser.get();
            newCurrentUser.setFirstName(newUserDto.getFirstName());
            newCurrentUser.setLastName(newUserDto.getLastName());
            newCurrentUser.setPhone(newUserDto.getPhone());
            //другие поля остаются теми же
            userRepository.save(newCurrentUser);
        }
        return userMapper.mapToUserDto(newCurrentUser);
    }

    @Override
    public UserDto updateUserDto(UserDto newUserDto, Authentication authentication) {
        User newCurrentUser = new User();
        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser.isPresent()) {
            newCurrentUser = currentUser.get();
            newCurrentUser.setFirstName(newUserDto.getFirstName());
            newCurrentUser.setLastName(newUserDto.getLastName());
            newCurrentUser.setPhone(newUserDto.getPhone());
            //другие поля остаются теми же
            userRepository.save(newCurrentUser);
        }
        return userMapper.mapToUserDto(newCurrentUser);
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        return Optional.empty();
    }


    @Override
    public void updateUserImage(MultipartFile image) {
    }
}