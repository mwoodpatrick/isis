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

import java.util.List;
import java.util.Map;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectActionParameter;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkFollowSpecs;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;
import org.apache.isis.viewer.restfulobjects.rendering.domaintypes.ActionDescriptionReprRenderer;
import org.codehaus.jackson.node.NullNode;

import com.google.common.collect.Lists;

public class ObjectActionReprRenderer extends AbstractObjectMemberReprRenderer<ObjectActionReprRenderer, ObjectAction> {

    public ObjectActionReprRenderer(final RendererContext resourceContext, final LinkFollowSpecs linkFollower, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, RepresentationType.OBJECT_ACTION, representation, Where.OBJECT_FORMS);
    }

    @Override
    public JsonRepresentation render() {
        // id and memberType are rendered eagerly

        renderMemberContent();
        putDisabledReasonIfDisabled();

        if (mode.isStandalone() || mode.isMutated()) {
            addParameterDetails();
        }

        return representation;
    }

    // ///////////////////////////////////////////////////
    // details link
    // ///////////////////////////////////////////////////

    /**
     * Mandatory hook method to support x-ro-follow-links
     */
    @Override
    protected void followDetailsLink(final JsonRepresentation detailsLink) {
        final ObjectActionReprRenderer renderer = new ObjectActionReprRenderer(getRendererContext(), getLinkFollowSpecs(), JsonRepresentation.newMap());
        renderer.with(new ObjectAndAction(objectAdapter, objectMember)).usingLinkTo(linkTo).asFollowed();
        detailsLink.mapPut("value", renderer.render());
    }

    // ///////////////////////////////////////////////////
    // mutators
    // ///////////////////////////////////////////////////

    @Override
    protected void addMutatorsIfEnabled() {
        if (usability().isVetoed()) {
            return;
        }
        final Map<String, MutatorSpec> mutators = memberType.getMutators();
        
        final ActionSemantics.Of actionSemantics = objectMember.getSemantics();
        final String mutator = InvokeKeys.getKeyFor(actionSemantics);
        final MutatorSpec mutatorSpec = mutators.get(mutator);

        addLinkFor(mutatorSpec);
    }

    @Override
    protected ObjectAdapterLinkTo linkToForMutatorInvoke() {
        if (!objectMember.isContributed()) {
            return super.linkToForMutatorInvoke();
        }
        final DomainServiceLinkTo linkTo = new DomainServiceLinkTo();
        return linkTo.usingUrlBase(getRendererContext()).with(contributingServiceAdapter());
    }

    private ObjectAdapter contributingServiceAdapter() {
        final ObjectSpecification serviceType = objectMember.getOnType();
        final List<ObjectAdapter> serviceAdapters = getServiceAdapters();
        for (final ObjectAdapter serviceAdapter : serviceAdapters) {
            if (serviceAdapter.getSpecification() == serviceType) {
                return serviceAdapter;
            }
        }
        // fail fast
        throw new IllegalStateException("Unable to locate contributing service");
    }

    @Override
    protected JsonRepresentation mutatorArgs(final MutatorSpec mutatorSpec) {
        final JsonRepresentation argMap = JsonRepresentation.newMap();
        final List<ObjectActionParameter> parameters = objectMember.getParameters();
        for (int i = 0; i < objectMember.getParameterCount(); i++) {
            argMap.mapPut(parameters.get(i).getId(), argValueFor(i));
        }
        return argMap;
    }

    private Object argValueFor(final int i) {
        if (objectMember.isContributed()) {
            final ObjectActionParameter actionParameter = objectMember.getParameters().get(i);
            if (actionParameter.getSpecification().isOfType(objectAdapter.getSpecification())) {
                return DomainObjectReprRenderer.newLinkToBuilder(rendererContext, Rel.VALUE, objectAdapter).build();
            }
        }
        // force a null into the map
        return NullNode.getInstance();
    }

    // ///////////////////////////////////////////////////
    // parameter details
    // ///////////////////////////////////////////////////

    private ObjectActionReprRenderer addParameterDetails() {
        final List<Object> parameters = Lists.newArrayList();
        for (int i = 0; i < objectMember.getParameterCount(); i++) {
            final ObjectActionParameter param = objectMember.getParameters().get(i);
            parameters.add(paramDetails(param));
        }
        representation.mapPut("parameters", parameters);
        return this;
    }

    private Object paramDetails(final ObjectActionParameter param) {
        final JsonRepresentation paramRep = JsonRepresentation.newMap();
        paramRep.mapPut("num", param.getNumber());
        paramRep.mapPut("id", param.getId());
        paramRep.mapPut("name", param.getName());
        paramRep.mapPut("description", param.getDescription());
        final Object paramChoices = choicesFor(param);
        if (paramChoices != null) {
            paramRep.mapPut("choices", paramChoices);
        }
        final Object paramDefault = defaultFor(param);
        if (paramDefault != null) {
            paramRep.mapPut("default", paramDefault);
        }
        return paramRep;
    }

    private Object choicesFor(final ObjectActionParameter param) {
        final ObjectAdapter[] choiceAdapters = param.getChoices(objectAdapter);
        if (choiceAdapters == null || choiceAdapters.length == 0) {
            return null;
        }
        final List<Object> list = Lists.newArrayList();
        for (final ObjectAdapter choiceAdapter : choiceAdapters) {
            // REVIEW: previously was using the spec of the parameter, but think instead it should be the spec of the adapter itself
            // final ObjectSpecification choiceSpec = param.getSpecification();
            final ObjectSpecification choiceSpec = choiceAdapter.getSpecification();
            list.add(DomainObjectReprRenderer.valueOrRef(rendererContext, choiceAdapter, choiceSpec));
        }
        return list;
    }

    private Object defaultFor(final ObjectActionParameter param) {
        final ObjectAdapter defaultAdapter = param.getDefault(objectAdapter);
        if (defaultAdapter == null) {
            return null;
        }
        // REVIEW: previously was using the spec of the parameter, but think instead it should be the spec of the adapter itself
        // final ObjectSpecification defaultSpec = param.getSpecification();
        final ObjectSpecification defaultSpec = defaultAdapter.getSpecification();
        return DomainObjectReprRenderer.valueOrRef(rendererContext, defaultAdapter, defaultSpec);
    }

    // ///////////////////////////////////////////////////
    // extensions and links
    // ///////////////////////////////////////////////////

    @Override
    protected void addLinksToFormalDomainModel() {
        getLinks().arrayAdd(ActionDescriptionReprRenderer.newLinkToBuilder(rendererContext, Rel.DESCRIBEDBY, objectAdapter.getSpecification(), objectMember).build());
    }

    @Override
    protected void addLinksIsisProprietary() {
        if (objectMember.isContributed()) {
            final ObjectAdapter serviceAdapter = contributingServiceAdapter();
            final JsonRepresentation contributedByLink = DomainObjectReprRenderer.newLinkToBuilder(rendererContext, Rel.CONTRIBUTED_BY, serviceAdapter).build();
            getLinks().arrayAdd(contributedByLink);
        }
    }

    @Override
    protected void putExtensionsIsisProprietary() {
        getExtensions().mapPut("actionType", objectMember.getType().name().toLowerCase());

        final ActionSemantics.Of semantics = objectMember.getSemantics();
        getExtensions().mapPut("actionSemantics", semantics.getCamelCaseName());
    }

}