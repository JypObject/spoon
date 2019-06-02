package com.huadong.spoon.customer;

import com.google.common.collect.Lists;
import com.huadong.spoon.annotation.SpoonMessage;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 1、根据指定的路径扫描所有的类
 * 2、拿到所有的添加了@CustomerMessageCustomer注解的类
 * @author jinjinhui
 * @date 2019/3/7
 * @see SpoonMessage
 * @see SpoonMessageCustomer
 * @see CustomerRegistrar
 */
public class CustomerScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerScanner.class);
    private static final String CLASSFILE_NAME_SUFFIX = ".class";

    public static void doScan(List<String> basePackages){
        if(CollectionUtils.isEmpty(basePackages)){
            return;
        }
        //定义一个class的列表
        List<Class<?>> classes = Lists.newLinkedList();
        for(String packageName : basePackages){
            classes.addAll(getClassesByPackageName(packageName));
        }
        for (Class<?> clazz : classes) {
            //循环获取所有的类
            if(!clazz.isAnnotationPresent(SpoonMessage.class)){
                continue;
            }
            if(!SpoonMessageCustomer.class.isAssignableFrom(clazz)){
                continue;
            }
            registerMnsMessageCallbackByClass(clazz);
        }
    }

    /**
     * 根据class注册MnsMessageCallback
     * @param clazz
     */
    protected static void registerMnsMessageCallbackByClass(Class<?> clazz) {
        SpoonMessage mnsMessage = clazz.getAnnotation(SpoonMessage.class);
        short[] messageTypes = mnsMessage.messageTypes();
        try {
            CustomerManager.registerSpoonMessageCustomer((SpoonMessageCustomer) clazz.newInstance(), messageTypes);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected static  List<Class<?>> getClassesByPackageName(String packageName){
        List<Class<?>> classes = Lists.newLinkedList();
        try {
            //获取包的名字 并进行替换
            String packageDirName = packageName.replace('.', '/');
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //判断是否以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("MnsMessageCallbackScanner#getClassesByPackageName scanning files");
                    }
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, classes);
                }else if("jar".equals(protocol)){
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("MnsMessageCallbackScanner#getClassesByPackageName scanning jars");
                    }
                    //获取包的物理路径
                    findAndAddClassesInPackageByJar(packageName, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void findAndAddClassesInPackageByJar(String packageName, List<Class<?>> classes) {
        String pathName = packageName.replace(".", "/");

        JarFile jarFile  = null;
        try {
            URL url = CustomerScanner.class.getClassLoader().getResource(pathName);
            if(url == null){
                return;
            }
            JarURLConnection jarURLConnection  = (JarURLConnection )url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            if(jarEntryName.contains(pathName) && !jarEntryName.equals(pathName+"/")){
                //递归遍历子目录
                if(jarEntry.isDirectory()){
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    findAndAddClassesInPackageByJar(prefix, classes);
                }
                if(jarEntryName.endsWith(CLASSFILE_NAME_SUFFIX)){
                    try {
                        String className = jarEntry.getName().replace("/", ".").replace(CLASSFILE_NAME_SUFFIX, "");
                        if(LOGGER.isDebugEnabled()){
                            LOGGER.debug("MnsMessageCallbackScanner#findAndAddClassesInPackageByJar loading class "+className);
                        }
                        classes.add(CustomerScanner.class.getClassLoader().loadClass(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, List<Class<?>> classes){

        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles((file)->{
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            return (file.isDirectory()) || (file.getName().endsWith(CLASSFILE_NAME_SUFFIX));
        });
        //循环所有文件
        for (File file : dirfiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().replace(CLASSFILE_NAME_SUFFIX, "");
                try {
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("MnsMessageCallbackScanner#findAndAddClassesInPackageByFile loading class "+className);
                    }
                    //添加到集合中去
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
