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
package io.smallrye.asyncapi.core.runtime;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class AsyncApiStaticFile implements Closeable {

    private AsyncApiFormat format;

    private InputStream content;

    public AsyncApiStaticFile() {
    }

    public AsyncApiStaticFile(InputStream content, AsyncApiFormat format) {
        this.content = content;
        this.format = format;
    }

    /**
     * @see Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (this.getContent() != null) {
            this.getContent()
                    .close();
        }
    }

    /**
     * @return the format
     */
    public AsyncApiFormat getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(AsyncApiFormat format) {
        this.format = format;
    }

    /**
     * @return the content
     */
    public InputStream getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(InputStream content) {
        this.content = content;
    }
}
