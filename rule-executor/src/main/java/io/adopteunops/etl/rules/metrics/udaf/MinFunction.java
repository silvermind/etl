package io.adopteunops.etl.rules.metrics.udaf;


import lombok.Getter;

public class MinFunction extends AggregateFunction<Number, Double> {
    @Getter
    private Double minValue = Double.MAX_VALUE;

    @Override
    public AggregateFunction addValue(Number value) {
        double doubleValue = value.doubleValue();
        if (doubleValue < minValue) {
            minValue = doubleValue;
        }
        return this;
    }

    @Override
    public Double compute() {
        return minValue;
    }

    @Override
    public AggregateFunction<Number, Double> merge(AggregateFunction<Number, Double> newValue) {
        return compute() < newValue.compute() ? this : newValue;
    }
}
