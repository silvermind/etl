package io.adopteunops.etl.domain;

public enum TypeImporter {
    PROCESS,
    PROCESS_SHUTDOWN,
    RETRY,
    RETRY_SHUTDOWN,
    ERROR,
    ERROR_SHUTDOWN
}