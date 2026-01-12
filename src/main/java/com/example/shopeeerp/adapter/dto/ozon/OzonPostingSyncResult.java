package com.example.shopeeerp.adapter.dto.ozon;

import com.example.shopeeerp.pojo.OzonPosting;
import com.example.shopeeerp.pojo.OzonPostingItem;

import java.util.List;

public class OzonPostingSyncResult {
    private List<OzonPosting> postings;
    private List<OzonPostingItem> items;

    public List<OzonPosting> getPostings() {
        return postings;
    }

    public void setPostings(List<OzonPosting> postings) {
        this.postings = postings;
    }

    public List<OzonPostingItem> getItems() {
        return items;
    }

    public void setItems(List<OzonPostingItem> items) {
        this.items = items;
    }
}
