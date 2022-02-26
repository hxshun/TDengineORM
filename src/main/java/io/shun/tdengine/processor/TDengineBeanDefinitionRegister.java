package io.shun.tdengine.processor;

import io.shun.tdengine.annotation.TDengineRepository;
import io.shun.tdengine.constants.TDengineStatementContent;
import io.shun.tdengine.handler.TDengineMapperFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;


import org.springframework.util.ClassUtils;


import java.io.IOException;
import java.util.*;

/**
 * @ClassName: TDengineBeanDefinitionRegister
 * @Description: 扫描TDengineRepository注解的Mapper 注入到spring中
 * @Author: ShunZ
 * @CreatedAt: 2021-12-27 11:04
 * @Version: 1.0
 **/
@Component
public class TDengineBeanDefinitionRegister implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware,ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;



    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        Set<Class<?>> beanClasses = scannerMapperClass();
        // 动态代理实现接口 并注册到spring中
        if (!TDStatementTool.isEmpty(beanClasses)) {
            for (Class clazz : beanClasses) {
                // 构建Bean
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

                // 设置对象构造方法的参数
                definition.getConstructorArgumentValues().addGenericArgumentValue(clazz);

                // 提供代理对象的工厂
                definition.setBeanClass(TDengineMapperFactory.class);

                // 注入Bean
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                beanDefinitionRegistry.registerBeanDefinition(clazz.getSimpleName(), definition);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);

    }



    /**
     * 扫描指定包下的Mapper类
     * @return
     */
    private Set<Class<?>> scannerMapperClass() {
        Set<Class<?>> set = new LinkedHashSet<>();
        Environment environment = applicationContext.getEnvironment();
        String mapperPackage = environment.getProperty("tdengine.mapperPackage");

        if (!TDStatementTool.isEmpty(mapperPackage)) { // 没有配置包路径不处理
            // 扫描路径
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(mapperPackage)) +
                    TDengineStatementContent.DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        Class<?> clazz;
                        try {
                            clazz = Class.forName(className);
                            TDengineRepository annotation = AnnotationUtils.getAnnotation(clazz, TDengineRepository.class);
                            if (!TDStatementTool.ifNull(annotation)){
                                set.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return set;
    }
}
