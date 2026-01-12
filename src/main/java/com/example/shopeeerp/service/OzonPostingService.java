package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonPosting;

import java.util.List;

public interface OzonPostingService {

    List<OzonPosting> getAll();

    OzonPosting getByPostingNumber(String postingNumber);

    boolean save(OzonPosting posting);

    boolean saveBatch(List<OzonPosting> postings);

    boolean update(OzonPosting posting);

    boolean deleteByPostingNumber(String postingNumber);

    boolean deleteAll();
}
