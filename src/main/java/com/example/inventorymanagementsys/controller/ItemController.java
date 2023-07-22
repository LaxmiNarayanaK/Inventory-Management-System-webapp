package com.example.inventorymanagementsys.controller;

import com.example.inventorymanagementsys.model.Item;
import com.example.inventorymanagementsys.service.CRUDService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final CRUDService service;

    public ItemController(CRUDService service) {
        this.service = service;
    }

    @GetMapping
    public List<Item> getItems(){
        return service.findAll();
    }

    @DeleteMapping("/{item_id}")
    public String deleteItem(@PathVariable Long item_id){
        return service.deleteCategory(item_id);
    }
}
