/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.viewer.restfulobjects.rendering.domainobjects;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.services.ServiceUtil;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkBuilder;

public class DomainServiceLinkTo extends DomainObjectLinkTo {
    private String serviceId;

    @Override
    public ObjectAdapterLinkTo with(final ObjectAdapter objectAdapter) {
        serviceId = ServiceUtil.id(objectAdapter.getObject());
        return super.with(objectAdapter);
    }

    @Override
    public LinkBuilder builder(final Rel rel) {
        final LinkBuilder linkBuilder = LinkBuilder.newBuilder(rendererContext, 
                relElseDefault(rel).andParam("serviceId", serviceId), 
                RepresentationType.DOMAIN_OBJECT, 
                linkRef(new StringBuilder()).toString());
        linkBuilder.withTitle(objectAdapter.titleString());
        return linkBuilder;
    }


    @Override
    protected StringBuilder linkRef(StringBuilder buf) {
        return buf.append("services/").append(serviceId);
    }

    @Override
    protected Rel defaultRel() {
        return Rel.SERVICE;
    }


}