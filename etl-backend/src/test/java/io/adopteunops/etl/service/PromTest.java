package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.prometheus.PrometheusDataHack;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class PromTest {

    public void callProm(){
        RestTemplate restTemplate = new RestTemplate();
        String datatype = "{type=\"alive\"}";
        String url ="http://34.240.248.199:9090/api/v1/query_range?query=alive_search_duration&start=2018-02-26T08:45:00.000Z&end=2018-02-26T08:55:00.000Z&step=15s";
        PrometheusDataHack prometheusDataHack = restTemplate.getForObject(url,PrometheusDataHack.class,datatype);
        log.error(prometheusDataHack.toString());
        Long init = Long.valueOf(prometheusDataHack.getData().getResult().get(0).getValues()[0][1]);
        int last = prometheusDataHack.getData().getResult().get(0).getValues().length;
        Long result= Long.valueOf(prometheusDataHack.getData().getResult().get(0).getValues()[last ==0 ? last : last - 1][1]);
        log.error("result {} init {} diff {}",result,init,(result-init));
    }

}
