package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.CampusLocation;
import com.campus.smartcampus.service.CampusLocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/directory")
public class DirectoryController {

    private final CampusLocationService locationService;

    public DirectoryController(CampusLocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public String listLocations(@RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "category", required = false) String category,
                                @RequestParam(value = "block", required = false) String block,
                                Model model) {
        model.addAttribute("locations", locationService.searchLocations(keyword, category, block));
        model.addAttribute("allLocations", locationService.getAllLocations());
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("block", block);
        return "directory/index";
    }

    @GetMapping("/view/{id}")
    public String viewLocation(@PathVariable("id") Long id, Model model) {
        CampusLocation location = locationService.getLocationById(id);
        model.addAttribute("location", location);
        return "directory/view";
    }
}
