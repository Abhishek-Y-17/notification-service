package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor
@Data
@Entity
@Table(name="blacklist")
public class Blacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="phone_no" ,unique=true)
    private String phoneNo;
    public Blacklist(String phoneNo) {
        this.phoneNo = phoneNo;
    }



}
