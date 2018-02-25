package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.GrokDomain;
import io.adopteunops.etl.domain.GrokResult;
import io.adopteunops.etl.domain.GrokResultSimulate;
import io.adopteunops.etl.service.parser.GrokService;
import io.adopteunops.etl.web.domain.GrokSimulateWeb;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/grok")
public class GrokController {

    private final GrokService grokService;

    @ResponseStatus(OK)
    @GetMapping("/find")
    public List<GrokDomain> findGrok(@PathParam("filters") String filter) {
        return grokService.findGrokPatten(filter);
    }

    @ResponseStatus(OK)
    @PostMapping("/simulate")
    public List<GrokResultSimulate> simulate(@RequestBody GrokSimulateWeb grokSimluateWeb) {
        return grokService.simulate(grokSimluateWeb.getGrokPattern(), grokSimluateWeb.getValueList());
    }

    @ResponseStatus(OK)
    @PostMapping("/simulateAllPattern")
    public List<GrokResult> simulateAllPattern(@RequestBody String value) {
        return grokService.simulateAllPattern(value);
    }

}
