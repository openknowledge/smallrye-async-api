package io.quarkus.asyncapi.deployment;

import io.smallrye.asyncapi.core.runtime.scanner.AnnotationScannerExtension;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScanner;
import org.jboss.jandex.ClassInfo;

import java.util.Collection;

/**
 * This adds support for the quarkus.http.root-path config option
 */
public class CustomPathExtension implements AnnotationScannerExtension {

  private final String defaultPath;

  public CustomPathExtension(String defaultPath) {
    this.defaultPath = defaultPath;
  }

  @Override
  public void processScannerApplications(AnnotationScanner scanner, Collection<ClassInfo> applications) {
    scanner.setContextRoot(defaultPath);
  }
}
