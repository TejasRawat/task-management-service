package com.example.taskmanagement.adapters.in.graphql.scaler;

import com.netflix.graphql.dgs.DgsScalar;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@DgsScalar(name = "Date")
public class DateScalar implements Coercing<LocalDate, String> {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  @Override
  public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
    if (dataFetcherResult instanceof LocalDate) {
      return FORMATTER.format((LocalDate) dataFetcherResult);
    }
    throw new CoercingSerializeException("Invalid value for LocalDate: " + dataFetcherResult);
  }

  @Override
  public LocalDate parseValue(Object input) throws CoercingParseValueException {
    try {
      return LocalDate.parse(input.toString(), FORMATTER);
    } catch (Exception e) {
      throw new CoercingParseValueException("Invalid date format, expected yyyy-MM-dd");
    }
  }

  @Override
  public LocalDate parseLiteral(Object input) throws CoercingParseLiteralException {
    return parseValue(input);
  }
}