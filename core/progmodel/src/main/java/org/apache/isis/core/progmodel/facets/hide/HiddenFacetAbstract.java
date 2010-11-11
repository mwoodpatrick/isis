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


package org.apache.isis.core.progmodel.facets.hide;

import org.apache.isis.applib.events.VisibilityEvent;
import org.apache.isis.core.metamodel.facets.Facet;
import org.apache.isis.core.metamodel.facets.FacetHolder;
import org.apache.isis.core.metamodel.facets.SingleWhenValueFacetAbstract;
import org.apache.isis.core.metamodel.facets.When;
import org.apache.isis.core.metamodel.facets.hide.HiddenFacet;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;


public abstract class HiddenFacetAbstract extends SingleWhenValueFacetAbstract implements HiddenFacet {

    public static Class<? extends Facet> type() {
        return HiddenFacet.class;
    }

    public HiddenFacetAbstract(final When value, final FacetHolder holder) {
        super(type(), holder, value);
    }

    public String hides(final VisibilityContext<? extends VisibilityEvent> ic) {
        return hiddenReason(ic.getTarget());
    }

}