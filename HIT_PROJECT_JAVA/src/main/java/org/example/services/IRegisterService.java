package org.example.services;

import org.example.model.user.User;

public interface IRegisterService {
    void register(User user, String confirmPassword);
}