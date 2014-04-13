/*
 * Copyright 2014 Webstar Csoport Kft.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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

    public Collection<BaseAttributeGroup> findAttributeGroup(Class componentClass) {
        List<BaseAttributeGroup> result = new LinkedList<>();
        Set<BaseAttributeGroup> referenced = new HashSet<>();
        for (BaseAttributeGroup baseAttributeGroup : attributeGroups) {
            if (baseAttributeGroup.isAppliesTo(componentClass) && !referenced.contains(baseAttributeGroup)) {
                result.add(baseAttributeGroup);
                addReferencedRecursively(referenced, baseAttributeGroup);
            }
        }
        return result;
    }
    
    private void addReferencedRecursively(Collection<BaseAttributeGroup> groups, BaseAttributeGroup attributeGroup) {
        for (QName ref : attributeGroup.getReferences()) {
            BaseAttributeGroup referenced = attributeGroupsByName.get(ref);
            groups.add(referenced);
            addReferencedRecursively(groups, referenced);
        }
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
