package org.acme.schooltimetabling.rest;

import ai.timefold.solver.benchmark.api.PlannerBenchmark;
import ai.timefold.solver.benchmark.api.PlannerBenchmarkFactory;
import ai.timefold.solver.benchmark.config.PlannerBenchmarkConfig;
import ai.timefold.solver.benchmark.config.ProblemBenchmarksConfig;
import ai.timefold.solver.benchmark.config.SolverBenchmarkConfig;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicType;
import ai.timefold.solver.core.config.localsearch.LocalSearchPhaseConfig;
import ai.timefold.solver.core.config.localsearch.LocalSearchType;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.schooltimetabling.domain.TimetableJsonIO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Path("benchmark")
public class BenchmarkerRunner {
    public static class BenchmarkPayload {
        private final Long duration;
        private final ConstructionHeuristicType construction_heuristic;
        private final LocalSearchType[] algorithms;

        public BenchmarkPayload(Long duration, ConstructionHeuristicType construction_heuristic, LocalSearchType[] algorithms) {
            this.duration = duration;
            this.construction_heuristic = construction_heuristic;
            this.algorithms = algorithms;
        }
    }
    @Operation(summary = "Run benchmark")
    @APIResponses(value = {
            @APIResponse(responseCode = "200",
                    description = "Run benchmark",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))) })
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(MediaType.TEXT_PLAIN)
    public String runBenchmark(BenchmarkPayload parameters) {
        // Import base config parameters
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("baseSolverConfig.xml");


        // Set construction heuristic
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(parameters.construction_heuristic);

        // Set duration
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setSecondsSpentLimit(parameters.duration);

        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        problemBenchmarksConfig.setSolutionFileIOClass(TimetableJsonIO.class);
        problemBenchmarksConfig.setWriteOutputSolutionEnabled(true);
        problemBenchmarksConfig.setInputSolutionFileList(Collections.singletonList(new File("data/example_LARGE.json")));

        List<SolverBenchmarkConfig> solverBenchmarkConfigList = new ArrayList<>(List.of());

        for (LocalSearchType alg : parameters.algorithms) {
            SolverConfig solverConfigTemp = new SolverConfig(solverConfig);
            LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
            localSearchPhaseConfig.setLocalSearchType(alg);
            localSearchPhaseConfig.setTerminationConfig(terminationConfig);

            solverConfigTemp.setPhaseConfigList(List.of(constructionHeuristicPhaseConfig, localSearchPhaseConfig));
            SolverBenchmarkConfig solverBenchmarksConfig = new SolverBenchmarkConfig();

            solverBenchmarksConfig.setName(alg.toString());
            solverBenchmarksConfig.setSolverConfig(solverConfigTemp);
            solverBenchmarksConfig.setProblemBenchmarksConfig(problemBenchmarksConfig);
            solverBenchmarkConfigList.add(solverBenchmarksConfig);
        }

        PlannerBenchmarkConfig plannerBenchmark = new PlannerBenchmarkConfig();
        plannerBenchmark.withSolverBenchmarkConfigList(solverBenchmarkConfigList);
        plannerBenchmark.setBenchmarkDirectory(new File("benchmarkReports"));

        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(plannerBenchmark);
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
        benchmark.benchmarkAndShowReportInBrowser();
        return "success";
    }
}
