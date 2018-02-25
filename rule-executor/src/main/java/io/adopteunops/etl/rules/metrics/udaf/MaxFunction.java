package io.adopteunops.etl.rules.metrics.udaf;


import lombok.Getter;

public class MaxFunction extends AggregateFunction<Number, Double> {
    @Getter
    private Double maxValue = Double.MIN_VALUE;

    @Override
    public AggregateFunction addValue(Number value) {
        double doubleValue = value.doubleValue();
        if (doubleValue > maxValue) {
            maxValue = doubleValue;
        }
        return this;
    }

    @Override
    public Double compute() {
        return maxValue;
    }

    @Override
    public AggregateFunction<Number, Double> merge(AggregateFunction<Number, Double> newValue) {
        return compute() > newValue.compute() ? this : newValue;
    }
}
