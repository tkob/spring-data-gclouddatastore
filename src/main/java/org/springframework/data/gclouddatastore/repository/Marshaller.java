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

import java.beans.PropertyDescriptor;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class Marshaller {

	private Value<?> toDatastoreValue(Object value) {
		if (value == null) {
			return NullValue.of();
		}
		else if (value instanceof String || value instanceof URI) {
			return StringValue.of(value.toString());
		}
		else if (value instanceof Boolean) {
			return BooleanValue.of(((Boolean) value).booleanValue());
		}
		else if (value instanceof Float) {
			return DoubleValue.of(((Float) value).doubleValue());
		}
		else if (value instanceof Double) {
			return DoubleValue.of(((Double) value).doubleValue());
		}
		else if (value instanceof Integer) {
			return LongValue.of(((Integer) value).longValue());
		}
		else if (value instanceof Long) {
			return LongValue.of(((Long) value).longValue());
		}
		else if (value instanceof byte[]) {
			return BlobValue.of(Blob.copyFrom((byte[]) value));
		}
		else if (value instanceof ByteBuffer) {
			return BlobValue.of(Blob.copyFrom((ByteBuffer) value));
		}
		else if (value instanceof Date) {
			return TimestampValue.of(Timestamp.of((Date) value));
		}
		else if (value instanceof java.sql.Timestamp) {
			return TimestampValue.of(Timestamp.of((java.sql.Timestamp) value));
		}
		else if (value instanceof com.google.protobuf.Timestamp) {
			return TimestampValue
					.of(Timestamp.fromProto((com.google.protobuf.Timestamp) value));
		}
		else if (value instanceof Calendar) {
			String rfc3339 = OffsetDateTime
					.ofInstant(((Calendar) value).toInstant(), ZoneOffset.UTC).toString();
			return TimestampValue.of(Timestamp.parseTimestamp(rfc3339));
		}
		else if (value instanceof OffsetDateTime) {
			String rfc3339 = ((OffsetDateTime) value)
					.withOffsetSameInstant(ZoneOffset.UTC).toString();
			return TimestampValue.of(Timestamp.parseTimestamp(rfc3339));
		}
		else if (value instanceof Map) {
			return EntityValue.of(toEntity(value, null));
		}
		else if (value instanceof List) {
			ListValue.Builder builder = ListValue.newBuilder();
			for (Object e : (List<?>) value) {
				builder.addValue(toDatastoreValue(e));
			}
			return builder.build();
		}
		else {
			return EntityValue.of(toEntity(value, null));
		}
	}

	@SuppressWarnings("unchecked")
	public FullEntity<? extends IncompleteKey> toEntity(Object object, Key key) {
		FullEntity.Builder<? extends IncompleteKey> builder;
		if (key == null) {
			builder = Entity.newBuilder();
		}
		else {
			builder = FullEntity.newBuilder(key);
		}

		if (object instanceof Map) {
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) object)
					.entrySet()) {
				setEntityValue(builder, entry.getKey(), entry.getValue());
			}
		}
		else {
			BeanWrapper beanWrapper = PropertyAccessorFactory
					.forBeanPropertyAccess(object);
			for (PropertyDescriptor propertyDescriptor : beanWrapper
					.getPropertyDescriptors()) {
				String name = propertyDescriptor.getName();
				if ("class".equals(name))
					continue;
				setEntityValue(builder, name, beanWrapper.getPropertyValue(name));
			}
		}
		return builder.build();
	}

	private void setEntityValue(FullEntity.Builder<? extends IncompleteKey> builder,
			String name, Object value) {
		builder.set(name, toDatastoreValue(value));
	}
}
