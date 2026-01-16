package org.example.services;

import org.example.model.user.User;

public interface ILoginService {
    User authenticate(String username, String password);
}