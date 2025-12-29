package org.example.sensenebula.service;

import org.example.sensenebula.model.User;
import org.example.sensenebula.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public List<User> getUsersByClass(String classid) {
        return repo.findByClassid(classid);
    }

    public List<User> getUsersByNameLike(String name) {
        return repo.findByNameContaining(name);
    }

    public User addUser(User user) {
        return repo.save(user); // 增/改
    }

    public void deleteUser(Long id) {
        repo.deleteById(id);
    }

    public User getUserById(Long id) {
        return repo.findById(id).orElse(null);
    }
}