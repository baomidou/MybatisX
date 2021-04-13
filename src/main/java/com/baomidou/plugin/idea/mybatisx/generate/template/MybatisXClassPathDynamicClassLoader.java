package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.util.ClassCreator;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.itranswarp.compiler.JavaStringCompiler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MybatisXClassPathDynamicClassLoader extends ClassLoader {
    private PsiClass psiClass;

    public MybatisXClassPathDynamicClassLoader(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.equals(psiClass.getQualifiedName())) {

            ClassCreator classCreator = new ClassCreator();
            final Set<String> allowedFields = Arrays.stream(psiClass.getAllFields())
                // 过滤静态字段, 主要是为了过滤序列化的字段
                .filter(field -> !field.hasModifierProperty(PsiModifier.STATIC))
                .map(PsiField::getName).collect(Collectors.toSet());
            String javaSourceCode = classCreator.defineClass(allowedFields, psiClass, psiClass.getName());

            try {
                JavaStringCompiler compiler = new JavaStringCompiler();
                Map<String, byte[]> results = compiler.compile(psiClass.getName() + ".java", javaSourceCode);
                return compiler.loadClass(psiClass.getQualifiedName(), results);
            } catch (IOException e) {
                throw new ClassNotFoundException("无法创建类", e);
            }
        }
        return super.findClass(name);
    }

}
