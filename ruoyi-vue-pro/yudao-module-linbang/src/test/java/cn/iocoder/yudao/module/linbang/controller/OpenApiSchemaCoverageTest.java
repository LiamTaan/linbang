package cn.iocoder.yudao.module.linbang.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiSchemaCoverageTest {

    private static final String CONTROLLER_PACKAGE = "cn.iocoder.yudao.module.linbang.controller";

    @Test
    void allControllerVoTypesAndFieldsHaveSchemaDescriptions() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<String> classNames = findClassNames(classLoader, CONTROLLER_PACKAGE);
        List<String> violations = new ArrayList<>();
        int checkedTypes = 0;
        int checkedFields = 0;
        for (String className : classNames) {
            Class<?> type = Class.forName(className, false, classLoader);
            if (!isVoType(type)) {
                continue;
            }
            checkedTypes++;
            Schema typeSchema = type.getAnnotation(Schema.class);
            if (typeSchema == null || isBlank(typeSchema.description())) {
                violations.add(type.getName() + " is missing a class-level @Schema description");
            }
            for (Field field : type.getDeclaredFields()) {
                if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                checkedFields++;
                Schema fieldSchema = field.getAnnotation(Schema.class);
                if (fieldSchema == null || isBlank(fieldSchema.description())) {
                    violations.add(type.getName() + "#" + field.getName()
                            + " is missing a field-level @Schema description");
                }
            }
        }
        Collections.sort(violations);
        assertTrue(checkedTypes > 0, "No controller VO types were discovered");
        assertTrue(checkedFields > 0, "No controller VO fields were discovered");
        assertTrue(violations.isEmpty(), String.join(System.lineSeparator(), violations));
    }

    private static Set<String> findClassNames(ClassLoader classLoader, String packageName) throws Exception {
        String packagePath = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        Set<String> classNames = new HashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (!"file".equals(resource.getProtocol())) {
                continue;
            }
            URI uri = resource.toURI();
            Path packageDirectory = Paths.get(uri);
            try (Stream<Path> files = Files.walk(packageDirectory)) {
                files.filter(Files::isRegularFile)
                        .filter(file -> file.getFileName().toString().endsWith(".class"))
                        .forEach(file -> classNames.add(toClassName(packageName, packageDirectory, file)));
            }
        }
        return classNames;
    }

    private static String toClassName(String packageName, Path packageDirectory, Path classFile) {
        String relativeName = packageDirectory.relativize(classFile).toString();
        String classSuffix = relativeName.substring(0, relativeName.length() - ".class".length())
                .replace(File.separatorChar, '.');
        return packageName + "." + classSuffix;
    }

    private static boolean isVoType(Class<?> type) {
        if (!type.getName().contains(".vo.") || type.isAnnotation() || type.isEnum()
                || type.isInterface() || type.isSynthetic() || type.getSimpleName().endsWith("Builder")) {
            return false;
        }
        Class<?> outermost = type;
        while (outermost.getEnclosingClass() != null) {
            outermost = outermost.getEnclosingClass();
        }
        return outermost.getSimpleName().endsWith("VO");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
