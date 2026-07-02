package com.canosaa.tinyurl.service;

import com.canosaa.tinyurl.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    boolean existsByUrl(String url);

    Optional<Url> findByUrl(String url);
}
