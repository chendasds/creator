package com.creation.platform.controller;

import com.creation.platform.entity.ArtworkTagRelation;
import com.creation.platform.service.ArtworkTagRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artwork-tag")
public class ArtworkTagRelationController {

    @Autowired
    private ArtworkTagRelationService artworkTagRelationService;

    @GetMapping("/artwork/{artworkId}")
    public Map<String, Object> getTagsByArtworkId(@PathVariable Long artworkId) {
        List<Long> tagIds = artworkTagRelationService.getTagIdsByArtworkId(artworkId);
        Map<String, Object> result = new HashMap<>();
        result.put("tagIds", tagIds);
        result.put("total", tagIds.size());
        return result;
    }

    @PostMapping("/artwork/{artworkId}")
    public boolean setTags(@PathVariable Long artworkId, @RequestBody List<Long> tagIds) {
        return artworkTagRelationService.setTags(artworkId, tagIds);
    }

    @GetMapping("/list")
    public List<ArtworkTagRelation> list() {
        return artworkTagRelationService.list();
    }
}
