package me.vicasong.autoconfig.aot;

import org.springframework.aot.context.bootstrap.generator.bean.descriptor.BeanInstanceDescriptor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.DefaultNativeReflectionEntry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


/**
 * AOT Hits for BeanDefinition
 *
 * @author vicasong
 * @since 2022-08-04 15:14
 */
public class BeanAOTFixConfiguration implements BeanNativeConfigurationProcessor {


    @Override
    public void process(BeanInstanceDescriptor descriptor, NativeConfigurationRegistry registry) {
        Class<?> userBeanClass = descriptor.getUserBeanClass();
        ReflectionUtils.doWithFields(userBeanClass, f -> {
            Annotation[] annotations = f.getDeclaredAnnotations();
            boolean read = false;
            boolean write = false;
            for (Annotation annotation : annotations) {
                // validator framework
                if (annotation.annotationType().getName().contains("valid")) {
                    read = true;
                } else if (annotation.annotationType() == Resource.class ||
                annotation.annotationType().getName().startsWith("picocli")) {
                    // @Resource support
                    write = true;
                    break;
                }
            }
            if (write || read) {
                registry.reflection().forType(f.getDeclaringClass())
                        .withField(f, DefaultNativeReflectionEntry.FieldAccess.ALLOW_WRITE);
            }
        });
        ReflectionUtils.doWithMethods(userBeanClass, m -> {
            if (m.getAnnotation(Autowired.class) != null ||
            m.getAnnotation(Resource.class) != null) {
                // Declare methods with @Autowired or @Resource
                registry.reflection()
                        .forType(userBeanClass)
                        .withExecutables(m);
            }
        }, m -> Modifier.isPublic(m.getModifiers()));
        Constructor<?>[] constructors = userBeanClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() < 1) {
                break;
            }
            // Bean's constructor
            registry.reflection()
                    .forType(userBeanClass)
                    .withExecutables(constructor);
        }
    }
}
