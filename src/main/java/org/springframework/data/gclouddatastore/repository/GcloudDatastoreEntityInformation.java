package org.springframework.data.gclouddatastore.repository;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.AbstractEntityInformation;

public class GcloudDatastoreEntityInformation<T, ID extends Serializable>
    extends AbstractEntityInformation<T, ID> {

    public GcloudDatastoreEntityInformation(Class<T> domainClass) {
        super(domainClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ID getId(T entity) {
        Class<?> domainClass = getJavaType();
        while (domainClass != Object.class) {
            for (Field field : domainClass.getDeclaredFields()) {
                if (field.getAnnotation(Id.class) != null) {
                    try {
                        return (ID)field.get(entity);
                    } catch (IllegalArgumentException
                        | IllegalAccessException e) {
                        BeanWrapper beanWrapper =
                            PropertyAccessorFactory
                                .forBeanPropertyAccess(entity);
                        return (ID)beanWrapper.getPropertyValue(
                            field.getName());
                    }
                }
            }
            domainClass = domainClass.getSuperclass();
        }
        throw new IllegalStateException("id not found");
    }

    @Override
    public Class<ID> getIdType() {
        Class<?> domainClass = getJavaType();
        while (domainClass != Object.class) {
            for (Field field : domainClass.getDeclaredFields()) {
                if (field.getAnnotation(Id.class) != null) {
                    return (Class<ID>)field.getType();
                }
            }
            domainClass = domainClass.getSuperclass();
        }
        throw new IllegalStateException("id not found");
    }
 }
