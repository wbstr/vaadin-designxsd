/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.baseattributegroup;

import javax.xml.namespace.QName;
import java.util.*;

public class BaseAttributeGroupMngr {

    private final List<BaseAttributeGroup> attributeGroups;
    private final Map<QName, BaseAttributeGroup> attributeGroupsByName;

    public BaseAttributeGroupMngr(BaseSchema... baseSchemas) {
        attributeGroupsByName = new HashMap<>();
        for (BaseSchema baseSchema : baseSchemas) {
            attributeGroupsByName.putAll(baseSchema.getAttrGroupsByName());
        }
        attributeGroups = computeBaseAttributeGroupList();
    }

    private List<BaseAttributeGroup> computeBaseAttributeGroupList() {
        Set<QName> storedAttrGroupNames = new HashSet<>();
        LinkedList<BaseAttributeGroup> result = new LinkedList<>();
        Map<QName, BaseAttributeGroup> allByName = new HashMap<>(attributeGroupsByName);
        while (!allByName.isEmpty()) {
            int beforeSize = storedAttrGroupNames.size();
            Iterator<Map.Entry<QName, BaseAttributeGroup>> iterator = allByName.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<QName, BaseAttributeGroup> entry = iterator.next();
                BaseAttributeGroup group = entry.getValue();
                HashSet<QName> notStoredReference = new HashSet<>(group.getReferences());
                notStoredReference.removeAll(storedAttrGroupNames);
                if (notStoredReference.isEmpty()) {
                    result.addFirst(group);
                    iterator.remove();
                    storedAttrGroupNames.add(entry.getKey());
                }
            }
            if (beforeSize == storedAttrGroupNames.size() && !allByName.isEmpty()) {
                throw new RuntimeException("Loop detected between attributeGroups!");
            }
        }
        return result;
    }

    public BaseAttributeGroup findAttributeGroup(Class componentClass) {
        for (BaseAttributeGroup baseAttributeGroup : attributeGroups) {
            if (baseAttributeGroup.isAppliesTo(componentClass)) {
                return baseAttributeGroup;
            }
        }
        return null;
    }

    public boolean isAttributeInherited(BaseAttributeGroup attributeGroup, String attribute) {
        if (attributeGroup == null) {
            return false;
        }
        if (attributeGroup.hasAttribute(attribute)) {
            return true;
        }
        for (QName ref : attributeGroup.getReferences()) {
            BaseAttributeGroup referenced = attributeGroupsByName.get(ref);
            if (isAttributeInherited(referenced, attribute)) {
                return true;
            }
        }
        return false;
    }

}
