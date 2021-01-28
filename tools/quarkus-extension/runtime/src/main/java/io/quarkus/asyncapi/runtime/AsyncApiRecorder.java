package io.quarkus.asyncapi.runtime;

import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class AsyncApiRecorder {

    public void setupClDevMode(ShutdownContext shutdownContext) {
        AsyncApiConstants.classLoader = Thread.currentThread().getContextClassLoader();
        shutdownContext.addShutdownTask(() -> AsyncApiConstants.classLoader = null);
    }

}
