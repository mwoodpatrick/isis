/*s
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
package org.apache.isis.viewer.restfulobjects.applib.domainobjects;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.isis.viewer.restfulobjects.applib.RestfulMediaType;
import org.jboss.resteasy.annotations.ClientResponseType;

@Path("/objects")
public interface DomainObjectResource {

    @POST
    @Path("/{domainType}")
    @Consumes({ MediaType.WILDCARD })
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_OBJECT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response persist(@PathParam("domainType") String domainType, final InputStream object);

    // //////////////////////////////////////////////////////////
    // domain object
    // //////////////////////////////////////////////////////////

    @GET
    @Path("/{domainType}/{instanceId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_OBJECT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response object(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId);

    @PUT
    @Path("/{domainType}/{instanceId}")
    @Consumes({ MediaType.WILDCARD })
    @Produces({ RestfulMediaType.APPLICATION_JSON_OBJECT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response object(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, final InputStream arguments);

    // //////////////////////////////////////////////////////////
    // domain object property
    // //////////////////////////////////////////////////////////

    @GET
    @Path("/{domainType}/{instanceId}/properties/{propertyId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_OBJECT_PROPERTY, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response propertyDetails(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("propertyId") final String propertyId);

    @PUT
    @Path("/{domainType}/{instanceId}/properties/{propertyId}")
    @Consumes({ MediaType.WILDCARD })
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response modifyProperty(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("propertyId") final String propertyId, final InputStream arguments);

    @DELETE
    @Path("/{domainType}/{instanceId}/properties/{propertyId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response clearProperty(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("propertyId") final String propertyId);

    // //////////////////////////////////////////////////////////
    // domain object collection
    // //////////////////////////////////////////////////////////

    @GET
    @Path("/{domainType}/{instanceId}/collections/{collectionId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_OBJECT_COLLECTION, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response accessCollection(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("collectionId") final String collectionId);

    @PUT
    @Path("/{domainType}/{instanceId}/collections/{collectionId}")
    @Consumes({ MediaType.WILDCARD })
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ERROR })
    public Response addToSet(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("collectionId") final String collectionId, final InputStream arguments);

    @POST
    @Path("/{domainType}/{instanceId}/collections/{collectionId}")
    @Consumes({ MediaType.WILDCARD })
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response addToList(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("collectionId") final String collectionId, final InputStream arguments);

    @DELETE
    @Path("/{domainType}/{instanceId}/collections/{collectionId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response removeFromCollection(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("collectionId") final String collectionId);

    // //////////////////////////////////////////////////////////
    // domain object action
    // //////////////////////////////////////////////////////////

    @GET
    @Path("/{domainType}/{instanceId}/actions/{actionId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_OBJECT_ACTION, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response actionPrompt(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("actionId") final String actionId);

    // //////////////////////////////////////////////////////////
    // domain object action invoke
    // //////////////////////////////////////////////////////////

    @GET
    @Path("/{domainType}/{instanceId}/actions/{actionId}/invoke")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ACTION_RESULT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response invokeActionQueryOnly(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("actionId") final String actionId);

    @PUT
    @Path("/{domainType}/{instanceId}/actions/{actionId}/invoke")
    @Consumes({ MediaType.WILDCARD })
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ACTION_RESULT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response invokeActionIdempotent(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("actionId") final String actionId, final InputStream arguments);

    @POST
    @Path("/{domainType}/{instanceId}/actions/{actionId}/invoke")
    @Consumes({ MediaType.WILDCARD })
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_ACTION_RESULT, RestfulMediaType.APPLICATION_JSON_ERROR })
    @ClientResponseType(entityType = String.class)
    public Response invokeAction(@PathParam("domainType") String domainType, @PathParam("instanceId") final String instanceId, @PathParam("actionId") final String actionId, final InputStream arguments);

}