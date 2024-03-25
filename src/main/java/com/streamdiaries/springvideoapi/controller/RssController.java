package com.streamdiaries.springvideoapi.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.streamdiaries.springvideoapi.entity.Rss;
import com.streamdiaries.springvideoapi.repository.RssRepository;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RssController {

    @Autowired
    private RssRepository rssRepository;

    @GetMapping("/rss/{code}")
    public ResponseEntity<String> getFeed(@PathVariable String code) {
        Optional<Rss> rssFeed = Optional.empty();
        if ("movie".equals(code)) {
            rssFeed = rssRepository.findByCode("movie");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(rssFeed.map(Rss::getContent).orElse("Empty"), headers, HttpStatus.OK);
    }

    

}
