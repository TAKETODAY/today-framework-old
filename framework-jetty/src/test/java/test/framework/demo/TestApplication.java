/**
 * Original Author -> 杨海健 (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © TODAY & 2017 - 2020 All Rights Reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/]
 */
package test.framework.demo;

import javax.servlet.http.HttpSession;

import cn.taketoday.context.annotation.Import;
import cn.taketoday.framework.WebApplication;
import cn.taketoday.framework.annotation.PropertiesSource;
import cn.taketoday.framework.server.jetty.JettyServer;
import cn.taketoday.web.annotation.EnableResourceHandling;
import cn.taketoday.web.annotation.GET;
import cn.taketoday.web.annotation.PathVariable;
import cn.taketoday.web.annotation.RestController;
import cn.taketoday.web.config.WebMvcConfiguration;
import cn.taketoday.web.registry.ResourceHandlerRegistry;

/**
 * @author TODAY <br>
 *         2019-06-19 09:58
 */
@Import(JettyServer.class)
@RestController
@EnableResourceHandling
@PropertiesSource("classpath:info.properties")
public class TestApplication implements WebMvcConfiguration {

  public static void main(String[] args) {
    WebApplication.run(TestApplication.class, args);
  }

  @GET("index/{q}")
  public String index(@PathVariable String q, HttpSession httpSession) {
    return q;
  }

  @Override
  public void configureResourceHandler(ResourceHandlerRegistry registry) {

    registry.addResourceMapping("/assets/**")//
            .addLocations("classpath:assets/");

  }

}
