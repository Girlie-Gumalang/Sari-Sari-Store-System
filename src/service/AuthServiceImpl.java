package service;

import model.User;
import repository.DataStorage;

public class AuthServiceImpl implements AuthService {
    
    @Override
    public User login(String username, String password) {
        for (User u : DataStorage.users) {
            // check if tama yung username and password
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u; 
            }
        }
        return null; 
    }
}