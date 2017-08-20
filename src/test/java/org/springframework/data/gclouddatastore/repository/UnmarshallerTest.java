package org.springframework.data.gclouddatastore.repository;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.gclouddatastore.repository.Unmarshaller;
import org.springframework.data.annotation.Id;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.StringValue;

public class UnmarshallerTest {
    @Data
    @NoArgsConstructor
    public static class TestBean {
        @Id
        long id;
        Object object;
        byte[] bytes;
        String string;
        Boolean boxedBoolean;
        boolean primitiveBoolean;
        Double boxedDouble;
        double primitiveDouble;
        Float boxedFloat;
        float primitiveFloat;
        Long boxedLong;
        long primitiveLong;
        Integer boxedInteger;
        int primitiveInt;
        Short boxedShort;
        short primitiveShort;
        Byte boxedByte;
        byte primitiveByte;
        List<?> list;
        LinkedList<?> linkedList;
        Map<String, Object> map;
        URI uri;
        Instant instant;
        Date date;
        Calendar calendar;
        java.sql.Timestamp sqlTimestamp;
        LocalDateTime localDateTime;
        OffsetDateTime offsetDateTime;
        ZonedDateTime zonedDateTime;
    }

    @Test
    public void testUnmarshalToObject_Blob() {
        //Setup
        byte[] hello = "hello".getBytes(Charset.forName("UTF-8"));
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", Blob.copyFrom(hello))
            .set("bytes", Blob.copyFrom(hello))
            .set("string", Blob.copyFrom(hello))
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Assert.assertArrayEquals(hello, (byte[])bean.object);
        Assert.assertArrayEquals(hello, bean.bytes);
        Assert.assertEquals("hello", bean.string);
    }

    @Test
    public void testUnmarshalToObject_Boolean() {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", true)
            .set("boxedBoolean", true)
            .set("primitiveBoolean", true)
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Assert.assertEquals(Boolean.TRUE, bean.object);
        Assert.assertEquals(Boolean.TRUE, bean.boxedBoolean);
        Assert.assertTrue(bean.primitiveBoolean);
    }

    @Test
    public void testUnmarshalToObject_Double() {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", 3.14)
            .set("boxedDouble", 3.14)
            .set("primitiveDouble", 3.14)
            .set("boxedFloat", 3.14)
            .set("primitiveFloat", 3.14)
            .set("boxedLong", 3.14)
            .set("primitiveLong", 3.14)
            .set("boxedInteger", 3.14)
            .set("primitiveInt", 3.14)
            .set("boxedShort", 3.14)
            .set("primitiveShort", 3.14)
            .set("boxedByte", 3.14)
            .set("primitiveByte", 3.14)
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Assert.assertEquals(Double.valueOf(3.14), bean.object);
        Assert.assertEquals(Double.valueOf(3.14), bean.boxedDouble);
        Assert.assertEquals(3.14, bean.primitiveDouble, 0.0);
        Assert.assertEquals(Float.valueOf(3.14f), bean.boxedFloat);
        Assert.assertEquals(3.14f, bean.primitiveFloat, 0.0f);
        Assert.assertEquals(Long.valueOf(3L), bean.boxedLong);
        Assert.assertEquals(3, bean.primitiveLong);
        Assert.assertEquals(Integer.valueOf(3), bean.boxedInteger);
        Assert.assertEquals(3, bean.primitiveInt);
        Assert.assertEquals(Short.valueOf((short)3), bean.boxedShort);
        Assert.assertEquals(3, bean.primitiveShort);
        Assert.assertEquals(Byte.valueOf((byte)3), bean.boxedByte);
        Assert.assertEquals(3, bean.primitiveByte);
    }

    @Test
    public void testUnmarshalToObject_Long() {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", 42)
            .set("boxedLong", 42)
            .set("primitiveLong", 42)
            .set("boxedInteger", 42)
            .set("primitiveInt", 42)
            .set("boxedShort", 42)
            .set("primitiveShort", 42)
            .set("boxedByte", 42)
            .set("primitiveByte", 42)
            .set("boxedDouble", 42)
            .set("primitiveDouble", 42)
            .set("boxedFloat", 42)
            .set("primitiveFloat", 42)
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Assert.assertEquals(Long.valueOf(42), bean.object);
        Assert.assertEquals(Long.valueOf(42), bean.boxedLong);
        Assert.assertEquals(42L, bean.primitiveLong);
        Assert.assertEquals(Integer.valueOf(42), bean.boxedInteger);
        Assert.assertEquals(42, bean.primitiveInt);
        Assert.assertEquals(Short.valueOf((short)42), bean.boxedShort);
        Assert.assertEquals(42, bean.primitiveShort);
        Assert.assertEquals(Byte.valueOf((byte)42), bean.boxedByte);
        Assert.assertEquals(42, bean.primitiveByte);
        Assert.assertEquals(Double.valueOf(42.0), bean.boxedDouble);
        Assert.assertEquals(42.0, bean.primitiveDouble, 0.0);
        Assert.assertEquals(Float.valueOf(42.0f), bean.boxedFloat);
        Assert.assertEquals(42.0f, bean.primitiveFloat, 0.0f);
    }

    @Test
    public void testUnmarshalToObject_String() throws Exception {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", "hello")
            .set("string", "hello")
            .set("bytes", "hello")
            .set("boxedLong", "42")
            .set("primitiveLong", "42")
            .set("boxedInteger", "42")
            .set("primitiveInt", "42")
            .set("boxedShort", "42")
            .set("primitiveShort", "42")
            .set("boxedByte", "42")
            .set("primitiveByte", "42")
            .set("boxedDouble", "3.14")
            .set("primitiveDouble", "3.14")
            .set("boxedFloat", "3.14")
            .set("primitiveFloat", "3.14")
            .set("uri", "https://example.com")
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Assert.assertEquals("hello", bean.object);
        Assert.assertEquals("hello", bean.string);
        Assert.assertArrayEquals("hello".getBytes(Charset.forName("UTF-8")), bean.bytes);
        Assert.assertEquals(Long.valueOf(42), bean.boxedLong);
        Assert.assertEquals(42L, bean.primitiveLong);
        Assert.assertEquals(Integer.valueOf(42), bean.boxedInteger);
        Assert.assertEquals(42, bean.primitiveInt);
        Assert.assertEquals(Short.valueOf((short)42), bean.boxedShort);
        Assert.assertEquals(42, bean.primitiveShort);
        Assert.assertEquals(Byte.valueOf((byte)42), bean.boxedByte);
        Assert.assertEquals(42, bean.primitiveByte);
        Assert.assertEquals(Double.valueOf(3.14), bean.boxedDouble);
        Assert.assertEquals(3.14, bean.primitiveDouble, 0.0);
        Assert.assertEquals(Float.valueOf(3.14f), bean.boxedFloat);
        Assert.assertEquals(3.14f, bean.primitiveFloat, 0.0f);
        Assert.assertEquals(new URI("https://example.com"), bean.uri);
    }

    @Test
    public void testUnmarshalToObject_List() {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", Arrays.asList(DoubleValue.of(3.14), LongValue.of(42), StringValue.of("hello")))
            .set("list", Arrays.asList(DoubleValue.of(3.14), LongValue.of(42), StringValue.of("hello")))
            .set("linkedList", Arrays.asList(DoubleValue.of(3.14), LongValue.of(42), StringValue.of("hello")))
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();
        bean.linkedList = new LinkedList<Object>();
 
        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Assert.assertThat((List<?>)bean.object, contains(3.14, 42L, "hello"));
        Assert.assertThat(bean.list, contains(3.14, 42L, "hello"));
        Assert.assertThat(bean.linkedList, contains(3.14, 42L, "hello"));
        Assert.assertThat(bean.linkedList, instanceOf(LinkedList.class));
    }

    @Test
    public void testUnmarshalToObject_Map1() {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", Entity.newBuilder().set("k", "v").build())
            .set("map", Entity.newBuilder().set("k", "v").build())
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Map<String, Object> expected = new HashMap<>();
        expected.put("k", "v");
        Assert.assertEquals(expected, bean.object);
        Assert.assertEquals(expected, bean.map);
    }

    @Test
    public void testUnmarshalToObject_Map2() {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object", Entity.newBuilder().set("k1",
                Entity.newBuilder().set("k2", "v2").build()).build())
            .set("map", Entity.newBuilder().set("k1",
                Entity.newBuilder().set("k2", "v2").build()).build())
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("k2", "v2");
        Map<String, Object> expected = new HashMap<>();
        expected.put("k1", innerMap);
        Assert.assertEquals(expected, bean.object);
        Assert.assertEquals(expected, bean.map);
    }

    @Test
    public void testUnmarshalToObject_Timestamp() {
        //Setup
        Key key = Key.newBuilder("project", "kind", 1).build();
        Entity entity = Entity.newBuilder(key)
            .set("object",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("primitiveLong",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("boxedLong",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("date",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("calendar",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("sqlTimestamp",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("localDateTime",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("offsetDateTime",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .set("zonedDateTime",  Timestamp.parseTimestamp("2017-07-09T12:34:56Z"))
            .build();
        Unmarshaller unmarshaller = new Unmarshaller();
        TestBean bean = new TestBean();

        // Exercise
        unmarshaller.unmarshalToObject(entity, bean);

        // Verify
        Assert.assertEquals(
            OffsetDateTime.parse("2017-07-09T12:34:56Z").toInstant(),
            (Instant)bean.object);
        Assert.assertEquals(
            OffsetDateTime.parse("2017-07-09T12:34:56Z").toEpochSecond(),
            bean.primitiveLong);
        Assert.assertEquals(
            Long.valueOf(OffsetDateTime.parse("2017-07-09T12:34:56Z").toEpochSecond()),
            bean.boxedLong);
         Assert.assertEquals(
            Date.from(OffsetDateTime.parse("2017-07-09T12:34:56Z").toInstant()),
            bean.date);
        Assert.assertEquals(
            new Calendar.Builder()
                .setInstant(Date.from(OffsetDateTime.parse("2017-07-09T12:34:56Z").toInstant()))
                .build(),
            bean.calendar);
        Assert.assertEquals(
            java.sql.Timestamp.from(OffsetDateTime.parse("2017-07-09T12:34:56Z").toInstant()),
            bean.sqlTimestamp);
        Assert.assertEquals(
            OffsetDateTime.parse("2017-07-09T12:34:56Z")
                .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
            bean.localDateTime);
        Assert.assertEquals(
            OffsetDateTime.parse("2017-07-09T12:34:56Z"),
            bean.offsetDateTime);
        Assert.assertEquals(
            ZonedDateTime.parse("2017-07-09T12:34:56Z"),
            bean.zonedDateTime);
   }
}
