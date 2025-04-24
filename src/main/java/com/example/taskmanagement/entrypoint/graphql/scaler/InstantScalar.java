package com.example.taskmanagement.entrypoint.graphql.scaler;

import com.netflix.graphql.dgs.DgsScalar;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

@DgsScalar(name = "Instant")
public class InstantScalar implements Coercing<Instant, String> {
  @Override
  public String serialize(@NotNull Object dataFetcherResult) throws CoercingSerializeException {
    if (dataFetcherResult instanceof Instant) {
      return dataFetcherResult.toString();
    } else {
      throw new CoercingSerializeException("Not a valid Instant");
    }
  }

  @Override
  @NotNull
  public Instant parseValue(@NotNull Object input) throws CoercingParseValueException {
    return Instant.parse(((StringValue) input).getValue());
  }

  @Override
  @NotNull
  public Instant parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
    if (input instanceof StringValue str) {
      return Instant.parse(str.getValue());
    }
    throw new CoercingParseLiteralException("Value is not a valid ISO date time");
  }
}
