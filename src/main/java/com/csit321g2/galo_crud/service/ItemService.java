package com.csit321g2.galo_crud.service;

import java.util.List;

import javax.naming.NameNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.csit321g2.galo_crud.entity.ItemEntity;
import com.csit321g2.galo_crud.repository.ItemRepository;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemEntity postItem(ItemEntity Item) {
        return itemRepository.save(Item);
    }

    public List<ItemEntity> getAllItems() {
        return itemRepository.findAll();
    }

    public ItemEntity putItem(int id, ItemEntity newItemDetails) {
        ItemEntity item = itemRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Item " + id + " does not exist!"));
        item.setName(newItemDetails.getName());
        item.setUnit(newItemDetails.getUnit());
        item.setPrice(newItemDetails.getPrice());
        return itemRepository.save(item);
    }

    public String deleteItem(int id) {
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item " + id + " does not exist!");
        }
        itemRepository.deleteById(id);
        return "Item " + id + " is successfully deleted!";
    }

    @ExceptionHandler
    public ResponseEntity<NameErrorResponse> handleException(NameNotFoundException ex) {

        NameErrorResponse error = new NameErrorResponse();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(System.currentTimeMillis());

        // return response entity
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler 
    public ResponseEntity<NameErrorResponse> handleException(Exception ex) {

        NameErrorResponse error = new NameErrorResponse();

        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(System.currentTimeMillis());

        // return response entity
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    

}
