package com.conduent.ts.ops.restapi.resources;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@HttpMethod("PATCH")
public @interface PATCH {

}
