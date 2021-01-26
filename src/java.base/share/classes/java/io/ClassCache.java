package java.io;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

final class ClassCache {

    private final static ConcurrentHashMap<ClassCacheKey, Class<?>> classCache = new ConcurrentHashMap<>();

    static Class<?> forName(String name, ClassLoader classLoader,
                            BiFunction<String, ClassLoader, Class<?>> load)
            throws ClassNotFoundException {
        ClassCacheKey key = new ClassCacheKey(classLoader, name);
        Class<?> clazz = classCache.computeIfAbsent(key, k -> {
            Class<?> cl = load.apply(name, classLoader);
            return cl == null ? NOT_EXIST.class : cl;
        });
        if (NOT_EXIST.class == clazz) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    private static class ClassCacheKey {
        private final ClassLoader classLoader;
        private final String name;

        ClassCacheKey(ClassLoader classLoader, String name) {
            this.classLoader = classLoader;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassCacheKey that = (ClassCacheKey) o;
            return Objects.equals(classLoader, that.classLoader) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classLoader, name);
        }
    }

    private static class NOT_EXIST {
    }
}
