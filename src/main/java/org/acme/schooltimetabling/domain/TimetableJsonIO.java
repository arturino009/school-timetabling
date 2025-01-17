package org.acme.schooltimetabling.domain;

import ai.timefold.solver.jackson.impl.domain.solution.JacksonSolutionFileIO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TimetableJsonIO extends JacksonSolutionFileIO<Timetable> {
    public TimetableJsonIO() { super(Timetable.class,
            new ObjectMapper().findAndRegisterModules()); }
}
