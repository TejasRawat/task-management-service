package com.example.taskmanagement.adapters.in.graphql.scaler;

import com.netflix.graphql.dgs.DgsScalar;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.springframework.web.multipart.MultipartFile;

@DgsScalar(name = "Upload")
public class UploadScalar implements Coercing<MultipartFile, Void> {

  public UploadScalar() {
    System.out.println("🎯 UploadScalar Initialized!");
  }

  @Override
  public MultipartFile parseValue(Object input) throws CoercingParseValueException {
    System.out.println("📥 parseValue triggered");
    if (input instanceof MultipartFile) {
      return (MultipartFile) input;
    }
    throw new CoercingParseValueException("Expected a MultipartFile object.");
  }

  @Override
  public MultipartFile parseLiteral(Object input) throws CoercingParseLiteralException {
    throw new CoercingParseLiteralException("Parsing literal is not supported for Upload.");
  }

  @Override
  public Void serialize(Object dataFetcherResult) throws CoercingSerializeException {
    throw new CoercingSerializeException("Upload scalar is input-only.");
  }
}
