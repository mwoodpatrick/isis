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

package org.apache.isis.core.metamodel.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.isis.core.metamodel.config.ConfigurationBuilderResourceStreams;
import org.apache.isis.core.metamodel.config.NotFoundPolicy;
import org.junit.Test;

public class ConfigurationBuilderResourceStreamsConfigResourceAndPolicyTest {

    @Test
    public void toStringWhenNull() throws Exception {
        ConfigurationBuilderResourceStreams.ConfigurationResourceAndPolicy configurationResourceAndPolicy =
            new ConfigurationBuilderResourceStreams.ConfigurationResourceAndPolicy(null, null);
        assertThat(configurationResourceAndPolicy.toString(), is("null{null}"));
    }

    @Test
    public void toStringWhenConfigResourceNotNull() throws Exception {
        ConfigurationBuilderResourceStreams.ConfigurationResourceAndPolicy configurationResourceAndPolicy =
            new ConfigurationBuilderResourceStreams.ConfigurationResourceAndPolicy("foo.properties", null);
        assertThat(configurationResourceAndPolicy.toString(), is("foo.properties{null}"));
    }

    @Test
    public void toStringWhenAllSpecified() throws Exception {
        ConfigurationBuilderResourceStreams.ConfigurationResourceAndPolicy configurationResourceAndPolicy =
            new ConfigurationBuilderResourceStreams.ConfigurationResourceAndPolicy("foo.properties",
                NotFoundPolicy.CONTINUE);
        assertThat(configurationResourceAndPolicy.toString(), is("foo.properties{CONTINUE}"));
    }

}