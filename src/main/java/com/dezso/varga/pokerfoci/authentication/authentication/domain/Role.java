package com.dezso.varga.pokerfoci.authentication.authentication.domain;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by dezso on 01.07.2017.
 */
@Entity
public class Role {
    private Long id;
    private String name;

    public Role() {
        super();
    }

    public Role(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
