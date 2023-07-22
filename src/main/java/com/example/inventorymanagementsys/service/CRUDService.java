package com.example.inventorymanagementsys.service;

import com.example.inventorymanagementsys.model.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class CRUDService {
    public List<Item> findAll() {
        return null;
    }


    public String deleteCategory(Long categoryId) {
        return null;
    }
}
