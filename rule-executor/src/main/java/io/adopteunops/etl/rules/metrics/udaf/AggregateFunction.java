package io.adopteunops.etl.rules.metrics.udaf;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class AggregateFunction<InputValueType, OutputValueType> {

    public abstract AggregateFunction addValue(InputValueType value);

    public abstract OutputValueType compute();

    public abstract AggregateFunction<Number, Double> merge(AggregateFunction<InputValueType, OutputValueType> newValue);
}
