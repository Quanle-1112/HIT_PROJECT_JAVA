package org.example.services;

public interface IEncryptorService {
    String encrypt(String password);
    boolean checkPassword(String plainPassword, String hashedPassword);
}

//Lo việc bảo mật nếu cần(Nếu muốn đổi cách thức bảo mật chỉ cần sửa class impl)