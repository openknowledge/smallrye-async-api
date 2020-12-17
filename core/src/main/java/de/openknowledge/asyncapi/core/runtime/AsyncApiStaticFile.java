package de.openknowledge.asyncapi.core.runtime;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import de.openknowledge.asyncapi.core.runtime.io.Format;

public class AsyncApiStaticFile implements Closeable {

    private Format format;

    private InputStream content;

    public AsyncApiStaticFile() {
    }

    public AsyncApiStaticFile(InputStream content, Format format) {
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
    public Format getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(Format format) {
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
