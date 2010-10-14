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


package org.apache.isis.metamodel.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.apache.isis.metamodel.adapter.ResolveState.DESTROYED;
import static org.apache.isis.metamodel.adapter.ResolveState.GHOST;
import static org.apache.isis.metamodel.adapter.ResolveState.NEW;
import static org.apache.isis.metamodel.adapter.ResolveState.PART_RESOLVED;
import static org.apache.isis.metamodel.adapter.ResolveState.RESOLVED;
import static org.apache.isis.metamodel.adapter.ResolveState.RESOLVING;
import static org.apache.isis.metamodel.adapter.ResolveState.RESOLVING_PART;
import static org.apache.isis.metamodel.adapter.ResolveState.SERIALIZING_GHOST;
import static org.apache.isis.metamodel.adapter.ResolveState.SERIALIZING_PART_RESOLVED;
import static org.apache.isis.metamodel.adapter.ResolveState.SERIALIZING_RESOLVED;
import static org.apache.isis.metamodel.adapter.ResolveState.SERIALIZING_TRANSIENT;
import static org.apache.isis.metamodel.adapter.ResolveState.TRANSIENT;
import static org.apache.isis.metamodel.adapter.ResolveState.UPDATING;
import static org.apache.isis.metamodel.adapter.ResolveState.VALUE;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ResolveStateIsTransientTest  {

    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {false, GHOST},
                {false, NEW},
                {false, PART_RESOLVED},
                {false, RESOLVED},
                {false, RESOLVING},
                {false, RESOLVING_PART},
                {true, TRANSIENT},
                {false, DESTROYED},
                {false, UPDATING},
                {true, SERIALIZING_TRANSIENT},
                {false, SERIALIZING_GHOST},
                {false, SERIALIZING_PART_RESOLVED},
                {false, SERIALIZING_RESOLVED},
                {false, VALUE},
        });
    }
    

    private final boolean whetherIs;
    private final ResolveState state;
    
    public ResolveStateIsTransientTest(final boolean whetherIs, final ResolveState state) {
        this.whetherIs = whetherIs;
        this.state = state;
    }

    @Test
    public void testIsTransient() {
        assertThat(state.isTransient(), is(whetherIs));
    }

}