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

package test.io.smallrye.asyncapi.tck;

import java.lang.reflect.Method;

import org.jboss.arquillian.testng.Arquillian;

public class ProxiedTckTest {

    private Arquillian delegate;

    private Object test;

    private Method testMethod;

    private Object[] arguments;

    public static ProxiedTckTest create(Arquillian delegate, Object test, Method testMethod, Object[] arguments) {
        ProxiedTckTest tckTest = new ProxiedTckTest();
        tckTest.delegate = delegate;
        tckTest.test = test;
        tckTest.testMethod = testMethod;
        tckTest.arguments = arguments;
        return tckTest;
    }

    public ProxiedTckTest() {
    }

    /**
     * @return the delegate
     */
    public Arquillian getDelegate() {
        return delegate;
    }

    /**
     * @param delegate the delegate to set
     */
    public void setDelegate(Arquillian delegate) {
        this.delegate = delegate;
    }

    /**
     * @return the testMethod
     */
    public Method getTestMethod() {
        return testMethod;
    }

    /**
     * @param testMethod the testMethod to set
     */
    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    /**
     * @return the test
     */
    public Object getTest() {
        return test;
    }

    /**
     * @param test the test to set
     */
    public void setTest(Object test) {
        this.test = test;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
