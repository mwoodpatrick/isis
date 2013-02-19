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
package org.apache.isis.viewer.restfulobjects.rendering.domaintypes;

import java.util.List;

import org.apache.isis.core.metamodel.facets.typeof.TypeOfFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectActionParameter;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkBuilder;
import org.apache.isis.viewer.restfulobjects.rendering.LinkFollowSpecs;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;

public class ActionDescriptionReprRenderer extends AbstractTypeMemberReprRenderer<ActionDescriptionReprRenderer, ObjectAction> {

    public static LinkBuilder newLinkToBuilder(final RendererContext resourceContext, final Rel rel, final ObjectSpecification objectSpecification, final ObjectAction objectAction) {
        final String typeFullName = objectSpecification.getFullIdentifier();
        final String actionId = objectAction.getId();
        final String url = "domainTypes/" + typeFullName + "/actions/" + actionId;
        return LinkBuilder.newBuilder(resourceContext, rel.getName(), RepresentationType.ACTION_DESCRIPTION, url);
    }

    public ActionDescriptionReprRenderer(final RendererContext resourceContext, final LinkFollowSpecs linkFollower, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, RepresentationType.ACTION_DESCRIPTION, representation);
    }

    @Override
    protected void addLinksSpecificToFeature() {
        addParameters();
        addLinkToReturnTypeIfAny();
        addLinkToElementTypeIfAny();
    }

    private void addParameters() {
        final JsonRepresentation parameterList = JsonRepresentation.newArray();
        final List<ObjectActionParameter> parameters = getObjectFeature().getParameters();
        for (final ObjectActionParameter parameter : parameters) {
            final LinkBuilder linkBuilder = ActionParameterDescriptionReprRenderer.newLinkToBuilder(getRendererContext(), Rel.ACTION_PARAM, objectSpecification, parameter);
            parameterList.arrayAdd(linkBuilder.build());
        }

        representation.mapPut("parameters", parameterList);
    }

    protected void addLinkToElementTypeIfAny() {
        final TypeOfFacet facet = getObjectFeature().getFacet(TypeOfFacet.class);
        if (facet == null) {
            return;
        }
        final ObjectSpecification typeOfSpec = facet.valueSpec();
        final LinkBuilder linkBuilder = DomainTypeReprRenderer.newLinkToBuilder(getRendererContext(), Rel.ELEMENT_TYPE, typeOfSpec);
        getLinks().arrayAdd(linkBuilder.build());
    }

    private void addLinkToReturnTypeIfAny() {
        final ObjectSpecification returnType = getObjectFeature().getReturnType();
        if (returnType == null) {
            return;
        }
        final LinkBuilder linkBuilder = DomainTypeReprRenderer.newLinkToBuilder(getRendererContext(), Rel.RETURN_TYPE, returnType);
        getLinks().arrayAdd(linkBuilder.build());
    }

    @Override
    protected void putExtensionsSpecificToFeature() {
        putExtensionsName();
        putExtensionsDescriptionIfAvailable();
    }

}