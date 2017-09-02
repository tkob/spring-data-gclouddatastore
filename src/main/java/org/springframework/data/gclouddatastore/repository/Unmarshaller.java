/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gclouddatastore.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.ValueType;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class Unmarshaller {

	public <K extends IncompleteKey> Object unmarshal(
			FullEntity<? extends IncompleteKey> entity) {
		Map<String, Object> newMap = new HashMap<>();
		unmarshalToMap(entity, newMap);
		return newMap;
	}

	public <K extends IncompleteKey, T> T unmarshal(
			FullEntity<? extends IncompleteKey> entity, Class<T> clazz) {
		try {
			T obj = clazz.newInstance();
			unmarshalToObject(entity, obj);
			return obj;
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Value<?> value, Class<T> clazz) {
		return (T) unmarshal(value);
	}

	public Object unmarshal(Value<?> value) {
		ValueType valueType = value.getType();
		switch (valueType) {
		case BLOB:
			return ((BlobValue) value).get().toByteArray();
		case BOOLEAN:
		case DOUBLE:
		case LAT_LNG:
		case LONG:
		case STRING:
			return value.get();
		case ENTITY:
			FullEntity<? extends IncompleteKey> entity = ((EntityValue) value).get();
			return unmarshal(entity);
		case KEY:
			throw new UnsupportedOperationException(valueType.toString());
		case LIST:
			List<Object> newList = new ArrayList<>();
			List<? extends Value<?>> list = ((ListValue) value).get();
			for (Value<?> newValue : list) {
				newList.add(unmarshal(newValue));
			}
			return newList;
		case NULL:
			return null;
		case RAW_VALUE:
			throw new UnsupportedOperationException(valueType.toString());
		case TIMESTAMP:
			return ((TimestampValue) value).get().toSqlTimestamp().toInstant();
		default:
			throw new RuntimeException("should never reach here");
		}
	}

	public <K extends IncompleteKey> void unmarshalToMap(FullEntity<K> entity,
			Map<String, Object> map) {

		for (String name : entity.getNames()) {
			Value<?> value = entity.getValue(name);
			ValueType valueType = value.getType();
			switch (valueType) {
			case ENTITY:
				if (map.containsKey(name)) {
					unmarshalToObject(entity.getEntity(name), map.get(name));
				}
				else {
					Map<String, Object> newMap = new HashMap<>();
					unmarshalToMap(entity.getEntity(name), newMap);
					map.put(name, newMap);
				}
				break;
			case BLOB:
			case BOOLEAN:
			case DOUBLE:
			case LAT_LNG:
			case LIST:
			case LONG:
			case NULL:
			case STRING:
			case TIMESTAMP:
				map.put(name, unmarshal(value));
				break;
			case KEY:
				break;
			case RAW_VALUE:
				throw new UnsupportedOperationException(valueType.toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <K extends IncompleteKey> void unmarshalToObject(FullEntity<K> entity,
			Object object) {

		if (object instanceof Map) {
			unmarshalToMap(entity, (Map<String, Object>) object);
			return;
		}

		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
		for (String name : entity.getNames()) {
			Value<?> value = entity.getValue(name);
			ValueType valueType = value.getType();
			Class<?> targetType = beanWrapper.getPropertyType(name);
			if (targetType == null)
				continue;

			switch (valueType) {
			case BLOB:
				if (targetType.isAssignableFrom(byte[].class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value));
				}
				else if (targetType.isAssignableFrom(String.class)) {
					beanWrapper.setPropertyValue(name, new String(
							unmarshal(value, byte[].class), Charset.forName("UTF-8")));
				}
				break;
			case BOOLEAN:
				if (targetType.isAssignableFrom(Boolean.class)
						|| targetType.isAssignableFrom(boolean.class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value));
				}
				break;
			case DOUBLE:
				if (targetType.isAssignableFrom(Double.class)
						|| targetType.isAssignableFrom(double.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).doubleValue());
				}
				else if (targetType.isAssignableFrom(Float.class)
						|| targetType.isAssignableFrom(float.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).floatValue());
				}
				else if (targetType.isAssignableFrom(Long.class)
						|| targetType.isAssignableFrom(long.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).longValue());
				}
				else if (targetType.isAssignableFrom(Integer.class)
						|| targetType.isAssignableFrom(int.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).intValue());
				}
				else if (targetType.isAssignableFrom(Short.class)
						|| targetType.isAssignableFrom(short.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).shortValue());
				}
				else if (targetType.isAssignableFrom(Byte.class)
						|| targetType.isAssignableFrom(byte.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).byteValue());
				}
				break;
			case LONG:
				if (targetType.isAssignableFrom(Long.class)
						|| targetType.isAssignableFrom(long.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).longValue());
				}
				else if (targetType.isAssignableFrom(Integer.class)
						|| targetType.isAssignableFrom(int.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).intValue());
				}
				else if (targetType.isAssignableFrom(Short.class)
						|| targetType.isAssignableFrom(short.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).shortValue());
				}
				else if (targetType.isAssignableFrom(Byte.class)
						|| targetType.isAssignableFrom(byte.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).byteValue());
				}
				else if (targetType.isAssignableFrom(Double.class)
						|| targetType.isAssignableFrom(double.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).doubleValue());
				}
				else if (targetType.isAssignableFrom(Float.class)
						|| targetType.isAssignableFrom(float.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Number.class).floatValue());
				}
				break;
			case STRING:
				if (targetType.isAssignableFrom(String.class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value));
				}
				else if (targetType.isAssignableFrom(byte[].class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value, String.class)
							.getBytes(Charset.forName("UTF-8")));
				}
				else if (targetType.isAssignableFrom(Long.class)
						|| targetType.isAssignableFrom(long.class)) {
					beanWrapper.setPropertyValue(name,
							Long.decode(unmarshal(value, String.class)));
				}
				else if (targetType.isAssignableFrom(Integer.class)
						|| targetType.isAssignableFrom(int.class)) {
					beanWrapper.setPropertyValue(name,
							Integer.decode(unmarshal(value, String.class)));
				}
				else if (targetType.isAssignableFrom(Short.class)
						|| targetType.isAssignableFrom(short.class)) {
					beanWrapper.setPropertyValue(name,
							Short.decode(unmarshal(value, String.class)));
				}
				else if (targetType.isAssignableFrom(Byte.class)
						|| targetType.isAssignableFrom(byte.class)) {
					beanWrapper.setPropertyValue(name,
							Byte.decode(unmarshal(value, String.class)));
				}
				else if (targetType.isAssignableFrom(Double.class)
						|| targetType.isAssignableFrom(double.class)) {
					beanWrapper.setPropertyValue(name,
							Double.valueOf(unmarshal(value, String.class)));
				}
				else if (targetType.isAssignableFrom(Float.class)
						|| targetType.isAssignableFrom(float.class)) {
					beanWrapper.setPropertyValue(name,
							Float.valueOf(unmarshal(value, String.class)));
				}
				else if (targetType.isAssignableFrom(URI.class)
						|| targetType.isAssignableFrom(float.class)) {
					try {
						beanWrapper.setPropertyValue(name,
								new URI(unmarshal(value, String.class)));
					}
					catch (URISyntaxException e) {
						break;
					}
				}
				break;
			case ENTITY:
				if (targetType.isAssignableFrom(Map.class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value, Map.class));
				}
				else if (Map.class.isAssignableFrom(targetType)) {
					Map<String, Object> map = (Map<String, Object>) beanWrapper
							.getPropertyValue(name);
					if (map == null) {
						try {
							map = (Map<String, Object>) targetType.getConstructor()
									.newInstance();
						}
						catch (ReflectiveOperationException e) {
							break;
						}
					}
					// map.clear();
					unmarshalToMap((FullEntity<?>) value.get(), map);
				}
				else {
					// Bean
					Object targetObject = beanWrapper.getPropertyValue(name);
					if (targetObject == null) {
						try {
							targetObject = targetType.getConstructor().newInstance();
						}
						catch (ReflectiveOperationException e) {
							break;
						}
					}
					unmarshalToObject(((EntityValue) value).get(), targetObject);
				}
				break;
			case KEY:
				break;
			case LAT_LNG:
				if (targetType.isAssignableFrom(LatLng.class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value));
				}
				else if (targetType.isAssignableFrom(com.google.type.LatLng.class)) {
					LatLng latLng = unmarshal(value, LatLng.class);
					beanWrapper.setPropertyValue(name,
							com.google.type.LatLng.newBuilder()
									.setLatitude(latLng.getLatitude())
									.setLongitude(latLng.getLongitude()).build());
				}
				break;
			case LIST:
				if (targetType.isAssignableFrom(List.class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value, List.class));
				}
				else if (List.class.isAssignableFrom(targetType)) {
					List<Object> newList = (List<Object>) beanWrapper
							.getPropertyValue(name);
					if (newList == null) {
						try {
							newList = (List<Object>) targetType.getConstructor()
									.newInstance();
						}
						catch (ReflectiveOperationException e) {
							break;
						}
					}
					newList.clear();
					for (Object newValue : unmarshal(value, List.class)) {
						newList.add(newValue);
					}
				}
				break;
			case NULL:
				if (Object.class.isAssignableFrom(targetType)) {
					beanWrapper.setPropertyValue(name, null);
				}
				break;
			case RAW_VALUE:
				break;
			case TIMESTAMP:
				if (targetType.isAssignableFrom(Instant.class)) {
					beanWrapper.setPropertyValue(name, unmarshal(value, Instant.class));
				}
				else if (targetType.isAssignableFrom(Date.class)) {
					beanWrapper.setPropertyValue(name,
							Date.from(unmarshal(value, Instant.class)));
				}
				else if (targetType.isAssignableFrom(Calendar.class)) {
					beanWrapper.setPropertyValue(name,
							new Calendar.Builder()
									.setInstant(
											Date.from(unmarshal(value, Instant.class)))
									.build());
				}
				else if (targetType.isAssignableFrom(java.sql.Timestamp.class)) {
					beanWrapper.setPropertyValue(name,
							((TimestampValue) value).get().toSqlTimestamp());
				}
				else if (targetType.isAssignableFrom(LocalDateTime.class)) {
					beanWrapper.setPropertyValue(name, ((TimestampValue) value).get()
							.toSqlTimestamp().toLocalDateTime());
				}
				else if (targetType.isAssignableFrom(OffsetDateTime.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Instant.class).atOffset(ZoneOffset.UTC));
				}
				else if (targetType.isAssignableFrom(ZonedDateTime.class)) {
					beanWrapper.setPropertyValue(name,
							unmarshal(value, Instant.class).atZone(ZoneOffset.UTC));
				}
				else if (targetType.isAssignableFrom(long.class)
						|| targetType.isAssignableFrom(Long.class)) {
					beanWrapper.setPropertyValue(name,
							((TimestampValue) value).get().getSeconds());
				}
				break;
			}
		}
	}
}
