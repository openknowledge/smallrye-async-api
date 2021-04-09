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
package io.smallrye.asyncapi.tck.schema;

import io.smallrye.asyncapi.spec.annotations.AsyncAPI;
import io.smallrye.asyncapi.spec.annotations.info.Info;
import io.smallrye.asyncapi.spec.annotations.schema.Schema;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;

import java.util.Date;
import java.util.List;

@AsyncAPI(asyncapi = "2.0.0",
    info = @Info(title = "SchemaService",
        version = "1.0.0"),
    defaultContentType = "application/json")
public class SchemaService {

  @Schema
  private class Hello {

    private String name;

    private Date date;

    @Schema(description = "A list of customer")
    private List<Customer> customers;

    public Hello(final String name, final Date date) {
      this.name = name;
      this.date = date;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(final Date date) {
      this.date = date;
    }
  }

  @Schema
  private class Customer {
    @Schema(example = "342-513-214", type = SchemaType.NUMBER)
    private Long id;

    private String name;

    public Customer(final Long id, final String name) {
      this.id = id;
      this.name = name;
    }

    public Long getId() {
      return id;
    }

    public void setId(final Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }
  }
}
