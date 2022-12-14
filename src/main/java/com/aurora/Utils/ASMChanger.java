package com.aurora.Utils;


import com.aurora.SupportType.MyDIYType;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM6;

public class ASMChanger {

    public static class ModifyAccessVisitor extends ClassVisitor {
        public String Classname;
        private String Path;
        private String Content;
        private MyDIYType Template2Int;

        public ModifyAccessVisitor(int i, ClassVisitor classVisitor, MyDIYType Template2Int, String ClassName, String Path, String Content) {
            super(i, classVisitor);
            Classname = ClassName;
            this.Path = Path;
            this.Content = Content;
            this.Template2Int = Template2Int;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            switch(Template2Int){
                case PutFile:
                    if (StringUtils.equals(name,"Contentbase64"))
                        return super.visitField(access, name, descriptor, signature, Content);
                    if (StringUtils.equals(name,"Path"))
                        return super.visitField(access, name, descriptor, signature, Path);
                    break;
                case commandecho:
                    if (StringUtils.equals(name,"cmd"))
                        return super.visitField(access, name, descriptor, signature, Content);
                    if (StringUtils.equals(name,"uri"))
                        return super.visitField(access, name, descriptor, signature, Path);
                    break;
            }
            return super.visitField(access, name, descriptor, signature, value);
        }
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (Classname.equals("")){
               return super.visitMethod(access,name,descriptor,signature,exceptions);
            }
            MyMethodVisitor mv2 = new MyMethodVisitor(ASM6,super.visitMethod(access,name,descriptor,signature,exceptions),Classname);
            return mv2;
        }
        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            super.visitInnerClass(name,outerName,innerName,access);
        }
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (Classname.equals("")){
              super.visit(version,access,name,signature,superName,interfaces);
            }else {
                //Change Class Name
                super.visit(version,access,Classname,signature,superName,interfaces);
            }

        }
    }
    public static class MyMethodVisitor extends MethodVisitor {
        private String Classname;
        public MyMethodVisitor(int i, MethodVisitor methodVisitor,String Classname) {
            super(i, methodVisitor);
            this.Classname = Classname;
        }
        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode,Classname,name,descriptor);
        }
    }

}
