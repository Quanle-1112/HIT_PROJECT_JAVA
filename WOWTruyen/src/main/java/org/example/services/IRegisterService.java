package org.example.services;

import org.example.model.user.User;

public interface IRegisterService {
    String register(User user, String confirmPassword);
}