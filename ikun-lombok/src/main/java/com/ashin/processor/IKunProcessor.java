package com.ashin.processor;

import com.ashin.annotation.IKun;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.ashin.annotation.IKun")
public class IKunProcessor extends BaseProcessor {

    /**
     * 对 AST 进行处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 获取被自定义 IKun 注解修饰的元素
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(IKun.class);

        set.forEach(element -> {
            // 根据元素获取对应的语法树 JCTree
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {

                    // 获取 IKun 注解的值
                    String iKunValue = jcMethodDecl.getModifiers().getAnnotations().stream()
                            .filter(annotation -> annotation.getAnnotationType().toString().equals(IKun.class.getName()))
                            .findFirst()
                            .map(annotation -> {
                                JCTree.JCExpression valueExpression = getAnnotationValue(annotation, "value");
                                if (valueExpression instanceof JCTree.JCLiteral) {
                                    JCTree.JCLiteral literal = (JCTree.JCLiteral) valueExpression;
                                    return literal.getValue().toString();
                                }
                                return null;
                            })
                            .orElse("获取IKun注解value失败");

                    // 添加输出语句
                    JCTree.JCStatement printIKun = treeMaker.Exec(
                            treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("out")), names.fromString("println")),
                                    List.of(treeMaker.Literal(iKunValue)))
                    );
                    jcMethodDecl.body.stats = jcMethodDecl.body.stats.append(printIKun);

                    super.visitMethodDef(jcMethodDecl);
                }
            });
        });
        return true;
    }

    // 辅助方法：获取注解的属性值
    private JCTree.JCExpression getAnnotationValue(JCTree.JCAnnotation annotation, String attributeName) {
        for (JCTree.JCExpression arg : annotation.getArguments()) {
            if (arg instanceof JCTree.JCAssign) {
                JCTree.JCAssign assign = (JCTree.JCAssign) arg;
                if (assign.getVariable().toString().equals(attributeName)) {
                    return assign.getExpression();
                }
            }
        }
        return null;
    }
}
