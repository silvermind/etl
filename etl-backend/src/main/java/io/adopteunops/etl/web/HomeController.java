package io.adopteunops.etl.web;

import io.adopteunops.etl.service.HomeService;
import io.adopteunops.etl.web.domain.DataCharts;
import io.adopteunops.etl.web.domain.DataChartsWeb;
import io.adopteunops.etl.web.domain.HomeWeb;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/home")
@AllArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @ResponseStatus(OK)
    @GetMapping("fetch")
    public HomeWeb home() {
        return homeService.getHome();
    }

    @ResponseStatus(OK)
    @GetMapping("dataCapture")
    public DataChartsWeb dataCapture() {
        return DataChartsWeb.builder()
                .dataProcess(homeService.chartsForProcess())
                .dataConfiguration(homeService.chartsForConfiguration())
                .dataMetric(homeService.chartsForMetrics())
                .dataWorker(homeService.chartsForWorker())
                .build();
    }


}
