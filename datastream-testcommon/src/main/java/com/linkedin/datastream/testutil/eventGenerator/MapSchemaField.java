package com.linkedin.datastream.testutil.eventGenerator;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;


public class MapSchemaField extends SchemaField {

  public MapSchemaField(Field field) {
    super(field);
  }

  @Override
  public void writeToRecord(GenericRecord record) throws UnknownTypeException {
    record.put(_field.name(), generateMap());
  }

  @Override
  public Object generateRandomObject() throws UnknownTypeException {
    return generateMap();
  }

  public Map<Utf8, Object> generateMap() throws UnknownTypeException {
    int count = _randGenerator.getNextInt(1, _maxNumElements);
    Map<Utf8, Object> map = new HashMap<Utf8, Object>(count);
    Field fakeField = new Field(_field.name(), _field.schema().getValueType(), null, null);

    for (int i = 0; i < count; i++) {
      SchemaField filler = SchemaField.createField(fakeField); // create a new filler each time to emulate null-able fields
      map.put(new Utf8(_field.name() + i), filler.generateRandomObject());
    }
    return map;
  }

}