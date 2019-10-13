/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.metrics.jmx;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

import org.slf4j.Logger;

import com.github.noraui.log.annotation.Loggable;

@Loggable
public class TimedJmxDynamicMBean implements DynamicMBean {

    static Logger log;

    private final ConcurrentMap<String, Long> value = new ConcurrentHashMap<>();

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return value.get(attribute);
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributs = new AttributeList();
        for (Entry<String, Long> entry : value.entrySet()) {
            attributs.add(new Attribute(entry.getKey(), entry.getValue()));
        }
        return attributs;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        MBeanParameterInfo[] withoutParamInfo = new MBeanParameterInfo[0];

        MBeanAttributeInfo attributs[] = new MBeanAttributeInfo[value.size()];
        int i = 0;
        for (Entry<String, Long> entry : value.entrySet()) {
            attributs[i] = new MBeanAttributeInfo(entry.getKey(), "long", "Timed of " + entry.getKey(), true, true, false);
            i++;
        }

        MBeanConstructorInfo[] constructeurs = new MBeanConstructorInfo[1];
        constructeurs[0] = new MBeanConstructorInfo("TimedJmxDynamicMBean", "Constructor by default", withoutParamInfo);

        MBeanOperationInfo[] operations = new MBeanOperationInfo[1];
        operations[0] = new MBeanOperationInfo("refresh", "Refresh data", withoutParamInfo, void.class.getName(), MBeanOperationInfo.ACTION);

        return new MBeanInfo(getClass().getName(), "TimedJmxDynamicMBean", attributs, constructeurs, operations, null);
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        try {
            if ("refresh".equals(actionName)) {
                refresh();
            }
            return null;
        } catch (Exception x) {
            throw new MBeanException(x);
        }
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        String name = attribute.getName();
        try {
            value.put(name, (Long) attribute.getValue());
        } catch (ClassCastException cce) {
            throw new InvalidAttributeValueException(name);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeList setAttributes(AttributeList attributes) {
        for (Object element : attributes) {
            Attribute attr = (Attribute) element;
            try {
                setAttribute(attr);
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidAttributeValueException e) {
                e.printStackTrace();
            } catch (MBeanException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {
                e.printStackTrace();
            }
        }
        return attributes;
    }

    private void refresh() {
        log.info("Refresh Data");
    }

}
