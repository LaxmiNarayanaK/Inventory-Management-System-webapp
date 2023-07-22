package com.example.inventorymanagementsys.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item {
    @Id
    private Long id;
    private String name;
    private int quantity;
    private Date date;

    public Item(String name, int quantity, Date date) {
        this.name = name;
        this.quantity = quantity;
        this.date = date;
    }

}
