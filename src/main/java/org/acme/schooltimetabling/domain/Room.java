package org.acme.schooltimetabling.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;

@JsonIdentityInfo(scope = Room.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Room {

    @PlanningId
    private String id;

    private String name;

    private List<String> suitableSubjects;

    public Room() {
    }

    public Room(String id, String name, List<String> suitableSubjects) {
        this.id = id;
        this.name = name;
        this.suitableSubjects = suitableSubjects;
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getSuitableSubjects() {
        return suitableSubjects;
    }
}
