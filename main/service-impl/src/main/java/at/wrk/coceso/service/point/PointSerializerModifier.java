package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.point.DummyPoint;
import at.wrk.coceso.entity.point.Point;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

class PointSerializerModifier extends BeanSerializerModifier {

  private final Converter<Object, Point> converter = new StdConverter<Object, Point>() {
    @Override
    public Point convert(Object obj) {
      return (obj instanceof Point) ? ((Point) obj).create(null) : null;
    }
  };

  @Override
  public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
    if (DummyPoint.class.isAssignableFrom(beanDesc.getBeanClass())) {
      // This is far from optimal, since it can end up here again with the result of convert, which might lead to endless recursion
      // Nevertheless, just feeding the result of convert to the original serializer won't work because the class changes,
      // which breaks reflection
      return new StdDelegatingSerializer(converter);
    }
    return super.modifySerializer(config, beanDesc, serializer);
  }

}
