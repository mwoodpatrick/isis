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

import java.util.Collection;
import java.util.Map;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionFacet;
import org.apache.isis.core.metamodel.facets.object.encodeable.EncodableFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.ActionResultRepresentation.ResultType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkBuilder;
import org.apache.isis.viewer.restfulobjects.rendering.LinkFollowSpecs;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;
import org.apache.isis.viewer.restfulobjects.rendering.ReprRendererAbstract;

public class ActionResultReprRenderer extends ReprRendererAbstract<ActionResultReprRenderer, ObjectAndActionInvocation> {

    private ObjectAdapterLinkTo adapterLinkTo = new DomainObjectLinkTo();

    private ObjectAdapter objectAdapter;
    private ObjectAction action;
    private JsonRepresentation arguments;
    private ObjectAdapter returnedAdapter;

    public ActionResultReprRenderer(final RendererContext resourceContext, final LinkFollowSpecs linkFollower, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, RepresentationType.ACTION_RESULT, representation);
    }

    @Override
    public ActionResultReprRenderer with(final ObjectAndActionInvocation objectAndActionInvocation) {

        objectAdapter = objectAndActionInvocation.getObjectAdapter();
        action = objectAndActionInvocation.getAction();
        arguments = objectAndActionInvocation.getArguments();
        returnedAdapter = objectAndActionInvocation.getReturnedAdapter();

        adapterLinkTo.with(returnedAdapter);

        return this;
    }

    public void using(final ObjectAdapterLinkTo adapterLinkTo) {
        this.adapterLinkTo = adapterLinkTo.with(objectAdapter);
    }

    @Override
    public JsonRepresentation render() {

        representationWithSelfFor(action, arguments);

        addResult(representation);

        addExtensionsIsisProprietaryChangedObjects();

        return representation;
    }

    private void addResult(final JsonRepresentation representation) {
        final JsonRepresentation result = JsonRepresentation.newMap();
        final ResultType resultType = addResultTo(result);

        if (!resultType.isVoid()) {
            putResultType(representation, resultType);
            representation.mapPut("result", result);
        }
    }

    private ResultType addResultTo(final JsonRepresentation result) {

        final ObjectSpecification returnType = this.action.getReturnType();

        if (returnType.getCorrespondingClass() == void.class) {
            // void
            return ResultType.VOID;
        }

        final CollectionFacet collectionFacet = returnType.getFacet(CollectionFacet.class);
        if (collectionFacet != null) {
            // collection

            final Collection<ObjectAdapter> collectionAdapters = collectionFacet.collection(returnedAdapter);

            final ListReprRenderer renderer = new ListReprRenderer(rendererContext, null, result);
            renderer.with(collectionAdapters).withReturnType(action.getReturnType()).withElementType(returnedAdapter.getElementSpecification());

            renderer.render();
            return ResultType.LIST;
        }

        final EncodableFacet encodableFacet = returnType.getFacet(EncodableFacet.class);
        if (encodableFacet != null) {
            // scalar

            final ScalarValueReprRenderer renderer = new ScalarValueReprRenderer(rendererContext, null, result);
            renderer.with(returnedAdapter).withReturnType(action.getReturnType());

            renderer.render();
            return ResultType.SCALAR_VALUE;

        }

        {
            // object
            final DomainObjectReprRenderer renderer = new DomainObjectReprRenderer(rendererContext, null, result);

            renderer.with(returnedAdapter).includesSelf();

            renderer.render();
            return ResultType.DOMAIN_OBJECT;
        }
    }

    private void putResultType(final JsonRepresentation representation, final ResultType resultType) {
        representation.mapPut("resulttype", resultType.getValue());
    }

    private JsonRepresentation representationWithSelfFor(final ObjectAction action, final JsonRepresentation bodyArgs) {
        final JsonRepresentation links = JsonRepresentation.newArray();
        representation.mapPut("links", links);

        final LinkBuilder selfLinkBuilder = adapterLinkTo.memberBuilder(Rel.SELF, MemberType.ACTION, action, RepresentationType.ACTION_RESULT, "invoke");

        // TODO: remove duplication with AbstractObjectMember#addLinkTo
        final MemberType memberType = MemberType.of(action);
        final Map<String, MutatorSpec> mutators = memberType.getMutators();

        final String mutator = InvokeKeys.getKeyFor(action.getSemantics());
        final MutatorSpec mutatorSpec = mutators.get(mutator);
        selfLinkBuilder.withHttpMethod(mutatorSpec.httpMethod);

        final JsonRepresentation selfLink = selfLinkBuilder.build();

        links.arrayAdd(selfLink);
        selfLink.mapPut("args", bodyArgs);
        return representation;
    }

}