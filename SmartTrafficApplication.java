package com.example.smarttraffic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.*;
import java.util.*;

@SpringBootApplication
public class SmartTrafficApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartTrafficApplication.class, args);
    }
}

@Entity
class TrafficData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;
    private int congestionLevel;
    private String predictedTraffic;

    public TrafficData() {}

    public TrafficData(String location, int congestionLevel, String predictedTraffic) {
        this.location = location;
        this.congestionLevel = congestionLevel;
        this.predictedTraffic = predictedTraffic;
    }

    public Long getId() { return id; }
    public String getLocation() { return location; }
    public int getCongestionLevel() { return congestionLevel; }
    public String getPredictedTraffic() { return predictedTraffic; }
    public void setLocation(String location) { this.location = location; }
    public void setCongestionLevel(int congestionLevel) { this.congestionLevel = congestionLevel; }
    public void setPredictedTraffic(String predictedTraffic) { this.predictedTraffic = predictedTraffic; }
}

interface TrafficRepository extends JpaRepository<TrafficData, Long> {}

@Service
class TrafficService {
    @Autowired
    private TrafficRepository repository;

    public TrafficData updateTraffic(String location, int congestionLevel) {
        String predictedTraffic = predictTraffic(congestionLevel);
        TrafficData data = new TrafficData(location, congestionLevel, predictedTraffic);
        return repository.save(data);
    }

    public List<TrafficData> getTrafficStatus() {
        return repository.findAll();
    }

    private String predictTraffic(int congestionLevel) {
        if (congestionLevel > 80) {
            return "Heavy Traffic Expected";
        } else if (congestionLevel > 50) {
            return "Moderate Traffic Expected";
        } else {
            return "Light Traffic Expected";
        }
    }
}

@RestController
@RequestMapping("/traffic")
class TrafficController {
    @Autowired
    private TrafficService service;

    @PostMapping("/update")
    public TrafficData updateTraffic(@RequestParam String location, @RequestParam int congestionLevel) {
        return service.updateTraffic(location, congestionLevel);
    }

    @GetMapping("/status")
    public List<TrafficData> getTrafficStatus() {
        return service.getTrafficStatus();
    }
}
