package service;

import model.User;
import repository.DataStorage;

public class AuthServiceImpl implements AuthService {
    
    @Override
    public User login(String username, String password) {
        for (User u : DataStorage.users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u; 
            }
        }
        return null; 
    }
}