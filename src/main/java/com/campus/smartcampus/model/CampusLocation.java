package com.campus.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "campus_locations")
public class CampusLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location name is required")
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "Block/Building name is required")
    @Column(nullable = false, length = 100)
    private String block;

    @Column(length = 50)
    private String floor;

    @Column(nullable = false, length = 50)
    private String category; // ACADEMIC, LAB, LIBRARY, CAFETERIA, ADMIN_OFFICE, SPORTS, HEALTH_CENTER

    @Column(name = "operating_hours", length = 100)
    private String operatingHours;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String icon = "bi-geo-alt";

    @Column(name = "map_zone", length = 50)
    private String mapZone = "Zone A";

    @Column(name = "coord_x")
    private Integer coordX = 50;

    @Column(name = "coord_y")
    private Integer coordY = 50;

    public CampusLocation() {}

    public CampusLocation(String name, String block, String floor, String category, String operatingHours, String contactEmail, String contactPhone, String description, String icon, String mapZone, Integer coordX, Integer coordY) {
        this.name = name;
        this.block = block;
        this.floor = floor;
        this.category = category;
        this.operatingHours = operatingHours;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.description = description;
        this.icon = icon;
        this.mapZone = mapZone;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getMapZone() { return mapZone; }
    public void setMapZone(String mapZone) { this.mapZone = mapZone; }

    public Integer getCoordX() { return coordX; }
    public void setCoordX(Integer coordX) { this.coordX = coordX; }

    public Integer getCoordY() { return coordY; }
    public void setCoordY(Integer coordY) { this.coordY = coordY; }
}
