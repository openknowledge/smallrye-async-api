/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package test.io.smallrye.asyncapi.runtime.scanner.resources;

public class Greeting {

  private String greeting;

  private String name;

  public Greeting() {
  }

  public Greeting(final String greeting, final String name) {
    this.greeting = greeting;
    this.name = name;
  }

  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(final String greeting) {
    this.greeting = greeting;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String greet() {
    return this.greeting + ", " + this.name;
  }
}
