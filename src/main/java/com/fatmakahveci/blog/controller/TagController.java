package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class TagController {
    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(path = "/tags")
    public List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping(path = "/tag/{id}")
    public ModelAndView getTagPosts(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView("tag");
        Optional<Tag> optionalTag = tagService.findById(id);
        Set<Post> posts = new HashSet<>();
        Tag tag = new Tag();
        if (optionalTag.isPresent()) {
            tag = optionalTag.get();
            posts = tag.getPosts();
        }
        mav.addObject("tag", tag);
        mav.addObject("posts", posts);
        return mav;
    }

    @PostMapping(value="/tags/save")
    public ModelAndView saveTag(@ModelAttribute Tag tag) {
        Tag newTag = tagService.getOrCreateByName(tag.getName());
        tagService.save(newTag);
        return new ModelAndView("redirect:/");
    }

    @GetMapping(value="/tags/delete/{id}")
    public ModelAndView deleteTag(@PathVariable Integer id) {
        Optional<Tag> optionalTag = tagService.findById(id);
        Tag tagToBeDeleted;
        if (optionalTag.isPresent()) {
            tagToBeDeleted = optionalTag.get();
            tagToBeDeleted.deleteTagFromPosts();
            tagService.deleteById(tagToBeDeleted.getId());
       }
       return new ModelAndView("redirect:/");
    }
}
