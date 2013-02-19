/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.restfulobjects.rendering;

import java.util.List;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.transaction.UpdateNotifier;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.domainobjects.DomainObjectReprRenderer;
import org.apache.isis.viewer.restfulobjects.rendering.domaintypes.DomainTypeReprRenderer;

public abstract class ReprRendererAbstract<R extends ReprRendererAbstract<R, T>, T> implements ReprRenderer<R, T> {

    protected final RendererContext rendererContext;
    private final LinkFollower linkFollower;
    private final RepresentationType representationType;
    protected final JsonRepresentation representation;

    protected boolean includesSelf;

    public ReprRendererAbstract(final RendererContext rendererContext, final LinkFollower linkFollower, final RepresentationType representationType, final JsonRepresentation representation) {
        this.rendererContext = rendererContext;
        this.linkFollower = asProvidedElseCreate(linkFollower);
        this.representationType = representationType;
        this.representation = representation;
    }

    public RendererContext getRendererContext() {
        return rendererContext;
    }

    public LinkFollower getLinkFollower() {
        return linkFollower;
    }

    private LinkFollower asProvidedElseCreate(final LinkFollower linkFollower) {
        if (linkFollower != null) {
            return linkFollower;
        }
        return LinkFollower.create(rendererContext.getFollowLinks());
    }

    @Override
    public RepresentationType getRepresentationType() {
        return representationType;
    }

    @SuppressWarnings("unchecked")
    public R includesSelf() {
        this.includesSelf = true;
        return (R) this;
    }

    public R withSelf(final String href) {
        if (href != null) {
            getLinks().arrayAdd(LinkBuilder.newBuilder(rendererContext, Rel.SELF.getName(), representationType, href).build());
        }
        return cast(this);
    }

    public R withSelf(final JsonRepresentation link) {
        final String rel = link.getString("rel");
        if (rel == null || !rel.equals(Rel.SELF.getName())) {
            throw new IllegalArgumentException("Provided link does not have a 'rel' of 'self'; was: " + link);
        }
        if (link != null) {
            getLinks().arrayAdd(link);
        }
        return cast(this);
    }

    /**
     * Will lazily create links array as required
     */
    protected JsonRepresentation getLinks() {
        JsonRepresentation links = representation.getArray("links");
        if (links == null) {
            links = JsonRepresentation.newArray();
            representation.mapPut("links", links);
        }
        return links;
    }

    protected void addLink(final Rel rel, final ObjectSpecification objectSpec) {
        if (objectSpec == null) {
            return;
        }
        final LinkBuilder linkBuilder = DomainTypeReprRenderer.newLinkToBuilder(getRendererContext(), rel, objectSpec);
        getLinks().arrayAdd(linkBuilder.build());
    }

    /**
     * Will lazily create extensions map as required
     */
    protected JsonRepresentation getExtensions() {
        JsonRepresentation extensions = representation.getMap("extensions");
        if (extensions == null) {
            extensions = JsonRepresentation.newMap();
            representation.mapPut("extensions", extensions);
        }
        return extensions;
    }

    public R withExtensions(final JsonRepresentation extensions) {
        if (!extensions.isMap()) {
            throw new IllegalArgumentException("extensions must be a map");
        }
        representation.mapPut("extensions", extensions);
        return cast(this);
    }

    @SuppressWarnings("unchecked")
    protected static <R extends ReprRendererAbstract<R, T>, T> R cast(final ReprRendererAbstract<R, T> builder) {
        return (R) builder;
    }

    @Override
    public abstract JsonRepresentation render();

    /**
     * Convenience for representations that are returned from objects that
     * mutate state.
     */
    protected final void addExtensionsIsisProprietaryChangedObjects() {
        final UpdateNotifier updateNotifier = getUpdateNotifier();

        addToExtensions("changed", updateNotifier.getChangedObjects());
        addToExtensions("disposed", updateNotifier.getDisposedObjects());
    }

    private void addToExtensions(final String key, final List<ObjectAdapter> adapters) {
        final JsonRepresentation adapterList = JsonRepresentation.newArray();
        getExtensions().mapPut(key, adapterList);
        for (final ObjectAdapter adapter : adapters) {
            adapterList.arrayAdd(DomainObjectReprRenderer.newLinkToBuilder(getRendererContext(), Rel.VALUE, adapter).build());
        }
    }

    protected List<ObjectAdapter> getServiceAdapters() {
        return IsisContext.getPersistenceSession().getServices();
    }

    protected AuthenticationSession getAuthenticationSession() {
        return IsisContext.getAuthenticationSession();
    }

    protected UpdateNotifier getUpdateNotifier() {
        return IsisContext.getCurrentTransaction().getUpdateNotifier();
    }

}
