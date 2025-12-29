package org.example.sensenebula.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "classid", nullable = false, length = 50)
    private String classid;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /* --- 无参构造 + getter/setter --- */
    public User() {
    }

    public User(String classid, String name) {
        this.classid = classid;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}