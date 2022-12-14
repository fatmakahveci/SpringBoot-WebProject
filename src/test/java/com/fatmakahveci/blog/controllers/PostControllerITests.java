package com.fatmakahveci.blog.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureJsonTesters
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PostControllerITests {

    @Autowired
    private MockMvc mockMvc; // fakes HTTP requests 

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private TagService tagService;

    @Autowired
    private JacksonTester<Post> jsonPost;

    @Test
    public void canRetrieveByIdWhenExists() throws Exception {
        List<Post> posts = new ArrayList<>();
        Post post = new Post(1, "title", "content", Collections.emptySet());
        posts.add(post);
        given(postService.findAll()).willReturn(posts);

        MockHttpServletResponse response = mockMvc.perform(get("/posts")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals("application/json", response.getContentType());
        assertEquals(response.getContentAsString(), String.format("[%s]", jsonPost.write(post).getJson()));
    }

    @Test
    public void canRetrieveByIdWhenDoesNotExist() throws Exception {
        given(postService.findById(1)).willReturn(null);

        MockHttpServletResponse response = mockMvc.perform(get("/posts")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void canRenderNewPostForm() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("post_form"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", new Post()))
                .andExpect(model().attributeExists("tag"))
                .andExpect(model().attribute("tag", new Tag()))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attribute("tags", Matchers.empty()));
    }

    @Test
    public void canSavePost() throws Exception {
        Post post = new Post(1, "title", "content", Collections.emptySet());
        
        given(postService.save(post)).willReturn(post);

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getPostFromForm() throws Exception {
        Post post = new Post(1, "title", "content", Collections.emptySet());
        
        when(postService.findById(1)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/{id}", "1"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("text/html;charset=UTF-8"));
    }
}
