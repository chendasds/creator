package com.creation.platform.controller;

import com.creation.platform.entity.UserInteraction;
import com.creation.platform.service.UserInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interaction")
public class UserInteractionController {

    @Autowired
    private UserInteractionService userInteractionService;

    @PostMapping
    public Map<String, Object> interact(@RequestBody UserInteraction interaction) {
        boolean result = userInteractionService.interact(
                interaction.getUserId(),
                interaction.getArtworkId(),
                interaction.getInteractionType()
        );
        Map<String, Object> map = new HashMap<>();
        map.put("success", result);
        return map;
    }

    @DeleteMapping
    public Map<String, Object> cancelInteraction(@RequestBody UserInteraction interaction) {
        boolean result = userInteractionService.cancelInteraction(
                interaction.getUserId(),
                interaction.getArtworkId(),
                interaction.getInteractionType()
        );
        Map<String, Object> map = new HashMap<>();
        map.put("success", result);
        return map;
    }

    @GetMapping("/check")
    public Map<String, Object> check(
            @RequestParam Long userId,
            @RequestParam Long artworkId,
            @RequestParam Integer interactionType) {
        boolean existed = userInteractionService.hasInteracted(userId, artworkId, interactionType);
        Map<String, Object> map = new HashMap<>();
        map.put("interacted", existed);
        return map;
    }
}
