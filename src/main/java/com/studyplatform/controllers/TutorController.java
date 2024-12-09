package com.studyplatform.controllers;

import com.studyplatform.models.Tutor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TutorController {
    private List<Tutor> tutors;

    public TutorController() {
        this.tutors = new ArrayList<>();
        populateInitialTutors();
    }

    private void populateInitialTutors() {
        tutors.add(new Tutor("Tutor A", "Math", "12-10-2024", "Room 101", false));
        tutors.add(new Tutor("Tutor B", "Science", "12-11-2024", "Room 202", false));
        tutors.add(new Tutor("Tutor C", "Math", "12-12-2024", "Room 101", false));
        tutors.add(new Tutor("Tutor D", "English", "12-13-2024", "Room 303", false));
    }

    public Tutor addTutor(String name, String tutorClass, String availableDate, String location) {
        Tutor tutor = new Tutor(name, tutorClass, availableDate, location, false);
        tutors.add(tutor);
        return tutor;
    }

    public void scheduleTutorSession(String tutorName) {
        Tutor tutor = findTutorByName(tutorName);
        if (tutor != null && !tutor.isScheduled()) {
            tutor.setScheduled(true);
        }
    }

    public List<Tutor> getTutorsByClass(String tutorClass) {
        return tutors.stream()
                .filter(tutor -> tutor.getTutorClass().equals(tutorClass))
                .collect(Collectors.toList());
    }

    public Tutor findTutorByName(String name) {
        return tutors.stream()
                .filter(tutor -> tutor.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<String> getUniqueTutorClasses() {
        return tutors.stream()
                .map(Tutor::getTutorClass)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Tutor> getAllTutors() {
        return new ArrayList<>(tutors);
    }
}
