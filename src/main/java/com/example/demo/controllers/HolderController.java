package com.example.demo.controllers;

import com.example.demo.DemoApplication;
import com.example.demo.libraries.Holder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.ArrayList;

@RestController
public class HolderController {

    private Holder getHolder() {
        return DemoApplication.holder;
    }

    @GetMapping("/set")
    public boolean set(HttpServletRequest request) {
        String ttl = request.getParameter("ttl");
        if (ttl != null) {
            long ttlValue;
            try {
                ttlValue = Long.parseLong(ttl);
            } catch (Exception e) {
                return false;
            }

            return getHolder().set(
                    request.getParameter("key"),
                    request.getParameter("value"),
                    ttlValue);
        }

        return getHolder().set(
                request.getParameter("key"),
                request.getParameter("value"));

    }

    @GetMapping("/get")
    public String get(@RequestParam(value = "key") String key) {
        return (String) getHolder().get(key);
    }

    @GetMapping("/remove")
    public boolean remove(@RequestParam(value = "key") String key) {
        return getHolder().remove(key);
    }

    @RequestMapping("/dump")
    public String dump() {
        try {
            Object d = getHolder().dump();
            return "Successfully saved!<br>";
        } catch (Exception e) {
            e.printStackTrace();
            return "Cannot save data!<br>" ;
        }
    }

    @RequestMapping("/load")
    public String load() {
        try {
            getHolder().load();
            return "Successfully loaded!<br>";
        } catch (Exception e) {
            e.printStackTrace();
            return "Cannot load data!<br>" + e.getMessage();
        }
    }

    @RequestMapping("/clear")
    public String clear() {
        getHolder().clearStore();
        return "Store cleared!<br>";
    }

    @RequestMapping("/size")
    public String getSize() {
        return "Size = " + getHolder().size();
    }

    @RequestMapping("/info")
    public String getInfo() {
        return getHolder().toString();
    }

}
