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
package org.apache.isis.viewer.restfulobjects.tck.resources.home;

import static org.apache.isis.viewer.restfulobjects.tck.RepresentationMatchers.isArray;
import static org.apache.isis.viewer.restfulobjects.tck.RepresentationMatchers.isMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RestfulHttpMethod;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulClient;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulRequest;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulRequest.RequestParameter;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulResponse;
import org.apache.isis.viewer.restfulobjects.applib.homepage.HomePageRepresentation;
import org.apache.isis.viewer.restfulobjects.tck.IsisWebServerRule;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomePageResourceTest_xrofollowlinks {

    @Rule
    public IsisWebServerRule webServerRule = new IsisWebServerRule();

    private RestfulClient client;

    private RestfulRequest request;
    private RestfulResponse<HomePageRepresentation> restfulResponse;
    private HomePageRepresentation repr;

    @Before
    public void setUp() throws Exception {
        client = webServerRule.getClient();

        request = client.createRequest(RestfulHttpMethod.GET, "");
        restfulResponse = request.executeT();
        repr = restfulResponse.getEntity();

        // given
        assertThat(repr.getUser().getValue(), is(nullValue()));
        assertThat(repr.getVersion().getValue(), is(nullValue()));
        assertThat(repr.getServices().getValue(), is(nullValue()));
    }

    @Test
    public void canFollowUser() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=" + Rel.USER.getName() + "]");

        assertThat(repr.getUser().getValue(), is(not(nullValue())));
    }

    @Test
    public void canFollowServices() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=" + Rel.SERVICES.getName() + "]");

        assertThat(repr.getServices().getValue(), is(not(nullValue())));
    }

    @Test
    public void canFollowVersion() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=" + Rel.VERSION.getName() + "]");

        assertThat(repr.getVersion().getValue(), is(not(nullValue())));
    }

    @Test
    public void canFollowAll() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", 
                        "links[rel=" + Rel.USER.getName() + "]," +
        		        "links[rel=" + Rel.SERVICES.getName() + "]," +
        				"links[rel=" + Rel.VERSION.getName() + "]");

        assertThat(repr.getServices().getValue(), is(not(nullValue())));
        assertThat(repr.getUser().getValue(), is(not(nullValue())));
        assertThat(repr.getVersion().getValue(), is(not(nullValue())));
    }

    @Test
    public void servicesValues() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=" + Rel.SERVICES.getName() + "].value");

        final JsonRepresentation servicesValue = repr.getServices().getValue();
        assertThat(servicesValue, is(not(nullValue())));
        assertThat(servicesValue, isMap());
        final JsonRepresentation serviceLinkList = servicesValue.getArray("value");
        assertThat(serviceLinkList, isArray());

        JsonRepresentation service;

        service = serviceLinkList.getRepresentation("[rel=%s;serviceId=\"%s\"]", Rel.SERVICE.getName(), "JdkValuedEntities");
        assertThat(service, isMap());
        assertThat(service.getRepresentation("value"), is(not(nullValue())));

        service = serviceLinkList.getRepresentation("[rel=%s;serviceId=\"%s\"]", Rel.SERVICE.getName(), "WrapperValuedEntities");
        assertThat(service, isMap());
        assertThat(service.getRepresentation("value"), is(not(nullValue())));
    }

    @Test // currently failing
    public void servicesValuesWithCriteria() throws Exception {

        repr = whenExecuteAndFollowLinksUsing("/", "links[rel=" + Rel.SERVICES.getName() + "].value[rel=" + Rel.SERVICE.andParam("serviceId", "WrapperValuedEntities")+"]");

        final JsonRepresentation servicesValue = repr.getServices().getValue();
        assertThat(servicesValue, is(not(nullValue())));
        assertThat(servicesValue, isMap());
        final JsonRepresentation serviceLinkList = servicesValue.getArray("value");
        assertThat(serviceLinkList, isArray());

        JsonRepresentation service;

        service = serviceLinkList.getRepresentation("[rel=%s;serviceId=\"%s\"]", Rel.SERVICE.getName(), "WrapperValuedEntities");
        assertThat(service, isMap());
        assertThat(service.getRepresentation("value"), is(not(nullValue())));

        service = serviceLinkList.getRepresentation("[rel=%s;serviceId=\"%s\"]", Rel.SERVICE.getName(), "JdkValuedEntities");
        assertThat(service.getRepresentation("value"), is(nullValue()));
    }

    private HomePageRepresentation whenExecuteAndFollowLinksUsing(final String uriTemplate, final String followLinks) throws JsonParseException, JsonMappingException, IOException {
        request = client.createRequest(RestfulHttpMethod.GET, uriTemplate).withArg(RequestParameter.FOLLOW_LINKS, followLinks);
        restfulResponse = request.executeT();
        return restfulResponse.getEntity();
    }

}
