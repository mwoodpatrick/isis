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
package org.apache.isis.viewer.restfulobjects.server.resources;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkFollower;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;
import org.apache.isis.viewer.restfulobjects.rendering.ReprRendererAbstract;

public class UserReprRenderer extends ReprRendererAbstract<UserReprRenderer, AuthenticationSession> {

    UserReprRenderer(final RendererContext resourceContext, final LinkFollower linkFollower, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, RepresentationType.USER, representation);
    }

    @Override
    public UserReprRenderer with(final AuthenticationSession authenticationSession) {
        representation.mapPut("userName", authenticationSession.getUserName());
        final JsonRepresentation roles = JsonRepresentation.newArray();
        for (final String role : authenticationSession.getRoles()) {
            roles.arrayAdd(role);
        }
        representation.mapPut("roles", roles);
        return this;
    }

    @Override
    public JsonRepresentation render() {
        if (includesSelf) {
            withSelf("user");
        }
        getExtensions();
        return representation;
    }

}