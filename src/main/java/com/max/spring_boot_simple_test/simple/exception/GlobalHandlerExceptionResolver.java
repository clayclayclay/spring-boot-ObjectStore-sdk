package com.max.spring_boot_simple_test.simple.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class GlobalHandlerExceptionResolver implements HandlerExceptionResolver {

    private static final Logger log = Logger.getLogger(GlobalHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        ModelAndView mav = new ModelAndView();
        MappingJackson2JsonView json = new MappingJackson2JsonView();
        json.addStaticAttribute("status", false);
        json.addStaticAttribute("message", ex.getMessage());
        mav.setView(json);
        return mav;
    }
}
