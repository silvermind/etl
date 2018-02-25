package io.adopteunops.etl.service.parser;


import io.adopteunops.etl.domain.ProcessParser;

public interface ParserProcess {

    String process(String value, ProcessParser processParser);
}
