package com.learn.spring.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.spring.todoapp.entity.Todo;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {

    List<Todo> findByUsername(String username);

    void deleteById(Integer id);

    Optional<Todo> findById(Integer id);
}