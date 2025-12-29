package org.example.sensenebula.repository;

import org.example.sensenebula.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // 根据班级查
    List<User> findByClassid(String classid);

    // 根据姓名模糊查
    List<User> findByNameContaining(String name);

    // 根据班级 + 姓名 联合查
    List<User> findByClassidAndNameContaining(String classid, String name);
}