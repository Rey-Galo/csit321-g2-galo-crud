package com.csit321g2.firstspringbootapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.csit321g2.firstspringbootapp.entity.ItemEntity;

@Repository 
public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {
}
