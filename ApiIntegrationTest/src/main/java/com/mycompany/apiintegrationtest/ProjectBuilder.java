
package com.mycompany.apiintegrationtest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ProjectBuilder {
    
    private String testConfig = "http://localhost:4569/test-configurations";
    
    public void start(String filename) {
        Map<String,String> map = new HashMap<>();
        map.put("TESTING_API", "TRUE");
        setEnv(map);
        try {
            Process process = new ProcessBuilder(filename, "").start();
            Thread.sleep(1000);
            process.destroy();
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        }
    }
    
    private void setEnv(Map<String, String> newenv) {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        }
        catch (NoSuchFieldException e) {
          try {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for(Class cl : classes) {
                if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
          } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e2) {
            e2.printStackTrace();
          }
        } catch (ClassNotFoundException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    public String getTestConfig() {
        return testConfig;
    }
}
