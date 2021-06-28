/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeHint;

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 *
 * Native Notes:
 * 1. By default, the native image builder will not integrate any of the resources which are on the
 * classpath during the generation into the final image.
 *
 * 2. Java localization support (java.util.ResourceBundle) enables Java code to load L10N resources and
 * show the right user messages suitable for actual runtime settings like time locale and format, etc.
 *
 * Native Image needs ahead-of-time knowledge of the resource bundles your application needs so that it can
 * load and store the appropriate bundles for usage in the generated binary
 */
@SpringBootApplication
@SerializationHint(types = {
        org.springframework.samples.petclinic.model.BaseEntity.class,
        org.springframework.samples.petclinic.model.Person.class,
        org.springframework.samples.petclinic.vet.Vet.class,
        java.lang.Number.class
})
public class PetClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }

}
