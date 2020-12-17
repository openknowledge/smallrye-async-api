/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.asyncapi.apps.filter;

import io.apicurio.datamodels.Library;
import io.apicurio.datamodels.combined.visitors.CombinedVisitorAdapter;
import io.apicurio.datamodels.core.models.Document;
import io.apicurio.datamodels.core.models.common.Info;
import io.apicurio.datamodels.core.models.common.Server;
import io.apicurio.datamodels.core.visitors.TraverserDirection;
import io.smallrye.asyncapi.spec.AAIFilter;

/**
 * A filter implementation used in the filter app TCK test.
 * 
 * @author eric.wittmann@gmail.com
 */
public class FilterImpl extends CombinedVisitorAdapter implements AAIFilter {

    /**
     * @see io.smallrye.asyncapi.spec.AAIFilter#filterDocument(io.apicurio.datamodels.core.models.Document)
     */
    @Override
    public void filterDocument(Document document) {
        Library.visitTree(document, this, TraverserDirection.down);
    }

    /**
     * @see io.apicurio.datamodels.combined.visitors.CombinedVisitorAdapter#visitInfo(io.apicurio.datamodels.core.models.common.Info)
     */
    @Override
    public void visitInfo(Info node) {
        node.title = "Filter API";
        node.version = "1.0.42";
        node.description = "An API definition filtered by a filter implementation.";
    }

    /**
     * @see io.apicurio.datamodels.combined.visitors.CombinedVisitorAdapter#visitServer(io.apicurio.datamodels.core.models.common.Server)
     */
    @Override
    public void visitServer(Server node) {
        node.url = node.url + "-filtered";
    }

}
