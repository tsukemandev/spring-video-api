package com.streamdiaries.springvideoapi.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.streamdiaries.springvideoapi.entity.Rss;

public interface RssRepository extends CrudRepository<Rss, String>{
    Optional<Rss> findByCode(String code);
    
    List<Rss> findAllByCode(String code);
}
