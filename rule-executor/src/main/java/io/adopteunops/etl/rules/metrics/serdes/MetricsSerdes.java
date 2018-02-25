package io.adopteunops.etl.rules.metrics.serdes;

import io.adopteunops.etl.rules.metrics.domain.Keys;
import io.adopteunops.etl.rules.metrics.udaf.AggregateFunction;
import io.adopteunops.etl.serdes.GenericDeserializer;
import io.adopteunops.etl.serdes.GenericSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class MetricsSerdes {

    public static Serde<Keys> keysSerde() {
        return Serdes.serdeFrom(new GenericSerializer<Keys>(), new GenericDeserializer(Keys.class));
    }

    public static Serde<AggregateFunction> aggFunctionSerdes() {
        return Serdes.serdeFrom(new GenericSerializer<AggregateFunction>(), new GenericDeserializer(AggregateFunction.class));
    }
}
