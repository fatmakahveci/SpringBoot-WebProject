package com.fatmakahveci.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fatmakahveci.blog.PostNotFoundException;
import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.model.Post;

@Service
public class IPostService implements PostService {

    private PostRepository postRepository;

    @Autowired
    public IPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post save(@RequestBody Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post findById(@PathVariable Integer id) throws PostNotFoundException {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Override
    public @ResponseBody Iterable<Post> findAll() {
        return postRepository.findAll();
    }
}