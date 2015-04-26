package hxws.generator.processor;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import hxws.generator.annotations.findview;
import hxws.generator.annotations.lifecycle.afterInject;
import hxws.generator.annotations.lifecycle.onDestroy;
import hxws.generator.annotations.lifecycle.onPause;
import hxws.generator.annotations.lifecycle.onResume;
import hxws.generator.annotations.lifecycle.onStart;
import hxws.generator.annotations.lifecycle.onStop;
import hxws.generator.annotations.onClick;
import hxws.generator.annotations.onItemClick;
import hxws.generator.annotations.onItemLongClick;
import hxws.generator.annotations.onLongClick;
import hxws.generator.annotations.setLayout;
import hxws.generator.generation.Listener;
import hxws.generator.generation.ViewGenerator;


/**
 * @author 苏海双
 * Created by suhaishuang
 */
@SupportedAnnotationTypes("hxws.generator.annotations.*")
public final class GeneratorProcessor extends AbstractProcessor{

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    private static final List<Class<? extends Annotation>> LISTENERS = Arrays.asList(
    onClick.class,
    onItemClick.class,
    onLongClick.class,
    onItemLongClick.class
    );

    private static final List<Class<? extends Annotation>> LIFELISTENERS = Arrays.asList(
    onStart.class,
    onResume.class,
    onPause.class,
    onStop.class,
    onDestroy.class
    );

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        HashMap<TypeElement, ViewGenerator> targetMap = findTarget(roundEnv);
        for(Map.Entry<TypeElement, ViewGenerator> entrySet:targetMap.entrySet()){
            TypeElement typeElement = entrySet.getKey();
           ViewGenerator generator =entrySet.getValue();
            try {
                JavaFileObject jfo = filer.createSourceFile(generator.getPackageClass(), typeElement);
                Writer writer = jfo.openWriter();
                writer.write(generator.generateSource(typeElement.getSuperclass().toString()));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private HashMap<TypeElement, ViewGenerator> findTarget(RoundEnvironment roundEnv){
        HashMap<TypeElement, ViewGenerator> targetMap = new LinkedHashMap<TypeElement, ViewGenerator>();
        findLayout(targetMap, roundEnv);
        findView(targetMap, roundEnv);
        try {
            findListener(targetMap, roundEnv);
        }catch (Exception e){

        }
        findAfter(targetMap,roundEnv);
        findLifeCycle(targetMap,roundEnv);
        return targetMap;
    }

    private void findLayout(Map<TypeElement, ViewGenerator> targetMap,RoundEnvironment roundEnv){
        for(Element element : roundEnv.getElementsAnnotatedWith(setLayout.class)){
            int id = element.getAnnotation(setLayout.class).value();
            ViewGenerator generator = getOrCreateGenerator(targetMap,(TypeElement)element);
            generator.setLayoutId(id);
        }
    }

    private void findView(Map<TypeElement, ViewGenerator> targetMap,RoundEnvironment roundEnv){
        for(Element element : roundEnv.getElementsAnnotatedWith(findview.class)){
            if(isPrivate(element)){
                error(element.getSimpleName()+" 不能为private,请修正");
            }
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            TypeMirror typeMirror = element.asType();
            if(!isSubType(typeMirror,"android.view.View")){
                error("使用注释@findview "+element.getSimpleName()+" 必须继承view");
            }
            int id = element.getAnnotation(findview.class).value();
            String type = typeMirror.toString();
            ViewGenerator generator = getOrCreateGenerator(targetMap,enclosingElement);
            generator.addViewAttr(id,element.toString(),type);
        }
    }
    private void findListener(Map<TypeElement, ViewGenerator> targetMap, RoundEnvironment roundEnv) throws Exception{
        for(Class<? extends Annotation> annotationClass : LISTENERS){
            for(Element element : roundEnv.getElementsAnnotatedWith(annotationClass)){
                if(isPrivate(element)){
                    error(element.getSimpleName()+" 不能为private,请修正");
                }
                TypeElement enclosingElement = (TypeElement)element.getEnclosingElement();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"注释的方法:"+element.getSimpleName()+",修饰符:"+element.getModifiers().toString());
                Annotation annotation = element.getAnnotation(annotationClass);
                Method method = annotationClass.getMethod("value");
                int id = (int)method.invoke(annotation);
                Listener listener = new Listener(element.getSimpleName().toString(),annotationClass);
                ViewGenerator generator = getOrCreateGenerator(targetMap,enclosingElement);
                generator.addListener(id, listener);
            }
        }
    }

    private void findAfter(Map<TypeElement, ViewGenerator> targetMap, RoundEnvironment roundEnv){
        for(Element element : roundEnv.getElementsAnnotatedWith(afterInject.class)){
            if(isPrivate(element)){
                error(element.getSimpleName()+" 不能为private,请修正");
            }
            TypeElement enclosingElement = (TypeElement)element.getEnclosingElement();
            ViewGenerator generator = getOrCreateGenerator(targetMap,enclosingElement);
            Listener listener = new Listener(element.getSimpleName().toString(), afterInject.class);
            generator.setAfter(listener);
        }
    }

    private void findLifeCycle(Map<TypeElement, ViewGenerator> targetMap, RoundEnvironment roundEnv){
        for(Class<? extends Annotation> annotationClass : LIFELISTENERS){
            for(Element element : roundEnv.getElementsAnnotatedWith(annotationClass)){
                if(isPrivate(element)){
                    error(element.getSimpleName()+" 不能为private,请修正");
                }
                TypeElement enclosingElement = (TypeElement)element.getEnclosingElement();
                Listener listener = new Listener(element.getSimpleName().toString(),annotationClass);
                ViewGenerator generator = getOrCreateGenerator(targetMap,enclosingElement);
                generator.addLifeListenner(listener);
            }
        }
    }

    private boolean isSubType(TypeMirror typeMirror,String superClass){
        if(typeMirror.toString().equals(superClass)){
            return true;
        }
        for(TypeMirror t :typeUtils.directSupertypes(typeMirror)){
            if(isSubType(t,superClass)){
                return true;
            }
        }
        return false;
    }

    private ViewGenerator getOrCreateGenerator(Map<TypeElement, ViewGenerator> targetMap,TypeElement encloseElement){
        ViewGenerator generator = targetMap.get(encloseElement);
        if(generator == null){
            String packageName = getPackageName(encloseElement);
            String className = getClassName(encloseElement,packageName);
            generator = new ViewGenerator(packageName,className);
            targetMap.put(encloseElement,generator);
        }
        return  generator;
    }

    private boolean isPrivate(Element element){
        Set<Modifier> modifiers = element.getModifiers();
        return modifiers.contains(Modifier.PRIVATE);
    }

    private void error(String massage){
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,massage);
    }

    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen);
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }
}
