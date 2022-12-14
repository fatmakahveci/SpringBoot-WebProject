package com.fatmakahveci.blog.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatmakahveci.blog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query(value = "SELECT * FROM posts p where p.title = :title", nativeQuery = true)
    Optional<Post> findByTitle(@Param("title") String title);
}