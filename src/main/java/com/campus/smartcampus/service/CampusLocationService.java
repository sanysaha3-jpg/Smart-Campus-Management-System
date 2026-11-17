package com.campus.smartcampus.service;

import com.campus.smartcampus.model.CampusLocation;
import com.campus.smartcampus.repository.CampusLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CampusLocationService {

    private final CampusLocationRepository locationRepository;

    public CampusLocationService(CampusLocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<CampusLocation> getAllLocations() {
        return locationRepository.findAllByOrderByNameAsc();
    }

    public List<CampusLocation> searchLocations(String keyword, String category, String block) {
        return locationRepository.searchLocations(keyword, category, block);
    }

    public CampusLocation getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + id));
    }

    public CampusLocation saveLocation(CampusLocation location) {
        return locationRepository.save(location);
    }
}
