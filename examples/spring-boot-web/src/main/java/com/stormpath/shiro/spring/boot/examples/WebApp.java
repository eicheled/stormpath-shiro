/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.shiro.spring.boot.examples;

import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ControllerAdvice
@SpringBootApplication
public class WebApp { //NOPMD

    private static Logger log = LoggerFactory.getLogger(WebApp.class);

    public static void main(String[] args) {

        SpringApplication.run(WebApp.class, args);
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleException(AuthorizationException e, Model model) {

        log.debug("AuthorizationException was thrown, but setting status to 404, original exception below.", e);

        Map<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.NOT_FOUND.value());
        map.put("message", "No message available");
        model.addAttribute("errors", map);

        return "error";
    }
}
