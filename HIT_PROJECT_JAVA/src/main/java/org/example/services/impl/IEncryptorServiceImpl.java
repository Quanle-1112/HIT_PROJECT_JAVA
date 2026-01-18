package org.example.services.impl;

import org.example.services.IEncryptorService;
import org.mindrot.jbcrypt.BCrypt;

public class IEncryptorServiceImpl implements IEncryptorService {
    @Override
    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}